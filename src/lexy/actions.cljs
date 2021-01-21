(ns lexy.actions
  (:require [lexy.dbs :as dbs]))

(defn bump-cursor
  "bump cursor on slugs list in def-panel-state"
  []
  (swap! dbs/def-panel-state update-in [:cursor] inc))

(defn right-action
  "on clicking right button"
  []
  (dbs/set-def-showing! false)
  (swap! dbs/def-panel-state assoc :dir (rand-int 2))
  (bump-cursor))

(defn wrong-action
  "on clicking wrong button"
  []
  (dbs/set-def-showing! false)
  (swap! dbs/def-panel-state assoc :dir (rand-int 2))
  (bump-cursor))

(defn show-def-button []
  [:button.button.is-rounded.is-warning
   {:on-click #(dbs/set-def-showing! true)}
   "ShowDef"])

(defn previous-word-button []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click dbs/previous-word!}
   "Previous word"])

(defn right-button []
  [:button.button.is-rounded.is-success.ml-4
   {:on-click right-action}
   "Right"])

(defn wrong-button []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click wrong-action}
   "Wrong"])

(defn fetch-more-button []
  [:button.button.is-rounded.is-success.ml-4
   {:on-click (print "fetch more,what to do???")}
   "Done, fetch more"])

;; TODO: add logout endpoint
(defn logout-button []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click #(.open js/window "/")}
   "Logout"])

;; TODO: should vary with type of message, now does nothing
(defn msg-dismiss-action
  "what to do when message dissmissed"
  [view-fn]
  (print "msg-dismiss-action")
  (dbs/set-message-flag-and-text false)
  (view-fn))
