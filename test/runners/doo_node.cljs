(ns runners.doo-node
  (:require [cljs.test :as test]
            [doo.runner :refer-macros [doo-all-tests doo-tests]]
            [core.async.http.client.node-test]
            [core.async.http.sample-endpoints]))

(doo-tests 'core.async.http.client.node-test)