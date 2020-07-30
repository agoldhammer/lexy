(ns lexy.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn hello-world []
  [:section.section
   [:div.level
    [:button.button.is-rounded.level-item.mr-1 (:text @app-state)]
    [:button.button.level-item.ml-1 "Edit this and watch it change!!!"]]
   [:div.container
    [:p "hello"]
    [:button.button.is-success.is-rounded "txt"]]])

(defn word-box [myword]
  [:div.defholder.mb-2
   [:span.tag.is-size-4.dark myword]])

(defn def-panel []
  (let [w "word"
        d "definition"
        s "supplement"]
    [:div.content
     (word-box w)
     (word-box d)
     (word-box s)
     [:div.field.is-grouped
      [:button.button.is-rounded.is-warning
       {:on-click #(js/console.log "clicked")}
       "ShowDef"]
      [:button.button.is-rounded.is-success "Right"]]]))

(defn menu []
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
     [:a.navbar-item {:href "#"} "German"]
     [:a.navbar-item {:href "#"} "Italian"]]]])

(defn file-picker []
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
       [:tbody
        [:tr [:th "File1"]]
        [:tr [:th "file2"]]]]]]]])

(defn start []
  (rdom/render [:div
                [menu]
                [file-picker]
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
