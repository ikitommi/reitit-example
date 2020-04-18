(ns example.backend.http-api-test
  (:require [clojure.test :refer :all]
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

(deftest ^:integration api-test

  (testing "there are no pizzas"
    (let [{:keys [status body]} (ts/http-get "/api/pizza")]
      (is (= 200 status))
      (is (= [] body))))

  (testing "adding pizzas"
    (let [{:keys [status body]} (ts/http-post "/api/pizza" {:body new-pizza})]
      (is (= 200 status))
      (is (= pizza body))))

  (testing "there are pizzas"
    (let [{:keys [status body]} (ts/http-get "/api/pizza")]
      (is (= 200 status))
      (is (= [pizza] body)))))

