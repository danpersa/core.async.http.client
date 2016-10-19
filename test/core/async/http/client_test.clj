(ns core.async.http.client-test
  (:require [clojure.test :refer :all]
            [core.async.http.client :refer :all]))

(deftest convert-method-test
  (is (= "GET" (convert-method-name :get))))
