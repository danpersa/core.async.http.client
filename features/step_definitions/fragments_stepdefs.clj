(use '[core.async.http.fragments :as fragments])
(use '[clj-http.client :as client])
(use '[midje.sweet :refer :all])

(def world (atom {:result ""}))

(Given #"^some fragments$" []
       (fragments/start-fragments))

(When #"^I check if the fragments are started$" []
      (let [response (client/get "http://localhost:8083/fragment-1")]
        (reset! world {:result (str (response :body))})))

(Then #"^they should be started$" []
      (fact
        (@world :result) => "Hello world and fragment-1\n"))
