(ns example.backend.pizza.pizza-routes
  (:require [example.backend.pizza.pizza-db :as pizza-db]
            [example.backend.pizza.pizza :as pizza]
            [ring.util.http-response :as r]
            [ring.util.http-status :as status]))

(defn create [{:keys [db]}]
  ["/pizza" {:swagger {:tags ["pizza"]}}

   ["" {:get {:summary "list pizzas"
              :responses {status/ok {:body [:vector pizza/Pizza]}}
              :handler (fn [_]
                         (r/ok (pizza-db/get-pizzas db)))}
        :post {:summary "add a pizza"
               :parameters {:body pizza/NewPizza}
               :responses {status/ok {:body pizza/Pizza}}
               :handler (fn [request]
                          (let [pizza (-> request :parameters :body)]
                            (r/ok (pizza-db/insert-pizza! db pizza))))}}]

   ["/:id" {:get {:summary "get a pizza"
                  :parameters {:path [:map [:id int?]]}
                  :responses {status/ok {:body pizza/Pizza}
                              status/not-found {:description "not found"}}
                  :handler (fn [request]
                             (let [id (-> request :parameters :path :id)]
                               (if-let [pizza (pizza-db/get-pizza db id)]
                                 (r/ok pizza)
                                 (r/not-found))))}}]])
