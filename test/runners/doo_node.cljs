(ns runners.doo-node
  (:require [cljs.test]
            [doo.runner :refer-macros [doo-all-tests doo-tests]]
            [core.async.http.client.node-test]
            [cljs.nodejs :as node]))

(node/enable-util-print!)

(doo-tests 'core.async.http.client.node-test)
