(ns core.async.http.client
  (:require [clojure.core.async :as async :refer [>!! <!! >! <! chan close!]]
            [core.async.http.utils :as utils]
            [clojure.tools.logging :as log]
            [clojure.core.match :as m])
  (:import (org.asynchttpclient
             DefaultAsyncHttpClient
             AsyncHandler
             HttpResponseBodyPart
             HttpResponseStatus
             HttpResponseHeaders
             AsyncHandler$State)
           (io.netty.handler.codec.http DefaultHttpHeaders HttpHeaders)))


(def client (DefaultAsyncHttpClient.))

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
                             (when (some? body-chan)
                               (log/debug "Close body part chan")
                               (close! body-chan))
                             (when (some? status-chan)
                               (close! status-chan))
                             (when (some? headers-chan)
                               (close! headers-chan))
                             (when (some? error-chan)
                               (close! error-chan)))
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
                                        (>!! body-chan body-part)
                                        (state :continue)))
                      (utils/assoc-if (fn [_ _] (some? error-chan))
                                      :error-callback
                                      (fn [this ^Throwable error]
                                        (>!! error-chan error)
                                        (close-all-channels)))
                      (assoc :completed-callback (fn [this]
                                                   (log/debug "Completed callback")
                                                   (close-all-channels))))]
    (log/debug "We have the callbacks" callbacks)
    (create-basic-handler callbacks)))

(defn sync-get
  ([url] (sync-get client url))
  ([client url]
   (let [out-chan (chan 1024)
         error-chan (chan 1)]
     (.execute
       (.prepareGet client url)
       (create-core-async-handler
         {:status-chan  out-chan
          :headers-chan out-chan
          :body-chan    out-chan
          :error-chan   error-chan}))
     (let [error-or-status (async/alts!! [error-chan out-chan])]
       (m/match [error-or-status]
                [[status status-chan]]
                (do
                  (log/debug "Status" status)
                  (let [headers (<!! out-chan)]

                    (loop [body-part (<!! out-chan)
                           result ""]
                      (if (some? body-part)
                        (recur (<!! out-chan) (str result (String. body-part "UTF-8")))
                        {:status  status
                         :headers headers
                         :body    result}))))
                [[ex error-chan]] (do
                                    (log/error ex "Error")
                                    nil))))))

(comment
  (sync-get client "http://www.example.com")
  (sync-get client "http://localhost:8083/endpoint-1")
  (sync-get client "http://www.theuselessweb.com/"))

(defn basic-get [client url & options]
  (.execute
    (.prepareGet client url) (create-basic-handler {})))

(defn channel-get [client url & options]
  (let [body-chan (chan 1024)]
    (.execute
      (.prepareGet client url) (create-core-async-handler {:body-chan body-chan}))
    {:body-chan body-chan}))

(comment
  (basic-get client "http://www.example.com"))

(comment
  (let [{:keys [body-chan]} (get client "http://www.example.com")]
    (async/go-loop []
      (when-some [body-part (String. (<! body-chan))]
        (log/debug "out of body" body-part)))))


(comment
  (let [{:keys [body-chan]} (channel-get client "http://www.example.com")]
    (async/go-loop []
      (when-some [body-part (String. (<! body-chan))]
        (log/debug "out of body" body-part)))))
