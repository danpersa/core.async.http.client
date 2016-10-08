(use '[core.async.http.client.async-http :as http])
(use '[world :as world])
(use '[speclj.core :refer :all])

(Given #"^an http client$" []
       (world/swap-world! assoc :client http/default-client))

(When #"^I do a sync get to \"([^\"]*)\"$" [endpoint-url]
      (let [client ((world/value) :client)
            prepared-headers ((world/value) :prepared-headers)
            response (http/sync-get (str "http://localhost:8083" endpoint-url)
                                    {:headers (or prepared-headers {})
                                     :client  client})]
        (world/reset-world! {:response response})))

(When #"^I do a sync get to \"([^\"]*)\" with a request timeout of (\d+)$" [endpoint-url timeout]
      (let [client ((world/value) :client)
            response (http/sync-get (str "http://localhost:8083" endpoint-url)
                                    {:client  client
                                     :timeout (Integer. timeout)})]
        (world/reset-world! {:response response})))

(Then #"^I should get the body \"([^\"]*)\"$" [expected-body]
      (should= expected-body (world/response-body)))

(Then #"^I should get the (\d+) status$" [expected-status]
      (should= (Integer. expected-status)
               (world/response-status)))

(Then #"^I should get an error$" []
      (should-not-be-nil (((world/value) :response) :error)))
