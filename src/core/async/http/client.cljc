(ns core.async.http.client
  (:require [core.async.http.protocols :as proto])
  (:import (core.async.http.protocols Client)))

(defn request [^Client client options]
  (proto/request! client options))

(defn sync-request [^Client client options]
  (proto/sync-request! client options))

(defn get [^Client client url & [options]]
  (request client (merge {:method :get :url url} options)))

(defn sync-get [^Client client url & [options]]
  (sync-request client (merge {:method :get :url url} options)))
