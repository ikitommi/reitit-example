(ns example.backend.pizza.pizza-db
  (:require [hugsql.core :as hugsql]
            [example.backend.db :as db]))

(declare sql-remove-pizzas)
(declare sql-get-pizzas)
(declare sql-get-pizza)
(declare sql-insert-pizza)
(hugsql/def-db-fns "example/backend/pizza/pizza.sql" {:quoting :ansi})

(defn ->db [x] (update x :origin db/->json))
(defn <-db [x] (update x :origin db/<-json))

(defn get-pizzas [db]
  (mapv <-db (sql-get-pizzas db)))

(defn get-pizza [db id]
  (some-> (sql-get-pizza db {:id id}) (<-db)))

(defn insert-pizza! [db pizza]
  (some->> (update pizza :id identity)
           (->db)
           (sql-insert-pizza db)
           (<-db)))

(defn remove-pizzas! [db]
  (sql-remove-pizzas db)
  nil)
