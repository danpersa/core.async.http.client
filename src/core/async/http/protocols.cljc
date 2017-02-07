(ns core.async.http.protocols)

(defprotocol Client
  (request! [this options]
    "Takes a request map as an argument. Returns a response map, with channels for status, headers, body and error."))
