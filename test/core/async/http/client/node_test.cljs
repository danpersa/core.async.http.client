(ns core.async.http.client.node-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [cljs.core.async :as async :refer [chan put! <!]]
            [core.async.http.client.node :as nodeclient]
            [cljs.nodejs :as node]))



(def ^:private http (node/require "http"))
(def ^:private url (node/require "url"))
(def ^:const port 8087)

(defn server []

  (letfn [(handler [request response]
            (.log js/console (str "start sample server"))
            (case (.-url request)
              "/error400"
              (do
                (.writeHead response 400 #js {"content-type" "text/plain"})
                (.end response "hello world"))

              "/error500"
              (do
                (.writeHead response 500 #js {"content-type" "text/plain"})
                (.end response "hello world"))

              "/timeout"
              (do
                (js/setTimeout (fn []
                                 (.writeHead response 500 #js {"content-type" "text/plain"})
                                 (.end response "hello world"))
                               1000))
              (do
                (.log js/console (str "call default"))
                (.writeHead response 200 #js {"content-type" "text/plain"})
                (.end response "hello world"))))]
    (do
      (.log js/console (str "call start sample server"))
      (-> (.createServer http handler)
          (.listen port "localhost")))))


(deftest adding
  (is (= 2 (nodeclient/adder 1 1))))


(deftest sample-req
  (server)
  (async done
    (nodeclient/sample-request done)))