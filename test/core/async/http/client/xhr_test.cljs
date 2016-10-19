(ns core.async.http.client.xhr-test
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.test :refer-macros [is testing async]]
            [core.async.http.client.xhr :as http]
            [devcards.core :refer-macros [deftest]]
            [cljs.core.async :refer [<! >!]]))

(deftest get-test
         (testing "successful get"
           (async done
             (let [response (http/get "http://localhost:8083/endpoint-1")
                   body-chan (response :body)
                   status-chan (response :status)]
               (go
                 (let [status (<! status-chan)
                       body (<! body-chan)]
                   (is (= 200 status))
                   (is (= "Hello world and endpoint-1" body))
                   (done)))))))
