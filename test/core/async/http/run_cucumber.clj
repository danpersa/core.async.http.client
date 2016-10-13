(ns core.async.http.run-cucumber
  (:require [clojure.test :refer [deftest]])
  (:import (cucumber.api.cli Main)))

; Use it to use the REPL to run Cucumber
(deftest ^:acceptance run-cukes
  (let [classloader (.getContextClassLoader (Thread/currentThread))]
    (. Main (run
              (into-array ["--plugin"
                           "pretty"
                           "--glue"
                           "features/step_definitions"
                           "features"])
              classloader))))
