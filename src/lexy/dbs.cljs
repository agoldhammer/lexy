(ns lexy.dbs
  (:require [reagent.core :as reagent]))

(defonce default-app-state  {:lang nil
                             :total 0
                             :batch-size 50
                             :direction :fwd
                             :logged-in? false
                             :message-showing? false
                             :message-text ""
                             :login-showing? true})

(defonce default-panel-state {:slugs []
                              :current-score nil
                              :cursor 0
                              :dir 0
                              :defs-loading? true
                              :def-showing? false})

(def def-panel-state (reagent/atom default-panel-state))

(def app-state (reagent/atom default-app-state))

(defn set-login-showing 
  "set/reset login-showing? flag"
  [t-or-f]
  (swap! app-state assoc :login-showing? t-or-f))

(defn reset-def-panel! 
  "reset definition panel to default state"
  []
  (reset! def-panel-state default-panel-state))

(defn set-current-score
  "set (or reset) the current score map"
  [score-map-or-nil]
  (swap! def-panel-state assoc :current-score score-map-or-nil)
  )

(defn get-current-score
  "return the current score map"
  []
  (:current-score @def-panel-state))


(defn reset-app-state!
  []
  (reset! app-state default-app-state))

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
  "set the language (= database name supplied by server)"
  [lang]
  (swap! app-state assoc-in [:lang] lang))

(defn set-message-flag-and-text
  "set message-showing? flag in app-state, with text if needed"
  ([t-or-f]
   (set-message-flag-and-text t-or-f ""))
  ([t-or-f text]
   ;; TODO: works now for bad login dismissal
   ;; but this should be changed to a more general purpose msg fn
   (swap! app-state merge {:message-showing? t-or-f
                               :message-text text
                               :login-showing? true})))

(defn close-login-box!
  "set :login-showing? flag to false"
  []
  (set-login-showing false)
  #(.open js/window "/"))


(comment
  (print @def-panel-state)
  (print (dissoc :slugs @def-panel-state))
  (print @app-state))