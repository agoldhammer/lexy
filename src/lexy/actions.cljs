(ns lexy.actions
  (:require [lexy.client :as client]
            [lexy.dbs :as dbs]))

(defn bump-cursor
  "bump cursor on slugs list in def-panel-state"
  []
  (dbs/set-def-showing! false)
  (swap! dbs/def-panel-state assoc :flipped (dbs/coin-flip))
    ;; modify score as appropriate

  (when (dbs/has-slug-changed?)
    (client/update-slug (dbs/get-current-slug)))

  (dbs/set-slug-changed false) ;; reset the slug changed flag
  (swap! dbs/def-panel-state update-in [:cursor] inc))

(defn- modify-score
  "inc nseen, modify lrnd flag as needed; call w true if right, false if wrong"
  [right?]
  (let [score-cursor (dbs/current-score)
        {:keys [nseen] :as score} @score-cursor
        flipped? (dbs/is-flipped?)]
    #_(print "score before modification" score "right?" right?
           "flipped?" flipped?)
    (let [temp-score (merge score {:nseen (inc nseen)})
          new-score (if flipped?
                      (if right?
                        (merge temp-score {:lrndtgt 1})
                        (merge temp-score {:lrndtgt 0}))
                      (if right?
                        (merge temp-score {:lrndsrc 1})
                        (merge temp-score {:lrndsrc 0})))]
      #_(print "score after modification" new-score)
      (client/update-score new-score))
    (reset! score-cursor nil) ;; reset score to trigger next fetch
    (bump-cursor)))

(defn right-action
  "on clicking right button"
  []
  (modify-score true))

(defn wrong-action
  "on clicking wrong button"
  []
  (modify-score false))

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

(defn fetch-more-button []
  [:button.button.is-rounded.is-warning.ml-4
   {:on-click client/fetch-batch}
   "Done, fetch more"])

(defn shuffle-slugs
  "reorder the current set of slugs"
  []
  (let [current-slugs (:slugs @dbs/def-panel-state)]
    (swap! dbs/def-panel-state assoc :slugs (shuffle current-slugs))
    (swap! dbs/def-panel-state assoc :cursor 0)))

(defn repeat-current-batch-button []
  [:button.button.is-rounded.is-success.ml-4
   {:on-click #(shuffle-slugs)}
   "Repeat"])

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
