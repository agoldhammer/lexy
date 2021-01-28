(ns lexy.dbs
  (:require [reagent.core :as reagent]))

(defn coin-flip
  "flip coin, return true if heads, false if tails"
  []
  (= 0 (rand-int 2)))

(defonce default-app-state  {:active-db nil
                             :total 0
                             :batch-size 50
                             :logged-in? false
                             :message-showing? false
                             :message-text ""
                             :login-showing? true})

(defonce default-panel-state {:slugs []
                              :current-score nil
                              :slug-changed? false
                              :cursor 0
                              :flipped (coin-flip)
                              :defs-loading? true
                              :def-showing? false})

(def def-panel-state (reagent/atom default-panel-state))

(def app-state (reagent/atom default-app-state))

(defn set-login-showing 
  "set/reset login-showing? flag"
  [t-or-f]
  (swap! app-state assoc :login-showing? t-or-f))

(defn is-flipped?
  "report state of flipped flag"
  []
  (:flipped @def-panel-state))

(defn reset-def-panel! 
  "reset definition panel to default state"
  []
  (reset! def-panel-state default-panel-state))

(defn current-score
  "cursor for current score"
  []
  (reagent/cursor def-panel-state [:current-score]))

(defn has-slug-changed?
  "returns true if current slug has changed, false otherwise"
  []
  (:slug-changed? @def-panel-state))


(defn reset-app-state!
  []
  (reset! app-state default-app-state))

(defn previous-word!
  "set cursor back 1"
  []
  (let [cur (:cursor @def-panel-state)]
    ;; reset current score to trigger new fetch
    (swap! def-panel-state assoc-in [:current-score] nil)
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

(defn set-slug-changed
  "set or reset the slug-changed? flag"
  [true-or-false]
  (swap! def-panel-state assoc :slug-changed? true-or-false)
  )

(defn get-current-slug
  "return the slug pointed to by the cursor"
  []
  (let [cursor (:cursor @def-panel-state)]
       (nth (:slugs @def-panel-state) cursor)))

(defn modify-current-slug
  [id new-value]
  (let [key (keyword id)
        cursor (:cursor @def-panel-state)
        slugs (:slugs @def-panel-state)
        old-slug (nth slugs cursor)
        new-slug (assoc old-slug key new-value)]
    (print "old new slugs" old-slug new-slug)
    (swap! def-panel-state assoc :slugs (assoc slugs cursor new-slug))
    (print "new item" (nth (:slugs @def-panel-state) cursor))
    (set-slug-changed true)))

(defn set-defs-loading
  "set or reset the defs loading flag"
  [true-or-false]
  (swap! def-panel-state assoc :defs-loading? true-or-false))




(comment
  (print @def-panel-state)
  (print (dissoc @def-panel-state :slugs))
  (print @app-state)
  (print (coin-flip))
  (has-slug-changed?))