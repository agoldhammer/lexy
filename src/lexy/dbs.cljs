(ns lexy.dbs
  (:require [reagent.core :as reagent]))

(defonce app-state (reagent/atom {:lang nil
                                  :total 0
                                  :batch-size 50
                                  :direction :fwd
                                  :logged-in? false
                                  :message-showing? false
                                  :message-text ""
                                  :login-showing? true}))

(defonce default-panel-state {:slugs []
                              :cursor 0
                              :dir 0
                              :defs-loading? true
                              :def-showing? false})

(def def-panel-state (reagent/atom default-panel-state))

(defn previous-word!
  "set cursor back 1"
  []
  (let [cur (:cursor @def-panel-state)]
    (swap! def-panel-state assoc-in [:cursor] (max 0 (dec cur)))))

(defn set-def-showing!
  "set def-showing? in def-panel-state"
  [t-or-f]
  (swap! def-panel-state assoc :def-showing? t-or-f))

(defn set-language!
  [lang]
  (swap! app-state assoc-in [:lang] lang))

(comment
  (print @def-panel-state)
  (print @app-state))