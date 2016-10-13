(ns core.async.http.client.xhr
  (:require [goog.net.XhrIo :as xhr]
            [cljs.core.async :refer [chan close! >! <!]])
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:import [goog.net ErrorCode EventType]))

(defn request [url]
  (let [headers-chan (chan 1)
        status-chan (chan 1)
        body-chan (chan 1)
        error-chan (chan 1)]
    (xhr/send
      url
      (fn [event]
        (.log js/console (str "Event: " event))
        (condp = (.-type event)
          EventType.COMPLETE
          (let [response (-> event
                             .-target)
                body (.getResponse response)
                headers (-> (.getResponseHeaders response)
                            (js->clj))
                status (.getStatus response)]
            (go
              (>! status-chan status)
              (>! headers-chan headers)
              (>! body-chan body))
            ;(.log js/console (str "headers: " headers " status:" status "!"))
            )
          EventType.TIMEOUT
          (do
            (close! status-chan)
            (close! headers-chan)
            (close! body-chan)
            (go (>! error-chan :timeout))))))
    {:status  status-chan
     :headers headers-chan
     :body    body-chan
     :error   error-chan}))

(defn main []
  (enable-console-print!)
  (prn "Hello, World!")
  (let [response (request "http://httpbin.org/")]
    (go
      (.log js/console (str "st: " (<! (response :status))))
      (.log js/console (str "he: " (<! (response :headers)))))))

(main)