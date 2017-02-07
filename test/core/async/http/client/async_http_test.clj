(ns core.async.http.client.async-http-test
  (:refer-clojure :exclude [get])
  (:require [clojure.test :refer :all]
            [midje.util :refer [testable-privates]]
            [core.async.http.client.async-http :refer :all])
  (:import (io.netty.handler.codec.http DefaultHttpHeaders HttpHeaders)
           (org.asynchttpclient HttpResponseHeaders RequestBuilderBase Request)))

(testable-privates core.async.http.client.async-http
                   convert-headers
                   add-headers!)

(def ^:private http-headers
  (let [http-headers (DefaultHttpHeaders.)]
    (.add http-headers "X-Header" "value1")
    (.add http-headers "X-Header" "value2")
    (.add http-headers "X-Header-1" "value2")
    (.add http-headers "X-Header-2" ["value1" "value2"])
    (let [headers (HttpResponseHeaders. http-headers)]
      headers)))

(deftest convert-headers-test
  (is (= {"X-Header"   ["value1" "value2"]
          "X-Header-1" "value2"
          "X-Header-2" ["value1" "value2"]}

         (convert-headers http-headers))))

(deftest add-headers-test
  (let [^RequestBuilderBase request-builder
        (.prepareGet default-client "http://localhost:8083/endpoint-1")]
    (add-headers! request-builder {"x-header-1" "value-1"
                                   "x-header-2" ["value-2" "value-3"]})
    (let [^Request request (.build request-builder)
          ^HttpHeaders headers (.getHeaders request)]
      (is (= #{"x-header-1" "x-header-2"} (.names headers)))
      (is (= ["value-1"] (.getAll headers "x-header-1")))
      (is (= ["value-2" "value-3"] (.getAll headers "x-header-2"))))))

(comment
  (sync-get "http://www.example.com" {:client default-client})
  (sync-get "http://localhost:8083/endpoint-1" {:client default-client})
  (sync-get "http://localhost:8083/sleep"
            {:timeout 100
             :client  default-client})
  (sync-get "http://www.theuselessweb.com/" {:client default-client}))
