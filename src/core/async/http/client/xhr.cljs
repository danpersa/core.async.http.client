(ns core.async.http.client.xhr
  (:refer-clojure :exclude [get])
  (:require [goog.net.XhrIo :as xhr]
            [goog.events :as events]
            [cljs.core.async :refer [chan close! >! <!]]
            [core.async.http.client :as c]
            [core.async.http.protocols :as proto])
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:import [goog.net ErrorCode EventType XhrIo]))

(defn close-chans [chans]
  (close! (chans :status-chan))
  (close! (chans :headers-chan))
  (close! (chans :body-chan))
  (close! (chans :error-chan)))

(def client
  (reify proto/Client
    (request! [_ {:keys [url
                         method
                         body
                         status-chan
                         headers-chan
                         body-chan
                         error-chan
                         timeout
                         headers] :as options}]

      (let [chans {:status-chan  (or status-chan (chan 1))
                   :headers-chan (or headers-chan (chan 1))
                   :body-chan    (or body-chan (chan 1024))
                   :error-chan   (or error-chan (chan 1))}
            xhrio (XhrIo.)]

        (comment (.setProgressEventsEnabled xhrio true))

        (events/listen xhrio EventType.TIMEOUT
                       (fn [event]
                         ; (println "Event type: " (.-type event))
                         (go
                           (>! (chans :error-chan) :timeout)
                           (close-chans chans))))
        (events/listen xhrio EventType.ERROR
                       (fn [event]
                         ; (println "Event type: " (.-type event))
                         (let [response (-> event
                                            .-target)
                               body (.getResponse response)
                               headers (-> (.getResponseHeaders response)
                                           (js->clj))
                               status (.getStatus response)]
                           (go
                             (>! (chans :status-chan) status)
                             (>! (chans :headers-chan) headers)
                             (>! (chans :body-chan) body)
                             (>! (chans :error-chan) :error)
                             (close-chans chans)))))
        (comment
          (events/listen xhrio EventType.PROGRESS
                         (fn [event]
                           (println "Event type: " (.-type event) " " (.-loaded event) " " (.-total event))))
          (events/listen xhrio EventType.ABORT
                         (fn [event]
                           (println "Event type: " (.-type event)))))
        (events/listen xhrio EventType.SUCCESS
                       (fn [event]
                         ; (println "Event type: " (.-type event))
                         (let [response (-> event
                                            .-target)
                               body (.getResponse response)
                               headers (-> (.getResponseHeaders response)
                                           (js->clj))
                               status (.getStatus response)]
                           (go
                             (>! (chans :status-chan) status)
                             (>! (chans :headers-chan) headers)
                             (>! (chans :body-chan) body)
                             (close-chans chans)))))
        (comment
          (events/listen xhrio EventType.COMPLETE
                         (fn [event]
                           (println "Event type: " (.-type event)))))
        (.setTimeoutInterval xhrio timeout)
        (.send xhrio
               url
               (c/convert-method-name method)
               body
               (if headers (clj->js headers) #js {}))

        {:status  (chans :status-chan)
         :headers (chans :headers-chan)
         :body    (chans :body-chan)
         :error   (chans :error-chan)}))

    (sync-request! [this options]
      nil)))


(def request (partial c/request client))

(def get (partial c/get client))

(def post (partial c/post client))
