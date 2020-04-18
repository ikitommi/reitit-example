(ns example.backend.routes
  (:require [reitit.swagger-ui :as swagger-ui]
            [reitit.swagger :as swagger]
            [fumi.client :as fc]
            [example.backend.pizza.pizza-routes :as pizza-routes]))

(defn create [env]
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "example-api"
                            :description "Example API"}
                     :tags [{:name "pizza", :description "pizza-api"}
                            {:name "health" :description "health-api"}]}
           :handler (swagger/create-swagger-handler)}}]
   ["/api-docs/*"
    {:get {:no-doc true
           :handler (swagger-ui/create-swagger-ui-handler
                      {:config {:validatorUrl nil}})}}]

   ["/api"
    ["/health" {:swagger {:tags ["health"]}}
     ["/ping" {:get {:summary "ping"
                     :handler (constantly {:status 200, :body {:message "pong"}})}}]
     ["/metrics" {:get {:summary "prometheus endpoint"
                        :handler (fn [_]
                                   {:status 200,
                                    :headers {"Content-Type" "text/plain"}
                                    :body (-> (fc/collect) (fc/serialize :text))})}}]]

    (pizza-routes/create env)]])
