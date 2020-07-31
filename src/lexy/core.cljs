(ns lexy.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]))

;; for development
(defrecord Slug [rowid src target supp lrd-from lrd-to nseen])

(def a (Slug. 1 "das Wort" "word" "" 0 0 0))
(def b (Slug. 2 "witzig" "witty" "Er war witzig" 1 0 15))

(def fake-defslugs [a b])

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:active-file nil
                          :batch-size 25
                          :direction :fwd}))

(def def-panel-state (atom {:slugs fake-defslugs
                            :cursor 0
                            :def-showing? false}))

(defn populate-files
  "add nemes of available files to the db"
  [lang]
  (if (= lang :de)
    (swap! app-state assoc :files ["ger1" "ger2" "ger3"])
    (swap! app-state assoc :files ["ital1" "ital2" "ital3" "ital4"])))

(defn info-panel
  "panel displaying info about current active settings"
  []
  (let [{:keys [active-file
                batch-size
                direction]} @app-state]
    [:div-level.is-size-7.is-italic.has-text-info
     [:span.ml-4 "Active file: "]
     (if active-file
       [:span active-file]
       [:span "None"])
     [:span.ml-4 (str "Batch size: " batch-size)]
     [:span.ml-4 (str "Dir: "  (name direction))]]))

(defn word-box
  "element for displaying word def, and supplement"
  [myword]
  [:div.defholder.mb-2.has-background-white-ter.mr-6
   [:span.tag.is-size-4.dark myword]])

(defn toggle-def-showing
  "Toggle def-showing? in def-panel-state"
  []
  (swap! def-panel-state update-in [:def-showing?] not))

(defn bump-cursor
  "bump cursor on slugs list in def-panel-state"
  []
  (swap! def-panel-state update-in [:cursor] inc))

(defn right-action
  "on clicking right button"
  []
  (toggle-def-showing)
  (bump-cursor))

(defn wrong-action
  "on clicking wrong button"
  []
  (toggle-def-showing)
  (bump-cursor))

(defn def-panel
  "view with word and defs"
  []
  (let [{:keys [slugs cursor def-showing?]} @def-panel-state
        slug (nth slugs cursor nil)]
    [:div.content.ml-2.mr-10
     (word-box (:src slug))
     (when def-showing?
       (word-box (:target slug)))
     (when def-showing?
       (let [supp (:supp slug)]
         (when (not= supp "")
           (word-box (:supp slug)))))
     [:div.field.is-grouped
      (if (not def-showing?)
        [:button.button.is-rounded.is-warning
         {:on-click toggle-def-showing}
         "ShowDef"]
        [:div.field.is-grouped
         [:button.button.is-rounded.is-success.ml-4
          {:on-click right-action}
          "Right"]
         [:button.button.is-rounded.is-danger.ml-4
          {:on-click wrong-action}
          "Wrong"]])]]))

(defn menu
  "fixed menu view"
  []
  [:nav.navbar.is-primary
   {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    [:div.level
     [:i.fas.fa-bookmark.ml-2.mt-5]
     [:a.navbar-item.mt-5.has-text-danger.is-size-4.is-italic
      {:href "#"} "lexy"]]
    [:a.navbar-burger.burger {:role "button"
                              :aria-label "menu"
                              :aria-expanded "false"}
     [:span {:aria-hidden "true"}]
     [:span {:aria-hidden "true"}]
     [:span {:aria-hidden "true"}]]]
   [:div.navbar-menu
    [:div.navbar-start
     [:a.navbar-item {:href "#"
                      :on-click (partial populate-files :de)}
      "German"]
     [:a.navbar-item {:href "#"
                      :on-click (partial populate-files :it)}
      "Italian"]]]])

(defn set-active-file
  "set active file name in app-state"
  [fname]
  (swap! app-state assoc-in [:active-file] fname))

(defn make-filemenu-entry
  "helper to create file table row"
  [fname]
  [:tr [:th ^{:key fname}
        {:id fname
         :on-click #(set-active-file (-> % .-target .-id))}
        fname]])

(defn make-filemenu-body
  "helper to create file table body"
  [files]
  (into [:tbody] (map make-filemenu-entry files)))


(defn file-picker
  "view for choosing files"
  []
  (let [files (:files @app-state)]
    [:div.columns.mt-2
     [:div.column.is-1]
     [:div.column.is-3
      [:table-container
       [:div.content
        [:table.table.is-bordered.is-hoverable
         [:thead
          [:tr
           [:th.has-text-info
            "File Name"]]]
         (make-filemenu-body files)]]]]]))

(defn have-active-file?
  "has active file been set in app-state?"
  []
  (not (nil? (:active-file @app-state))))

(defn start-panel
  "initial view before active file chosen"
  []
  [:div
   [menu]
   [info-panel]])

(defn start []
  (rdom/render
   [:div
    [menu]
    [info-panel]
    [:div#picker
     [file-picker]]
    [def-panel]
    #_[hello-world]]
   (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))



(comment
  @app-state
  (make-filemenu-body (:files @app-state)))

