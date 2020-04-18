(ns example.backend.http
  (:require [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.swagger :as swagger]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.spec :as spec]
            [spec-tools.spell :as spell]
            [muuntaja.core :as m]
            [clojure.tools.logging :as log]))

(defn handler [{:keys [routes]}]
  (ring/ring-handler
    (ring/router
      routes
      {:validate spec/validate ;; enable spec validation for route data
       :reitit.spec/wrap spell/closed ;; strict top-level validation
       :exception pretty/exception ;; pretty exceptions
       :data {:coercion (reitit.coercion.malli/create) ;; malli
              :muuntaja m/instance ;; default content negotiation
              :middleware [;; swagger feature
                           swagger/swagger-feature
                           ;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           (exception/create-exception-middleware
                             (merge
                               exception/default-handlers
                               {::exception/wrap (fn [handler ^Exception e request]
                                                   (log/error e (.getMessage e))
                                                   (handler e request))}))
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodys
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware
                           ;; multipart
                           multipart/multipart-middleware]}})
    (ring/routes
      (ring/redirect-trailing-slash-handler)
      (ring/create-resource-handler {:path "/"})
      (ring/create-default-handler))))
