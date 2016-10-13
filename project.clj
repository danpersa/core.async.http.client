(defproject core.async.http.client "0.1.0-SNAPSHOT"
  :description "Async http client using core.async"
  :url "https://github.com/danpersa/core.async.http.client"
  :license {:name "MIT License"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure                     "1.9.0-alpha11"]
                 [org.clojure/clojurescript               "1.9.229"]
                 [org.asynchttpclient/async-http-client   "2.0.12"]
                 [org.clojure/core.async                  "0.2.385"]
                 [org.clojure/core.match                  "0.3.0-alpha4"]
                 [org.clojure/tools.logging               "0.3.1"]
                 [devcards                                "0.2.1-7"]
                 [lein-doo                                "0.1.7"]]
  :plugins [[lein-cljsbuild                    "1.1.4"]
            [lein-figwheel                     "0.5.8"]
            [lein-doo                          "0.1.7"]]
  :target-path "target/%s"
  :clean-targets ^{:protect false} [:target-path "out" "resources/public/cljs"]
  :figwheel {:css-dirs ["resources/public/css"]}
  :cljsbuild {
              :builds        [{:id           "dev"          ; development configuration
                               :source-paths ["src"]        ; Paths to monitor for build
                               :figwheel     true           ; Enable Figwheel
                               :compiler     {:main                 core.async.http.client.xhr ; your main namespace
                                              :asset-path           "cljs/out" ; Where load-dependent files will go, mind you this one is relative
                                              :output-to            "resources/public/cljs/main.js" ; Where the main file will be built
                                              :output-dir           "resources/public/cljs/out" ; Directory for temporary files
                                              :source-map-timestamp true} ; Sourcemaps hurray!
                               }
                              {:id           "test"
                               :source-paths ["src" "test"]
                               :compiler     {:main          runners.doo
                                              :optimizations :none
                                              :output-dir    "resources/public/cljs/tests/out"
                                              :output-to     "resources/public/cljs/tests/all-tests.js"}}
                              {:id           "devcards-test"
                               :source-paths ["src" "test"]
                               :figwheel     {:devcards true}
                               :compiler     {:main                 runners.browser
                                              :optimizations        :none
                                              :asset-path           "cljs/tests/out"
                                              :output-dir           "resources/public/cljs/tests/out"
                                              :output-to            "resources/public/cljs/tests/all-tests.js"
                                              :source-map-timestamp true}}]
              :test-commands {"test" ["lein" "doo" "phantom" "test" "once"]}}
  :profiles {:uberjar {:aot :all}
             :dev     {:source-paths           ["dev"]
                       :test-paths             ["features" "features/step_definitions" "test"]
                       :cucumber-feature-paths ["features"]
                       :resource-paths         ["test/resources"]
                       :plugins                [[org.clojars.punkisdead/lein-cucumber "1.0.7"]
                                                [com.jakemccrary/lein-test-refresh    "0.17.0"]]
                       :dependencies           [[ch.qos.logback/logback-classic       "1.1.7"]
                                                [info.cukes/cucumber-clojure          "1.2.4"]
                                                [midje                                "1.9.0-alpha5"]
                                                [org.immutant/web                     "2.1.5"]]}}
  :test-selectors {:default (complement :acceptance)
                   :acceptance :acceptance
                   :all (constantly true)}
  :test-refresh {:growl          true})
