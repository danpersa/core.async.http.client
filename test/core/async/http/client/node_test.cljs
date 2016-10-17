(ns core.async.http.client.node-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [cljs.core.async :as async :refer [chan put! <!]]
            [core.async.http.client.node :as nodeclient]
            [core.async.http.sample-endpoints :as sample-endpoints]))



(deftest adding
  (is (= 2 (nodeclient/adder 1 1))))


(deftest sample-req
  (sample-endpoints/server)
  (async done
    (nodeclient/sample-request done)))
