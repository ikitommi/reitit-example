(ns example.backend.dispatch
  (:require [example.backend.pizza.pizza :as pizza]
            [example.backend.pizza.pizza-db :as pizza-db]
            [malli.core :as m]
            [malli.error :as me]))

(def actions
  {:pizza/list {:type :query
                :summary "list pizzas"
                :output [:vector pizza/Pizza]
                :handler (fn [{:keys [db]} _]
                           (pizza-db/get-pizzas db))}
   :pizza/get {:type :query
               :summary "get a pizza"
               :input [:map [:id int?]]
               :output [:maybe pizza/Pizza]
               :handler (fn [{:keys [db]} {:keys [id]}]
                          (pizza-db/get-pizza db id))}

   :pizza/add {:type :command
               :summary "add a pizza"
               :input pizza/NewPizza
               :output pizza/Pizza
               :handler (fn [{:keys [db]} pizza]
                          (pizza-db/insert-pizza! db pizza))}

   :pizza/clear {:type :command
                 :summary "remove pizzas"
                 :handler (fn [{:keys [db]} _]
                            (pizza-db/remove-pizzas! db))}})

(defn- validate [key schema data]
  (when (and schema (not (m/validate schema data)))
    (let [errors (-> (m/explain schema data) (me/humanize))]
      (throw (ex-info (format "invalid %s schema" key) {:schema schema,
                                                        :data data,
                                                        :key key
                                                        :errors errors}))))
  data)

(defn dispatch [env [type data]]
  (let [{:keys [input output handler]} (actions type)]
    (assert handler (str "invalid action: " type))
    (->> data
         (validate :input input)
         (handler env)
         (validate :output output))))

(comment
  ;; list
  (dispatch (user/env) [:pizza/list])

  ;; add
  (dispatch (user/env) [:pizza/add {:name "quatro"
                                    :size "L"
                                    :price 120
                                    :origin {:country "Italy"}}])

  ;; one
  (dispatch (user/env) [:pizza/get {:id 1}])

  ;; clear
  (dispatch (user/env) [:pizza/clear]))
