(defproject core.async.http.client "0.1.0"
  :description "Async http client using core.async"
  :url "https://github.com/danpersa/core.async.http.client"
  :license {:name "MIT License"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure                     "1.9.0-alpha11"]
                 [org.asynchttpclient/async-http-client   "2.0.12"]
                 [org.clojure/core.async                  "0.2.385"]
                 [org.clojure/core.match                  "0.3.0-alpha4"]
                 [org.clojure/tools.logging               "0.3.1"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev     {:source-paths           ["dev"]
                       :test-paths             ["features" "features/step_definitions" "spec"]
                       :cucumber-feature-paths ["features"]
                       :resource-paths         ["spec/resources"]
                       :plugins                [[speclj                               "3.3.2"]
                                                [org.clojars.punkisdead/lein-cucumber "1.0.7"]]
                       :dependencies           [[speclj                               "3.3.2"]
                                                [ch.qos.logback/logback-classic       "1.1.7"]
                                                [info.cukes/cucumber-clojure          "1.2.4"]
                                                [midje                                "1.9.0-alpha5"]
                                                [org.immutant/web                     "2.1.5"]
                                                [clj-http                             "3.1.0"]]}})
