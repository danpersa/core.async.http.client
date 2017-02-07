(ns core.async.http.client.async-http
  (:refer-clojure :exclude [get])
  (:require [core.async.http.protocols :as proto]
            [core.async.http.client :as c]
            [core.async.http.utils :as utils]
            [clojure.core.async :as async :refer [go chan >!! <!!]]
            [clojure.core.match :as m]
            [clojure.tools.logging :as log])
  (:import (org.asynchttpclient
             DefaultAsyncHttpClient
             AsyncHandler
             HttpResponseBodyPart
             HttpResponseStatus
             HttpResponseHeaders
             AsyncHandler$State
             AsyncHttpClient
             RequestBuilderBase
             BoundRequestBuilder)
           (io.netty.handler.codec.http HttpHeaders)))

(def default-client (DefaultAsyncHttpClient.))

(def state {:continue AsyncHandler$State/CONTINUE
            :abort    AsyncHandler$State/ABORT
            :upgrade  AsyncHandler$State/UPGRADE})

(defn- convert-headers [^HttpResponseHeaders headers]
  (let [^HttpHeaders http-headers (.getHeaders headers)]
    (->> (.names http-headers)
         (map (fn [header-name]
                (let [header-values (.getAll http-headers header-name)
                      value (if (= 1 (count header-values))
                              (first header-values)
                              (vec header-values))]
                  [header-name value])))
         (into {}))))

