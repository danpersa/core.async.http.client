(ns core.async.http.client.node
  (:refer-clojure :exclude [get])
  (:require [cljs.nodejs :as node]))



(def ^:private http (node/require "http"))
(def ^:private https (node/require "https"))
(def ^:private url (node/require "url"))
(def ^:private querystring (node/require "querystring"))

(def options {:hostname "localhost"
              :port     8087
              :method   "GET"})

(defn sample-request [done]
  (println "sample request")
  (let [req (.request
              http (clj->js options)
              (fn [res]
                (.on res "data"
                     (fn [chunk]
                       (println "got something " chunk)
                       ))
                (.on res "end"
                     (fn []
                       (println "end request")
                       (done)))
                (.on res "close"
                     (fn []
                       (println "close request")
                       (done)))
                (.on res "error"
                     (fn [err]
                       (println "got some err " err)
                       (done)))))]
    (.end req)))

(defn adder [x y]
  (+ x y))

;
;(defn -main [& args]
;  (println "Hello world!"))
;
;(set! *main-cli-fn* -main)
