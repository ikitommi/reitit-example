(ns example.backend.test-system
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [example.backend.main :as main]
            [muuntaja.core :as m]
            [clj-http.client :as client])
  (:import (java.net ServerSocket)))

(defonce system (atom nil))

(defn base-url []
  (main/env
    "TEST_BASE_URL"
    (str "http://localhost:" (-> @system ::main/jetty .getConnectors first .getPort))))

(defn random-port []
  (with-open [s (ServerSocket. 0)]
    (.getLocalPort s)))

(defn halt []
  (swap! system #(if % (ig/halt! %))))

(defn config []
  (-> (main/system-config)
      (assoc-in [::main/jetty :port] (random-port))
      (assoc-in [::main/flyway :clean] true)
      (assoc-in [::main/db :database-name] "example_test")))

(defn go []
  (halt)
  (reset! system (ig/init (config))))

(defn system-running [f]
  (try
    (go)
    (println "testing against:" (base-url))
    (f)
    (finally
      (halt))))

(defn env [] (::main/env @system))
(defn db [] (:db (env)))

(defn- http
  ([f uri]
   (http f uri nil))
  ([f uri request]
   (let [url (str (base-url) uri)
         request (cond-> request (:body request) (update :body (partial m/encode "application/json")))]
     (binding [*data-readers* (merge *data-readers* {})] ;; merge data-readers
       (f url (merge {:accept "application/edn"
                      :as :auto
                      ;:debug true
                      :content-type "application/json"
                      :throw-exceptions false}
                     request))))))

(def http-get (partial http client/get))
(def http-post (partial http client/post))
