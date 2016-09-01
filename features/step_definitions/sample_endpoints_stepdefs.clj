(use '[core.async.http.sample-endpoints :as endpoints])
(use '[core.async.http.client :as http])
(use '[midje.sweet :refer :all])

(def world (atom {:result ""}))

(Given #"^some sample endpoints$" []
       (endpoints/start))

(When #"^I check if the sample endpoints are started$" []
      (let [response (http/sync-get http/client "http://localhost:8083/endpoint-1")]
        (reset! world {:result (str (response :body))})))

(Then #"^they should be started$" []
      (fact
        (@world :result) => "Hello world and endpoint-1"))
