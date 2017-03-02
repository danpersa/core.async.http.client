(ns core.async.http.client
  (:refer-clojure :exclude [get])
  (:require [core.async.http.protocols :as proto])
  #?(:clj
     (:import (core.async.http.protocols Client))))

(defn request [^Client client options]
  (proto/request! client options))

(defn get [^Client client url & [options]]
  (request client (merge {:method :get :url url} options)))

(defn post [^Client client url & [options]]
  (request client (merge {:method :post :url url} options)))

(defn put [^Client client url & [options]]
  (request client (merge {:method :put :url url} options)))

(defn patch [^Client client url & [options]]
  (request client (merge {:method :patch :url url} options)))

(defn delete [^Client client url & [options]]
  (request client (merge {:method :delete :url url} options)))

(defn head [^Client client url & [options]]
  (request client (merge {:method :head :url url} options)))

(defn options [^Client client url & [options]]
  (request client (merge {:method :options :url url} options)))

(defn trace [^Client client url & [options]]
  (request client (merge {:method :trace :url url} options)))

(defn convert-method-name [method]
  (.toUpperCase (name method)))
