(use '[core.async.http.sample-endpoints :as endpoints])
(use '[clj-http.client :as client])
(use '[midje.sweet :refer :all])

(def world (atom {:result ""}))

(Given #"^some sample endpoints$" []
       (endpoints/start))

(When #"^I check if the sample endpoints are started$" []
      (let [response (client/get "http://localhost:8083/endpoint-1")]
        (reset! world {:result (str (response :body))})))

(Then #"^they should be started$" []
      (fact
        (@world :result) => "Hello world and endpoint-1\n"))
