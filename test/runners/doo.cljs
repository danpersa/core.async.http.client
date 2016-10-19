(ns runners.doo
  (:require [doo.runner :refer-macros [doo-all-tests doo-tests]]
            [runners.tests]))

(doo-tests 'core.async.http.client.xhr-test)
