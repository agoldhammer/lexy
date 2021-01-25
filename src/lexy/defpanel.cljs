(ns lexy.defpanel
  (:require [lexy.dbs :as dbs]
            [lexy.actions :as ax]
            [lexy.client :as client]
            [lexy.utils :refer [tagged-text]]
            [lexy.cmpts :refer [lkup-button]]))

(def DEBUG false)

;; TODO fixthis
(defn- modify-slug
  "called by word-box when any part of slug is changed"
  [id event]
  #_(print "slug changed called" id (.. event -target -value))
  (dbs/modify-current-slug id (.. event -target -value))
  (dbs/set-slug-changed true))

(defn word-box
  "element for displaying word def, and supplement"
  [id myword]
  #_(print "word-box called with" id myword)
  [:div.control.my-3.ml-4.mr-6
   [:input.input.is-medium.is-primary.mx-2.is-size-4
    {:id id
     :value myword
     :type "text"
     :on-change #(modify-slug id %)}]])

(defn check-wordbox
  "get value of element with specified id"
  [id]
  (let [elt (. js/document getElementById id)]
    (print "value" (.-value elt))))


(defn score-panel
  "display score"
  [wid]
  (let [{:keys [sid lrndsrc
                lrndtgt nseen]} @(dbs/current-score)]
    #_(print "score-panel:" lrndsrc lrndtgt nseen score)
    [:div-level.is-size-8.is-italic.has-text-info
     (tagged-text "wid" wid)
     (tagged-text "sid" sid)
     (tagged-text "Learned fwd" lrndsrc)
     (tagged-text "Learned bwd" lrndtgt)
     (tagged-text "Times seen" nseen)]))

(defn button-array [def-showing?]
  [:div.field.is-grouped
              (if (not def-showing?)
                [:div.field.is-grouped
                 (ax/show-def-button)
                 (ax/previous-word-button)]
                [:div.field.is-grouped
                 (ax/right-button)
                 (ax/wrong-button)])])

(defn lkup-array [lang src target flipped]
  [:div.field.is-grouped  ;; else def-showing? is false
                  ;; when lang is "italian", :other = :reit
                  ;; when lang is "german", :other = :glosbe
   (when (not= lang "italian")
     (list
      (lkup-button src target lang :dict-cc :fwd flipped)
      (lkup-button src target lang :dict-cc :rev flipped)))
   (lkup-button src target lang :other :fwd flipped)
   (lkup-button src target lang :other :rev flipped)])

(defn def-panel
  "view with word and defs"
  []
  (let [{:keys [slugs flipped cursor def-showing? defs-loading?]} @dbs/def-panel-state
        slug (nth slugs cursor nil)
        ;; unflipped src0 and target0 go to buttons
        ;; [src0, target0] ((juxt :src :target) slug)
        srcid (if flipped "target" "src")
        targetid (if flipped "src" "target")
        [wid, src, target, supp] (if (not flipped)
                                   ((juxt :wid :src :target :supp) slug)
                                   ((juxt :wid :target :src :supp) slug))
        logged-in? [:logged-in? @dbs/app-state]
        lang (:lang @dbs/app-state)]
    (when DEBUG
      (print "def-panel: " defs-loading? slug cursor
             (first slugs)))
    #_(print "in def-panel: flipped " flipped) 
    (when (nil? @(dbs/current-score))
      (client/fetch-score wid)) 
    (if defs-loading?
      [:div [:span "Defs loading"]]
      ;; else not loading
      (if logged-in?
        (if slug
          [:div.field.ml-2.mr-10
           [score-panel wid]
           [word-box srcid src] ;; this is the word to be defined
           ;; even if supp is blank, show box, so can be edited
           (when def-showing?
             [:<>
              [word-box targetid target] ;; this is the definition
              [word-box "supp" (:supp supp)]]) ;; this is the supplement
             ;; showdef/prevword or right/wrong depending on state
           [button-array def-showing?]
           (when def-showing?
             ;; right wrong
             ;; lookup buttons
             [lkup-array lang src target flipped])]
        ;; else if slug is nil
          [:div
           (ax/fetch-more-button)
           (ax/logout-button)])
;; not logged in
        [:div "Internal error: def-panel-view called when not logged in"]))))

(comment
  (check-wordbox "src")
  (check-wordbox "target"))
