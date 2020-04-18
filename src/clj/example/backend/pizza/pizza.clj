(ns example.backend.pizza.pizza
  (:require [malli.util :as mu]))

(def Pizza
  [:map {:title "pizza"}
   [:id int?]
   [:name string?]
   [:size [:enum "S" "M" "L"]]
   [:price int?]
   [:origin
    [:map
     [:country string?]]]])

(def NewPizza
  (mu/dissoc Pizza :id))
