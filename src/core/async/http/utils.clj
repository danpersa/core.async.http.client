(ns core.async.http.utils)

(defn assoc-if [coll pred k v]
  (if (pred coll k) (assoc coll k v) coll))

(defn assoc-if-new [coll k v]
  (assoc-if coll (complement contains?) k v))
