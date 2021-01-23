(ns lexy.defpanel
  (:require [lexy.dbs :as dbs]
            [lexy.actions :as ax]
            [lexy.client :as client]
            [lexy.utils :refer [tagged-text]]
            [lexy.cmpts :refer [lkup-button]]
            [reagent.core :as reagent]))

(def DEBUG false)

(defn word-box
  "element for displaying word def, and supplement"
  [_ myword]
  (let [word-atom (reagent/atom myword)]
    (fn [id myword]
      [:div.control.my-3.ml-4.mr-6
       [:input.input.is-medium.is-primary.mx-2.is-size-4
        {:id id
         :defaultValue myword
         :value @word-atom
         :type "text"
         :on-change #(reset! word-atom (-> % .-target .-value))}]])))

;; #_(defn word-box
;;   "element for displaying word def, and supplement"
;;   [myword]
;;     [:div.control.my-3.ml-4.mr-6
;;      [:input.input.is-medium.is-primary.mx-2.is-size-4
;;       {;; :placeholder myword
;;        :value myword
;;        :type "text"
;;        }]])


(defn score-panel
  "display score"
  [wid]
  (let [{:keys [sid lrndsrc
                lrndtgt nseen] :as score} (dbs/get-current-score)]
    (print "score-panel:" lrndsrc lrndtgt nseen score)
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

(defn lkup-array [lang src0 def-showing? target0]
  (when def-showing?
               ;; TODO: fix this!
               [:div.field.is-grouped  ;; else def-showing? is false
                  ;; when lang is "italian", :other = :reit
                  ;; when lang is "german", :other = :glosbe
                (when (not= lang "italian")
                  (list
                   (lkup-button src0 lang :dict-cc :fwd)
                   (lkup-button target0 lang :dict-cc :rev)))
                (lkup-button src0 lang :other :fwd)
                (lkup-button target0 lang :other :rev)]))

(defn def-panel
  "view with word and defs; choose dir randomly"
  []
  (let [{:keys [slugs dir cursor def-showing? defs-loading?]} @dbs/def-panel-state
        slug (nth slugs cursor nil)
        ;; unflipped src0 and target0 go to buttons
        [src0, target0] ((juxt :src :target) slug)
        [wid, src, target, supp] (if (= dir 0)
                                   ((juxt :wid :src :target :supp) slug)
                                   ((juxt :wid :target :src :supp) slug))
        logged-in? [:logged-in? @dbs/app-state]
        lang (:lang @dbs/app-state)]
    (when DEBUG
      (print "def-panel: " defs-loading? slug cursor
             (first slugs)))
    ;; (print "wid: " wid) 
    (when (nil? (dbs/get-current-score))
      (client/fetch-score wid)) 
    (if defs-loading?
      [:div [:span "Defs loading"]]
      ;; else not loading
      (if logged-in?
        (do
          (print "Def panel logged in")
          (if slug
            [:div.field.ml-2.mr-10
             (score-panel wid)
             [word-box "src" src] ;; this is the word to be defined
             (when def-showing?
               [word-box "tgt" target]) ;; this is the definition
             (when (and def-showing?
                        (not= supp ""))
               [word-box "supp" (:supp slug)]) ;; this is the supplement
             ;; showdef/prevword or right/wrong depending on state
             (button-array def-showing?)
             ;; right wrong
             ;; lookup buttons
             (lkup-array lang src0 def-showing? target0)]
        ;; else if slug is nil
            [:div
             (ax/fetch-more-button)
             (ax/logout-button)]))
;; not logged in
        [:div "not logged in"]))))
