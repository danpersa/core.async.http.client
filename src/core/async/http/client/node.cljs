(ns core.async.http.client.node
  (:refer-clojure :exclude [get])
  (:require [cljs.nodejs :as node]
            ))

(node/enable-util-print!)


(def ^:private http (node/require "http"))
(def ^:private https (node/require "https"))
(def ^:private url (node/require "url"))
(def ^:private querystring (node/require "querystring"))

(def options {:hostname "localhost"
              :port     8087
              :method   "GET"})

(defn sample-request [done]
  (println "sample request")
  (.request http (clj->js options)
            (fn [res]
              (.on res "response"
                   (fn [chunk]
                     (println "got something " chunk)
                     (.log js/console (str "Body: " chunk))
                     (done)))
              (.on res "error"
                   (fn [err]
                     (println "got something " err)
                     (.log js/console (str "Body: " err))
                     (done))))))

(defn adder [x y]
  (+ x y))

(def ^:private http (node/require "http"))
(def ^:private url (node/require "url"))
(def ^:const port 8087)
;
;(def server
;
;  (letfn [(handler [request response]
;            (.log js/console (str "start sample server"))
;            (case (.-url request)
;              "/error400"
;              (do
;                (.writeHead response 400 #js {"content-type" "text/plain"})
;                (.end response "hello world"))
;
;              "/error500"
;              (do
;                (.writeHead response 500 #js {"content-type" "text/plain"})
;                (.end response "hello world"))
;
;              "/timeout"
;              (do
;                (js/setTimeout (fn []
;                                 (.writeHead response 500 #js {"content-type" "text/plain"})
;                                 (.end response "hello world"))
;                               1000))
;              (do
;                (.log js/console (str "call default"))
;                (.writeHead response 200 #js {"content-type" "text/plain"})
;                (.end response "hello world"))))]
;    (do
;      (.log js/console (str "call start sample server"))
;      (-> (.createServer http handler)
;          (.listen port "localhost")))))
;
;
;(defn -main [& args]
;  (println "Hello world!"))
;
;(set! *main-cli-fn* -main)
