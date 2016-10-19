(ns core.async.http.client.xhr
  (:refer-clojure :exclude [get])
  (:require [goog.net.XhrIo :as xhr]
            [cljs.core.async :refer [chan close! >! <!]]
            [core.async.http.client :as c]
            [core.async.http.protocols :as proto])
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:import [goog.net ErrorCode EventType]))

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
                   :error-chan   (or error-chan (chan 1))}]
        (xhr/send
          url
          (fn [event]
            (condp = (.-type event)
              EventType.COMPLETE
              (let [response (-> event
                                 .-target)
                    body (.getResponse response)
                    headers (-> (.getResponseHeaders response)
                                (js->clj))
                    status (.getStatus response)]
                (go
                  (>! (chans :status-chan) status)
                  (>! (chans :headers-chan) headers)
                  (>! (chans :body-chan) body)))
              EventType.TIMEOUT
              (do
                (close! (chans :status-chan))
                (close! (chans :headers-chan))
                (close! (chans :body-chan))
                (go (>! (chans :error-chan) :timeout)))))
          (c/convert-method-name method)
          body
          (clj->js headers)
          timeout)
        {:status  (chans :status-chan)
         :headers (chans :headers-chan)
         :body    (chans :body-chan)
         :error   (chans :error-chan)}))

    (sync-request! [this options]
      nil)))


(def request (partial c/request client))

(def get (partial c/get client))

(def post (partial c/post client))
