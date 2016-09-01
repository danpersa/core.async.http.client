(use '[core.async.http.client :as http])
(use '[midje.sweet :refer :all])

(def world (atom {:result nil}))

(When #"^I do a sync get to \"([^\"]*)\"$" [endpoint-url]
      (let [response (http/sync-get (str "http://localhost:8083" endpoint-url))]
        (reset! world {:result response})))

(Then #"^I should get the body \"([^\"]*)\"$" [expected-body]
      (fact
        ((@world :result) :body) => expected-body))

(Then #"^I should get the (\d+) status$" [expected-status]
      (fact
        ((@world :result) :status) => (Integer. expected-status)))
