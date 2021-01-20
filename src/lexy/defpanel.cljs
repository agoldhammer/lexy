(ns lexy.defpanel
  (:require [lexy.dbs :as dbs]
            [lexy.actions :as ax]
            [lexy.utils :refer [tagged-text]]
            [lexy.cmpts :refer [lkup-button]]
            #_[lexy.utils :as utils]))

(def DEBUG false)

(defn word-box
  "element for displaying word def, and supplement"
  [myword]
  [:div.control.my-3.ml-4.mr-6
   [:input.input.is-medium.is-primary.mx-2.is-size-4
    {:value myword
     :type "text"
     :on-change #()}]])

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
        lang (:active-file @dbs/app-state)]
    (when DEBUG
      (print "def-panel: " defs-loading? slug cursor
             (first slugs)))
    ;; (print "wid: " wid)  
    (if defs-loading?
      [:div [:span "Defs loading"]]
      ;; else not loading
      (if logged-in?
        (do
          (print "Def panel logged in")
          (if slug
            [:div.field.ml-2.mr-10
             [:div-level.is-size-8.is-italic.has-text-info
              (tagged-text "wid" wid)]
             (word-box src)
             (when def-showing?
               (word-box target))
             (when (and def-showing?
                        (not= supp ""))
               (word-box (:supp slug)))
             [:div.field.is-grouped
              (if (not def-showing?)
                [:div.field.is-grouped
                 (ax/show-def-button)
                 (ax/previous-word-button)]
                [:div.field.is-grouped
                 (ax/right-button)
                 (ax/wrong-button)])]
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
                (lkup-button target0 lang :other :rev)])]
        ;; else if slug is nil
            [:div
             (ax/fetch-more-button)
             (ax/logout-button)]))
;; not logged in
        [:div "not logged in"]))))
