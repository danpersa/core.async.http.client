(use '[core.async.http.client :as http])
(use '[midje.sweet :refer :all])
(use '[clojure.core.async :refer [<!!]])

(def world (atom {:result nil}))

(Given #"^an empty world$" []
       (reset! world {:result nil}))

(When #"^I do a get to \"([^\"]*)\"$" [endpoint-url]
      (let [client (@world :client)
            response (http/get (str "http://localhost:8083" endpoint-url)
                               :client client)]
        (reset! world {:result response})))

(When #"^I do a get to \"([^\"]*)\" with a request timeout of (\d+)$" [endpoint-url timeout]
      (let [client (@world :client)
            response (http/get (str "http://localhost:8083" endpoint-url)
                               :client client
                               :timeout (Integer. timeout))]
        (reset! world {:result response})))

(defn get-result-chan [name]
  ((@world :result) name))

(defn verify-chan [name]
  (fact
    (nil? (get-result-chan name)) => false))

(Then #"^I should get back a map of channels$" []
      (verify-chan :status)
      (verify-chan :body)
      (verify-chan :headers)
      (verify-chan :error))

(Then #"^I should get nil from the \"([^\"]*)\" chan$" [chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (fact
          (nil? value-from-chan) => true)))

(Then #"^I should get back \"([^\"]*)\" from the \"([^\"]*)\" chan$" [expected chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (fact
          (str value-from-chan) => expected)))

(Then #"^I should get back the UTF-8 string \"([^\"]*)\" as byte array from the \"([^\"]*)\" chan$" [expected chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (fact
          (String. value-from-chan "UTF-8") => expected)))

(Then #"^I should get an error from the \"([^\"]*)\" chan$" [chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (fact
          (instance? Exception value-from-chan) => true)))