(comment
  "Takes as a parameter a map with the following keys:
  [:status-callback :headers-callback :body-part-callback :completed-callback]")
(deftype BasicAsyncHandler
  [callbacks]
  AsyncHandler
  (^void onThrowable [this ^Throwable throwable]
    (log/error throwable "There was an error")
    (((callbacks :error-callback) this throwable)))
  (^AsyncHandler$State onStatusReceived [this ^HttpResponseStatus status]
    ;(log/debug "Got status" (str status))
    ((callbacks :status-callback) this (.getStatusCode status)))
  (^AsyncHandler$State onHeadersReceived [this ^HttpResponseHeaders headers]
    ;(log/debug "Got headers" (str headers))
    ((callbacks :headers-callback) this (convert-headers headers)))
  (^AsyncHandler$State onBodyPartReceived [this ^HttpResponseBodyPart bodypart]
    ;(log/debug "Got body part")
    ((callbacks :body-part-callback) this (.getBodyPartBytes bodypart)))
  (onCompleted [this]
    (log/debug "Completed")
    ((callbacks :completed-callback) this)))

(defn default-callback [_ _]
  (state :continue))

(defn default-completed-callback [_])
(defn default-error-callback [_ _])

(defn create-basic-handler
  "Takes as a parameter a map with the following keys as optionals:
  [:status-callback :headers-callback :body-part-callback :completed-callback :error-callback]"
  [callbacks]

  (let [callbacks-with-default
        (-> callbacks
            (utils/assoc-if-new :status-callback default-callback)
            (utils/assoc-if-new :headers-callback default-callback)
            (utils/assoc-if-new :body-part-callback default-callback)
            (utils/assoc-if-new :error-callback default-error-callback)
            (utils/assoc-if-new :completed-callback default-completed-callback))]
    (log/debug "We have new callbacks" callbacks-with-default)
    (->BasicAsyncHandler callbacks-with-default)))

(defn create-core-async-handler
  ""
  [{:keys [status-chan headers-chan body-chan error-chan] :as chans}]
  (log/debug "Create core async handler with chans" chans)

  (let [close-all-channels (fn []
                             (when body-chan
                               (log/debug "Close body part chan")
                               (async/close! body-chan))
                             (when status-chan
                               (async/close! status-chan))
                             (when headers-chan
                               (async/close! headers-chan))
                             (when error-chan
                               (async/close! error-chan)))
        callbacks (-> {}
                      (utils/assoc-if (fn [_ _] (some? status-chan))
                                      :status-callback
                                      (fn [this status-code]
                                        (>!! status-chan status-code)
                                        (state :continue)))
                      (utils/assoc-if (fn [_ _] (some? headers-chan))
                                      :headers-callback
                                      (fn [this headers]
                                        (>!! headers-chan headers)
                                        (state :continue)))
                      (utils/assoc-if (fn [_ _] (some? body-chan))
                                      :body-part-callback
                                      (fn [this body-part]
                                        (if (not (empty? body-part))
                                          (>!! body-chan (String. body-part "UTF-8")))
                                        (state :continue)))
                      (utils/assoc-if (fn [_ _] (some? error-chan))
                                      :error-callback
                                      (fn [this ^Throwable error]
                                        (if (instance? java.util.concurrent.TimeoutException error)
                                          (>!! error-chan :timeout)
                                          (>!! error-chan :error))
                                        (close-all-channels)))
                      (assoc :completed-callback (fn [this]
                                                   (log/debug "Completed callback")
                                                   (close-all-channels))))]
    (log/debug "We have the callbacks" callbacks)
    (create-basic-handler callbacks)))

(defn- add-headers! [^RequestBuilderBase request-builder headers]
  (when headers
    (doseq [header (seq headers)]
      (let [header-name (first header)
            header-values (last header)]
        ; Use a multimethod here
        (if (instance? String header-values)
          (.addHeader request-builder header-name header-values)
          (doseq [header-value header-values]
            (.addHeader request-builder header-name header-value)))))))

(def client
  (reify proto/Client
    (request! [_ {:keys [^AsyncHttpClient client
                         url
                         method
                         body
                         status-chan
                         headers-chan
                         body-chan
                         error-chan
                         timeout
                         headers] :as options}]

      (let [cl (or client default-client)
            chans {:status-chan  (or status-chan (chan 1))
                   :headers-chan (or headers-chan (chan 1))
                   :body-chan    (or body-chan (chan 1024))
                   :error-chan   (or error-chan (chan 1))}
            ^BoundRequestBuilder request-builder (BoundRequestBuilder.
                                                   cl
                                                   (c/convert-method-name method)
                                                   false)]
        (log/debug "Doing a call to" url)

        (.setUrl request-builder url)

        (when timeout
          (.setRequestTimeout request-builder timeout))

        (when body
          (.setBody request-builder body))

        (add-headers! request-builder headers)

        (.execute
          request-builder (create-core-async-handler chans))
        {:status  (chans :status-chan)
         :headers (chans :headers-chan)
         :body    (chans :body-chan)
         :error   (chans :error-chan)}))
    (sync-request! [this options]
      (let [out-chan (chan 1024)
            error-chan (chan 1)]

        (proto/request! this (merge
                               options
                               {:status-chan  out-chan
                                :headers-chan out-chan
                                :body-chan    out-chan
                                :error-chan   error-chan}))

        (let [error-or-status (async/alts!! [out-chan error-chan] :priority true)]
          (m/match [error-or-status]
                   [[status out-chan]]
                   (do
                     (log/debug "Status" status)
                     (let [headers (<!! out-chan)]
                       (log/debug "Headers" headers)
                       (loop [body-part (<!! out-chan)
                              result ""]
                         (log/debug "Got part" body-part)
                         (if body-part
                           (recur (<!! out-chan) (str result body-part))
                           {:status  status
                            :headers headers
                            :body    result}))))
                   [[ex error-chan]]
                   (do
                     (log/error ex "Error")
                     {:error ex})))))))

(def request (partial c/request client))

(def sync-request (partial c/sync-request client))

(def get (partial c/get client))

(def sync-get (partial c/sync-get client))

(def post (partial c/post client))

(def sync-post (partial c/sync-post client))

(comment
  (let [{:keys [body-chan]} (get "http://www.example.com" {:client default-client})]
    (async/go-loop []
      (when-some [body-part (String. (<! body-chan))]
        (log/debug "out of body" body-part)))))


(comment
  (let [{:keys [body-chan]} (get "http://www.example.com" {:client default-client})]
    (async/go-loop []
      (when-some [body-part (String. (<! body-chan))]
        (log/debug "out of body" body-part)))))
