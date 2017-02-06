(ns core.async.http.client.xhr-test
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.test :refer-macros [is testing async]]
            [core.async.http.client.xhr :as http]
            [devcards.core :refer-macros [deftest]]
            [cljs.core.async :refer [<! >!]]))

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
                 ; not really streaming, as xhr doesn't seem to really support it
                 (is (= "streaming-endpoint-1 part-0 part-1 part-2" (<! body-chan)))
                 (done))))))

(deftest ^:acceptance error-enpoint
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
