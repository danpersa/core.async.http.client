(defproject core.async.http.client "0.1.0-SNAPSHOT"
  :description "Async http client using core.async"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure                     "1.9.0-alpha11"]
                 [org.asynchttpclient/async-http-client   "2.0.12"]
                 [org.clojure/core.async                  "0.2.385"]
                 [org.clojure/core.match                  "0.3.0-alpha4"]
                 [org.clojure/tools.logging               "0.3.1"]]
  :profiles {:uberjar {:aot :all}
             :dev {:source-paths ["dev"]
                   :test-paths ["spec"]
                   :resource-paths ["test/resources"]
                   :plugins      [[speclj                                  "3.3.2"]]
                   :dependencies [[speclj                                  "3.3.2"]
                                  [ch.qos.logback/logback-classic          "1.1.7"]
                                  [midje                                   "1.9.0-alpha5"]
                                  [clj-http                                "3.1.0"]]}})
