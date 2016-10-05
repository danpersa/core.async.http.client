(ns core.async.http.client-spec
  (:require [speclj.core :refer :all]
            [midje.util :refer [testable-privates]]
            [core.async.http.client :refer :all]
            [core.async.http.client :as http])
  (:import (io.netty.handler.codec.http DefaultHttpHeaders HttpHeaders)
           (org.asynchttpclient HttpResponseHeaders RequestBuilderBase Request)))

(testable-privates core.async.http.client
                   convert-headers
                   add-headers!
                   convert-method-name)

(def ^:private http-headers
  (let [http-headers (DefaultHttpHeaders.)]
    (.add http-headers "X-Header" "value1")
    (.add http-headers "X-Header" "value2")
    (.add http-headers "X-Header-1" "value2")
    (.add http-headers "X-Header-2" ["value1" "value2"])
    (let [headers (HttpResponseHeaders. http-headers)]
      headers)))

(describe "convert-headers"
          (it "should return the correct headers"
              (should= {"X-Header"   ["value1" "value2"]
                        "X-Header-1" "value2"
                        "X-Header-2" ["value1" "value2"]}

                       (convert-headers http-headers))))

(describe "add-headers"
          (it "should set the headers for the request"
              (let [^RequestBuilderBase request-builder
                    (.prepareGet http/default-client "http://localhost:8083/endpoint-1")]
                (add-headers! request-builder {"x-header-1" "value-1"
                                               "x-header-2" ["value-2" "value-3"]})
                (let [^Request request (.build request-builder)
                      ^HttpHeaders headers (.getHeaders request)]
                  (should= #{"x-header-1" "x-header-2"} (.names headers))
                  (should= ["value-1"] (.getAll headers "x-header-1"))
                  (should= ["value-2" "value-3"] (.getAll headers "x-header-2"))))))

(describe "convert-method"
          (it "should transform a keyword into an uppercase string"
              (should= "GET" (convert-method-name :get))))

(run-specs)

(comment
  (sync-get "http://www.example.com" :client default-client)
  (sync-get "http://localhost:8083/endpoint-1" :client default-client)
  (sync-get "http://localhost:8083/sleep"
            :timeout 100
            :client default-client)
  (sync-get "http://www.theuselessweb.com/" :client default-client))
