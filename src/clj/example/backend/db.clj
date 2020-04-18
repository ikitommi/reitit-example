(ns example.backend.db
  (:require [jsonista.core :as j])
  (:import (org.postgresql.util PGobject)))

(def ^:private object-mapper (j/object-mapper {:decode-key-fn true}))

(defn ->json [m]
  (doto (PGobject.)
    (.setType "JSONB")
    (.setValue (j/write-value-as-string m))))

(defn <-json [x] (some-> x (.getValue) (j/read-value object-mapper)))
(defn <-json-str-keys [x] (some-> x (.getValue) (j/read-value)))
