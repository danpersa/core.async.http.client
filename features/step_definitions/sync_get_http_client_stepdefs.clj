(use '[core.async.http.client :as http])
(use '[midje.sweet :refer :all])

(def world (atom {:result nil}))

(Given #"^an http client$" []
       (swap! world assoc :client http/client))

(When #"^I do a sync get to \"([^\"]*)\"$" [endpoint-url]
      (let [client (@world :client)
            response (http/sync-get client (str "http://localhost:8083" endpoint-url))]
        (reset! world {:result response})))

(When #"^I do a sync get to \"([^\"]*)\" with a request timeout of (\d+)$" [endpoint-url timeout]
      (let [client (@world :client)
            response (http/sync-get client (str "http://localhost:8083" endpoint-url)
                                    :timeout (Integer. timeout))]
        (reset! world {:result response})))

(Then #"^I should get the body \"([^\"]*)\"$" [expected-body]
      (fact
        ((@world :result) :body) => expected-body))

(Then #"^I should get the (\d+) status$" [expected-status]
      (fact
        ((@world :result) :status) => (Integer. expected-status)))

(Then #"^I should get an error$" []
      (fact
        (nil? ((@world :result) :error)) => false))
