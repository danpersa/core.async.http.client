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

(defn convert-method-name [method]
  (.toUpperCase (name method)))
