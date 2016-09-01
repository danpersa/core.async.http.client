(ns core.async.http.sample-endpoints
  (:require
    [immutant.web :as web]
    [immutant.web.async :as iasync]))

(def ^:private localhost "localhost")

(def ^:private bloat (str (range 0 5)))

(defn- async-endpoint [name num]
  (fn [request]
    (iasync/as-channel request
                       {:on-open (fn [stream]
                                   (do
                                     (iasync/send! stream (str name "\n"))
                                     (dotimes [msg num]
                                       (iasync/send! stream (str name " " msg " " bloat "\n")
                                                     {:close? (= msg (- num 1))})
                                       (Thread/sleep 70))))})))

(defn- run-async-endpoint [name num]
  (web/run (async-endpoint name num) :host localhost :port 8083 :path (str "/" name)))

(defn- endpoint [name]
  (fn [request]
    {:status  200
     :headers {"X-Header-1" ["Value 1" "Value 2"]}
     :body    (str "Hello world and " name "\n")}))

(defn- error-endpoint [request]
  {:status 500
   :body   "Hello world 500\n"})

(defn- error-sleep-endpoint [request]
  (Thread/sleep 1000)
  (error-endpoint request))

(defn- sleep-endpoint [request]
  (Thread/sleep 1000)
  {:status 200
   :body   (str "Hello world and sleep\n")})

(defn- run-endpoint [name]
  (web/run (endpoint name) :host localhost :port 8083 :path (str "/" name)))

(defn start []
  (web/run error-endpoint :host localhost :port 8083 :path (str "/error"))
  (web/run error-sleep-endpoint :host localhost :port 8083 :path (str "/error-sleep"))
  (web/run sleep-endpoint :host localhost :port 8083 :path (str "/sleep"))
  (run-async-endpoint "async-endpoint-1" 10)
  (run-async-endpoint "async-endpoint-2" 15)
  (run-async-endpoint "async-endpoint-3" 20)

  (run-endpoint "endpoint-1")
  (run-endpoint "endpoint-2")
  (run-endpoint "endpoint-3"))
