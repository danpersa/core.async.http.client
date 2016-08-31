(ns core.async.http.clj-http-spec
  (:require [clj-http.client :as http]))

(comment
  (http/get "http://localhost:8083/fragment-1"))