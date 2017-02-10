(defproject core.async.http.client "0.2.0-SNAPSHOT"
  :description "Async http client using core.async"
  :url "https://github.com/danpersa/core.async.http.client"
  :license {:name "MIT License"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure                     "1.9.0-alpha14" :scope "provided"]
                 [org.clojure/clojurescript               "1.9.456"       :scope "provided"]
                 [org.asynchttpclient/async-http-client   "2.0.28"]
                 [org.clojure/core.async                  "0.2.395"]
                 [org.clojure/core.match                  "0.3.0-alpha4"]
                 [org.clojure/tools.logging               "0.3.1"]]
  :plugins [[lein-cljsbuild                    "1.1.4"]
            [lein-figwheel                     "0.5.8"]
            [lein-doo                          "0.1.7"]
            [lein-npm                          "0.6.2"]]
  :target-path "target/%s"
  :clean-targets ^{:protect false} [:target-path "out" "resources/public/cljs"]
  :figwheel {:css-dirs ["resources/public/css"]}
  :doo {:paths {:phantom "phantomjs --web-security=false"
                :node    "node"}}
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
                                               :asset-path           "cljs/devcard-tests/out"
                                               :output-dir           "resources/public/cljs/devcard-tests/out"
                                               :output-to            "resources/public/cljs/devcard-tests/all-tests.js"
                                               :source-map-timestamp true}}
                                             {:id           "node-test"
                                              :source-paths ["src" "test"]
                                              :compiler     {:main           runners.doo-node
                                                             :optimizations  :simple
                                                             :asset-path     "cljs/node-tests/out"
                                                             :output-dir     "resources/public/cljs/node-tests/out"
                                                             :output-to      "resources/public/cljs/node-tests/all-tests.js"
                                                             :cache-analysis true
                                                             :target         :nodejs}}
                               {:id           "node-prod"
                                :source-paths ["src"]
                                :compiler     {:main          core.async.http.client.node
                                               :optimizations :simple
                                               :asset-path    "cljs/node-prod/out"
                                               :output-dir    "resources/public/cljs/node-prod/out"
                                               :output-to     "resources/public/cljs/node-prod/main.js"
                                               :target        :nodejs}}]
              :test-commands {"test" ["lein" "doo" "phantom" "test" "once"]}}
  :profiles {:uberjar {:aot :all}
             :dev     {:source-paths           ["dev"]
                       :test-paths             ["test"]
                       :main                   core.async.http.sample-endpoints
                       :resource-paths         ["test/resources"]
                       :plugins                [[com.jakemccrary/lein-test-refresh    "0.17.0"]
                                                [lein-ancient                         "0.6.10"]]
                       :dependencies           [[ch.qos.logback/logback-classic       "1.2.1"]
                                                [midje                                "1.9.0-alpha6"]
                                                [org.immutant/web                     "2.1.6"]
                                                [devcards                             "0.2.2"]
                                                [lein-doo                             "0.1.7"]]}}
  :test-selectors {:default (complement :acceptance)
                   :acceptance :acceptance
                   :all (constantly true)}
  :test-refresh {:growl          true})
