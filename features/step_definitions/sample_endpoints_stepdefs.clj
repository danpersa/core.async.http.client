(use '[core.async.http.sample-endpoints :as endpoints])

(Given #"^some sample endpoints$" []
       (endpoints/start))

