(use '[core.async.http.client.async-http :as http])
(use '[clojure.core.async :refer [<!!]])
(use '[world :as world])
(use '[speclj.core :refer :all])


(When #"^I do a sync post with \"([^\"]*)\" to \"([^\"]*)\"$" [body endpoint-url]

      (let [client ((world/value) :client)
            prepared-headers ((world/value) :prepared-headers)
            response (http/sync-post (str "http://localhost:8083" endpoint-url)
                                     {:headers (or prepared-headers {})
                                      :client  client
                                      :body    body})]
        (world/reset-world! {:response response})))

