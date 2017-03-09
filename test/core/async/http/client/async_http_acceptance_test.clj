(ns core.async.http.client.async-http-acceptance-test
  (:refer-clojure :exclude [get])
  (:require [clojure.test :refer :all]
            [core.async.http.client.async-http :as http]
            [clojure.core.async :refer [<!!]]))

(def ^:private endpoints-url "http://localhost:8083")

(deftest ^:acceptance get-test
  (testing "Successful async get"
    (let [response (http/get (str endpoints-url "/endpoint-1"))
          body-chan (response :body)
          status-chan (response :status)]
      (is (= 200 (<!! status-chan)))
      (is (= "Hello world and endpoint-1" (<!! body-chan))))))

(deftest ^:acceptance get-from-streaming-endpoint-test
  (testing "Successful async get from a streaming endpoint"
    (let [response (http/get (str endpoints-url "/streaming-endpoint-1"))
          body-chan (response :body)
          status-chan (response :status)]
      (is (= 200 (<!! status-chan)))
      (is (= "streaming-endpoint-1" (<!! body-chan)))
      (is (= " part-0" (<!! body-chan)))
      (is (= " part-1" (<!! body-chan)))
      (is (= " part-2" (<!! body-chan)))
      (is (= nil (<!! body-chan))))))

(deftest ^:acceptance error-endpoint-test
  (testing "Do an async get to an endpoint which returns 500"
    (let [response (http/get (str endpoints-url "/error"))
          body-chan (response :body)
          status-chan (response :status)]
      (is (= 500 (<!! status-chan)))
      (is (= "Hello world 500" (<!! body-chan)))
      (is (= nil (<!! body-chan))))))

(deftest ^:acceptance timeout-endpoint
  (testing "Successful async get to an endpoint which times out"
    (let [response (http/get (str endpoints-url "/sleep")
                             {:timeout 100})
          body-chan (response :body)
          status-chan (response :status)
          error-chan (response :error)]

      (is (= :timeout (<!! error-chan)))
      (is (= nil (<!! status-chan)))
      (is (= nil (<!! body-chan))))))

(deftest ^:acceptance headers-test
  (testing "Successfully specify headers"

    (let [response (http/get (str endpoints-url "/x-headers")
                             {:headers {"x-header-1" "value-1"
                                        "x-header-2" "value-2"
                                        "x-header-3" ["value-3" "value-4"]}})
          body-chan (response :body)
          status-chan (response :status)]
      (is (= 200 (<!! status-chan)))
      (is (= "x-header-1: value-1, x-header-2: value-2, x-header-3: value-3,value-4" (<!! body-chan))))))

(deftest ^:acceptance post-test
  (testing "Successful async post"
    (let [response (http/post (str endpoints-url "/echo")
                              {:body "Hello world"})
          body-chan (response :body)
          status-chan (response :status)]
      (is (= 200 (<!! status-chan)))
      (is (= "Hello world" (<!! body-chan))))))

(defn call-with-method [http-call result]
  (let [response (http-call (str endpoints-url "/method-echo"))
        body-chan (response :body)
        status-chan (response :status)]
    (is (= 200 (<!! status-chan)))
    (is (= result (<!! body-chan)))))

(deftest ^:acceptance method-test
  (testing "Successful specify the method"
    (call-with-method http/get "get")
    (call-with-method http/post "post")
    (call-with-method http/put "put")
    (call-with-method http/patch "patch")
    (call-with-method http/delete "delete")
    (call-with-method http/head nil)
    (call-with-method http/options "options")
    (call-with-method http/trace "trace")))
