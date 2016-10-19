(ns core.async.http.client.node-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [cljs.core.async :as async :refer [chan put! <!]]
            [core.async.http.client.node :as node]))

(deftest adding
  (is (= 2 (node/adder 1 1))))

(deftest sample-req
  (async done
    (node/sample-request done)))
