(ns example.backend.dispatcher-test
  (:require [clojure.test :refer :all]
            [example.backend.dispatch :as d]
            [example.backend.test-system :as ts]))

(def pizza
  {:id 1
   :name "quatro"
   :size "L"
   :price 120
   :origin {:country "Italy"}})

(def new-pizza
  (dissoc pizza :id))

(use-fixtures :each ts/system-running)

(deftest ^:integration local-test

  (testing "there are no pizzas"
    (is (= [] (d/dispatch (ts/env) [:pizza/list]))))

  (testing "adding pizzas"
    (is (= pizza (d/dispatch (ts/env) [:pizza/add pizza]))))

  (testing "there are pizzas"
    (is (= [pizza] (d/dispatch (ts/env) [:pizza/list])))))

(deftest ^:integration remote-test

  (testing "there are no pizzas"
    (is (= [] (ts/dispatch [:pizza/list]))))

  (testing "adding pizzas"
    (is (= pizza (ts/dispatch [:pizza/add pizza]))))

  (testing "there are pizzas"
    (is (= [pizza] (ts/dispatch [:pizza/list])))))
