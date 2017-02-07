(ns core.async.http.client.node-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [cljs.core.async :refer [chan put! <!]]
            [core.async.http.client.node :as http]))

(def ^:private endpoints-url "http://localhost:8083")

(deftest ^:acceptance get-test
  (testing "Successful async get"
    (async done
      (let [response (http/get (str endpoints-url "/endpoint-1"))
            body-chan (response :body)
            status-chan (response :status)]
        (go
          (is (= 200 (<! status-chan)))
          (is (= "Hello world and endpoint-1" (<! body-chan)))
          (done))))))

(deftest ^:acceptance get-from-streaming-endpoint-test
  (testing "Successful async get from a streaming endpoint"
    (async done
      (let [response (http/get (str endpoints-url "/streaming-endpoint-1"))
            body-chan (response :body)
            status-chan (response :status)]
        (go
          (is (= 200 (<! status-chan)))
          (is (= "streaming-endpoint-1" (<! body-chan)))
          (is (= " part-0" (<! body-chan)))
          (is (= " part-1" (<! body-chan)))
          (is (= " part-2" (<! body-chan)))
          (is (= nil (<! body-chan)))
          (done))))))

(deftest ^:acceptance error-endpoint-test
  (testing "Do an async get to an endpoint which returns 500"
    (async done
      (let [response (http/get (str endpoints-url "/error"))
            body-chan (response :body)
            status-chan (response :status)]
        (go
          (is (= 500 (<! status-chan)))
          (is (= "Hello world 500" (<! body-chan)))
          (is (= nil (<! body-chan)))
          (done))))))

(deftest ^:acceptance timeout-endpoint
  (testing "Successful async get to an endpoint which times out"
    (async done
      (let [response (http/get (str endpoints-url "/sleep")
                               {:timeout 100})
            body-chan (response :body)
            status-chan (response :status)
            error-chan (response :error)]
        (go
          (is (= :timeout (<! error-chan)))
          (is (= nil (<! status-chan)))
          (is (= nil (<! body-chan)))
          (done))))))

(deftest ^:acceptance headers-test
  (testing "Successfully specify headers"
    (async done
      (let [response (http/get (str endpoints-url "/x-headers")
                               {:headers {"x-header-1" "value-1"
                                          "x-header-2" "value-2"
                                          "x-header-3" ["value-3" "value-4"]}})
            body-chan (response :body)
            status-chan (response :status)]
        (go
          (is (= 200 (<! status-chan)))
          (is (= "x-header-1: value-1, x-header-2: value-2, x-header-3: value-3,value-4" (<! body-chan)))
          (done))))))
