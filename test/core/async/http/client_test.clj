(ns core.async.http.client-test
  (:refer-clojure :exclude [get])
  (:require [clojure.test :refer :all]
            [core.async.http.client :refer :all]))

(deftest convert-method-test
  (is (= "GET" (convert-method-name :get))))
