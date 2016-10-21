(ns core.async.http.client.node-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [async deftest is testing]]
            [cljs.core.async :refer [chan put! <!]]
            [core.async.http.client.node :as http]))

(deftest get-test
  (testing "successful get"
    (async done
      (let [response (http/get "http://localhost:8083/endpoint-1")
            body-chan (response :body)
            status-chan (response :status)
            headers-chan (response :headers)]
        (go
          (let [status (<! status-chan)
                body (<! body-chan)
                headers (<! headers-chan)]
            (println "Body headers" headers)
            (is (= 200 status))
            (is (= "Hello world and endpoint-1" body))
            (done)))))))
