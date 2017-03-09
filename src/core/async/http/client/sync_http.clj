(ns core.async.http.client.sync-http
  (:refer-clojure :exclude [get])
  (:require [core.async.http.protocols :as proto]
            [core.async.http.client :as c]
            [clojure.core.async :as async :refer [chan <!!]]
            [clojure.core.match :as m]
            [clojure.tools.logging :as log]
            [core.async.http.client.async-http :as async-http]))

(def client
  (reify proto/Client
    (request! [this options]
      (let [out-chan (chan 1024)
            error-chan (chan 1)]

        (async-http/request (merge
                              options
                              {:status-chan  out-chan
                               :headers-chan out-chan
                               :body-chan    out-chan
                               :error-chan   error-chan}))

        (let [error-or-status (async/alts!! [out-chan error-chan]
                                            :priority true)]
          (m/match [error-or-status]
                   [[status out-chan]]
                   (do
                     (log/debug "Status" status)
                     (let [headers (<!! out-chan)]
                       (log/debug "Headers" headers)
                       (loop [body-part (<!! out-chan)
                              result ""]
                         (log/debug "Got part" body-part)
                         (if body-part
                           (recur (<!! out-chan) (str result body-part))
                           {:status  status
                            :headers headers
                            :body    result}))))
                   [[ex error-chan]]
                   (do
                     (log/error ex "Error")
                     {:error ex})))))))

(def request (partial c/request client))

(def get (partial c/get client))

(def post (partial c/post client))

(def put (partial c/put client))

(def patch (partial c/patch client))

(def delete (partial c/delete client))

(def head (partial c/head client))

(def options (partial c/options client))

(def trace (partial c/trace client))
