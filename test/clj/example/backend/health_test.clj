(ns example.backend.health-test
  (:require [clojure.test :refer :all]
            [example.backend.test-system :as ts]))

(use-fixtures :each ts/system-running)

(deftest ^:integration ping-api-test
  (testing "ping"
    (let [{:keys [status body]} (ts/http-get "/api/health/ping")]
      (is (= 200 status))
      (is (= {:message "pong"} body))))
  (testing "metrics"
    (let [{:keys [status _body]} (ts/http-get "/api/health/metrics")]
      (is (= 200 status)))))
