(use '[core.async.http.client.async-http :as http])
(use '[world :as world])
(use '[clojure.test :refer :all])

(Given #"^an http client$" []
       (world/swap-world! assoc :client http/default-client))

(When #"^I do a sync get to \"([^\"]*)\"$" [endpoint-url]
      (let [client ((world/value) :client)
            headers ((world/value) :prepared-headers)
            timeout ((world/value) :prepared-timeout)
            response (http/sync-get (str "http://localhost:8083" endpoint-url)
                                    {:headers (or headers {})
                                     :timeout (Integer. (or timeout 2000))
                                     :client  client})]
        (world/reset-world! {:response response})))

(Then #"^I should get the body \"([^\"]*)\"$" [expected-body]
      (is (= expected-body (world/response-body))))

(Then #"^I should get the (\d+) status$" [expected-status]
      (is (= (Integer. expected-status)
             (world/response-status))))

(Then #"^I should get an error$" []
      (is (not (= nil (((world/value) :response) :error)))))
