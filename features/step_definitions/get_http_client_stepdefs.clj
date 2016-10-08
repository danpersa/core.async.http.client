(use '[core.async.http.client.async-http :as http])
(use '[clojure.core.async :refer [<!!]])
(use '[world :as world])
(use '[speclj.core :refer :all])

(Given #"^an empty world$" []
       (world/reset-world!))

(When #"^I do a get to \"([^\"]*)\"$" [endpoint-url]
      (let [client ((world/value) :client)
            response (http/get (str "http://localhost:8083" endpoint-url)
                               {:client client})]
        (world/reset-world! {:result response})))

(When #"^I do a get to \"([^\"]*)\" with a request timeout of (\d+)$" [endpoint-url timeout]
      (let [client ((world/value) :client)
            response (http/get (str "http://localhost:8083" endpoint-url)
                               {:client  client
                                :timeout (Integer. timeout)})]
        (world/reset-world! {:result response})))

(defn get-result-chan [name]
  (((world/value) :result) name))

(defn verify-chan [name]
  (should-not-be-nil (get-result-chan name)))

(Then #"^I should get back a map of channels$" []
      (verify-chan :status)
      (verify-chan :body)
      (verify-chan :headers)
      (verify-chan :error))

(Then #"^I should get nil from the \"([^\"]*)\" chan$" [chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (should-be-nil value-from-chan)))

(Then #"^I should get back \"([^\"]*)\" from the \"([^\"]*)\" chan$" [expected chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (should= expected (str value-from-chan))))

(Then #"^I should get back the UTF-8 string \"([^\"]*)\" as byte array from the \"([^\"]*)\" chan$" [expected chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (should= expected (String. value-from-chan "UTF-8"))))

(Then #"^I should get an error from the \"([^\"]*)\" chan$" [chan-name]
      (let [chan (get-result-chan (keyword chan-name))
            value-from-chan (<!! chan)]
        (should-be-a Exception value-from-chan)))
