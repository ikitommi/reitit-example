(ns example.backend.main
  (:require [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [example.backend.http :as http]
            [hikari-cp.core :as hikari-cp]
            [example.backend.routes :as routes]
            [clojure.tools.logging :as log])
  (:import [org.flywaydb.core Flyway]
           (org.eclipse.jetty.server Server)))

(defn ->long [x]
  (if (int? x) x (Long/parseLong x)))

(defn env [key default]
  (some-> (or (System/getenv (name key)) default)))

(defn system-config []
  {::db {:adapter "postgresql"
        :username (env "DB_USERNAME" "example")
        :password (env "DB_PASSWORD" "example")
        :server-name (env "DB_HOST" "localhost")
        :port-number (->long (env "DB_PORT" 5432))
        :database-name (env "DB_NAME" "example")
        :connection-timeout 5000
        :validation-timeout 5000
        :maximum-pool-size 10}
   ::flyway {:schemas ["example"]
            :migrate true
            ;:clean true
            :db (ig/ref ::db)}
   ::jetty {:port 3000
           :join? false
           :env (ig/ref ::env)}
   ::env {:db (ig/ref ::db)}})

(defmethod ig/init-key ::db [_ opts]
  {:datasource (hikari-cp/make-datasource opts)})

(defmethod ig/halt-key! ::db [_ this]
  (hikari-cp/close-datasource (:datasource this)))

(defmethod ig/init-key ::flyway [_ {:keys [schemas migrate clean db]}]
  (let [flyway (-> (Flyway/configure)
                   (.table "schema_version")
                   (.dataSource (:datasource db))
                   (.schemas (into-array String schemas))
                   (.locations (into-array String ["/db/migration"]))
                   (.load))]
    (when clean (.clean flyway))
    (when migrate (.migrate flyway))
    flyway))

(defmethod ig/init-key ::jetty [_ {:keys [port join? env]}]
  (-> (http/handler {:routes (routes/create env)})
      (jetty/run-jetty {:port port :join? join?})))

(defmethod ig/halt-key! ::jetty [_ ^Server server]
  (.stop server))

(defmethod ig/init-key ::env [_ env] env)

(defn -main []
  (log/info "System starting...")
  (ig/init (system-config)))
