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

(defn show-def-button
  "show the definition for the displayed word"
  []
  [:button.button.is-rounded.is-warning
   {:on-click #(dbs/set-def-showing! true)}
   "ShowDef"])

(defn previous-word-button
  "redisplay the previous word"
  []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click dbs/previous-word!}
   "Previous word"])

(defn right-button
  "user indicates right answer given"
  []
  [:button.button.is-rounded.is-success.ml-4
   {:on-click right-action}
   "Right"])

(defn wrong-button
  "user indicates wrong answer given"
  []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click wrong-action}
   "Wrong"])

;; TODO test this and fix
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
  "kill modal message box and display view defined by view-fn"
  [view-fn]
  (print "msg-dismiss-action")
  (dbs/set-message-flag-and-text false)
  (view-fn))
