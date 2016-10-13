(ns core.async.http.client.xhr-test
  (:require [cljs.test :refer-macros [is testing]]
            [core.async.http.client.xhr :as http]
            [devcards.core :refer-macros [deftest]]))

(deftest a-test
         (testing "FIXME, I fail."
           (is (= 0 1))))

(deftest another-test
         (testing "FIXME, I fail."
           (is (= "Hello world!" (http/hello-world)))))
