(ns core.async.http.clj-http-spec
  (:require [clj-http.client :as http]))

(comment
  (http/get "http://localhost:8083/endpoint-1")
  (http/get "http://localhost:8083/sleep"
              {:socket-timeout 1000
               :conn-timeout 200
               :throw-exceptions false}))
