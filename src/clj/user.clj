(ns user
  (:require [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [integrant.repl.state :as state]
            [example.backend.main :as main]))

(integrant.repl/set-prep! main/system-config)

(defn system [] (or state/system (throw (ex-info "System not running" {}))))

(defn env [] (::main/env (system)))
(defn db [] (:db (env)))
