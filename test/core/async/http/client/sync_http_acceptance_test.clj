(ns core.async.http.client.sync-http-acceptance-test
  (:refer-clojure :exclude [get])
  (:require [clojure.test :refer :all]
            [core.async.http.client.sync-http :as http]))

(def ^:private endpoints-url "http://localhost:8083")

(deftest ^:acceptance sync-get-test
  (testing "Successful sync get"
    (let [response (http/get (str endpoints-url "/endpoint-1"))
          body (response :body)
          status (response :status)]
      (is (= 200 status))
      (is (= "Hello world and endpoint-1" body)))))

(deftest ^:acceptance sync-get-from-streaming-endpoint-test
  (testing "Successful sync get from a streaming endpoint"
    (let [response (http/get (str endpoints-url "/streaming-endpoint-1"))
          body (response :body)
          status (response :status)]
      (is (= 200 status))
      (is (= "streaming-endpoint-1 part-0 part-1 part-2" body)))))

(deftest ^:acceptance sync-error-endpoint-test
  (testing "Do an sync get to an endpoint which returns 500"
    (let [response (http/get (str endpoints-url "/error"))
          body (response :body)
          status (response :status)]
      (is (= 500 status))
      (is (= "Hello world 500" body)))))

(deftest ^:acceptance sync-timeout-endpoint
  (testing "Successful sync get to an endpoint which times out"
    (let [response (http/get (str endpoints-url "/sleep")
                             {:timeout 100})
          body (response :body)
          status (response :status)
          error-chan (response :error)]

      (is (= :timeout error-chan))
      (is (= nil status))
      (is (= nil body)))))

(deftest ^:acceptance sync-headers-test
  (testing "Successfully specify headers for a sync request"

    (let [response (http/get (str endpoints-url "/x-headers")
                             {:headers {"x-header-1" "value-1"
                                        "x-header-2" "value-2"
                                        "x-header-3" ["value-3" "value-4"]}})
          body (response :body)
          status (response :status)]
      (is (= 200 status))
      (is (= "x-header-1: value-1, x-header-2: value-2, x-header-3: value-3,value-4" body)))))

(deftest ^:acceptance sync-post-test
  (testing "Successful sync post"
    (let [response (http/post (str endpoints-url "/echo")
                              {:body "Hello world"})
          body (response :body)
          status (response :status)]
      (is (= 200 status))
      (is (= "Hello world" body)))))
