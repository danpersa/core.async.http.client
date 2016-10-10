(use '[core.async.http.client.async-http :as http])
(use '[clojure.core.async :refer [<!!]])
(use '[world :as world])
(use '[speclj.core :refer :all])

(When #"^I do a post with \"([^\"]*)\" to \"([^\"]*)\"$" [body endpoint-url]
      (let [client ((world/value) :client)
            response (http/post (str "http://localhost:8083" endpoint-url)
                                {:client client
                                 :body   body})]
        (world/reset-world! {:result response})))