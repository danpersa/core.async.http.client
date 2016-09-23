(ns core.async.http.run-cucumber
  (:import (cucumber.api.cli Main)))

; Use it to use the REPL to run Cucumber
(comment
  (let [classloader (.getContextClassLoader (Thread/currentThread))]
    (. Main (run
              (into-array ["--plugin"
                           "pretty"
                           "--glue"
                           "features/step_definitions"
                           "features"])
              classloader))))
