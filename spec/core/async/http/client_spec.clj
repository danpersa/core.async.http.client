(ns core.async.http.client-spec
  (:require [speclj.core :refer :all]
            [midje.util :refer [testable-privates]]
            [core.async.http.client :refer :all])
  (:import (io.netty.handler.codec.http DefaultHttpHeaders)
           (org.asynchttpclient HttpResponseHeaders)))

(testable-privates core.async.http.client
                   convert-headers)

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
                        "X-Header-1" ["value2"]
                        "X-Header-2" ["value1" "value2"]}

                       (convert-headers http-headers))))

(run-specs)
