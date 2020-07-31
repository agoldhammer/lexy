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
                          :direction :fwd
                          :slugs fake-defslugs
                          :cursor 0}))

(defn populate-files [lang]
  (if (= lang :de)
    (swap! app-state assoc :files ["ger1" "ger2" "ger3"])
    (swap! app-state assoc :files ["ital1" "ital2" "ital3" "ital4"])))

#_(defn hello-world []
  [:section.section
   [:div.level
    [:button.button.is-rounded.level-item.mr-1 (:text @app-state)]
    [:button.button.level-item.ml-1 "Edit this and watch it change!!!"]]
   [:div.container
    [:p "hello"]
    [:button.button.is-success.is-rounded "txt"]]])

(defn info-panel []
  (let [state  @app-state
        active-file (:active-file state)
        batch-size (:batch-size state)
        direction (:direction state)]
    [:div-level.is-size-7.is-italic.has-text-info
     [:span.ml-4 "Active file: "]
     (if active-file
       [:span active-file]
       [:span "None"])
     [:span.ml-4 (str "Batch size: " batch-size)]
     [:span.ml-4 (str "Dir: "  (name direction))]]))

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
     [:a.navbar-item {:href "#"
                      :on-click (partial populate-files :de)}
      "German"]
     [:a.navbar-item {:href "#"
                      :on-click (partial populate-files :it)}
      "Italian"]]]])

(defn make-filemenu-entry [fname]
  [:tr [:th ^{:key fname}
        {:id fname
         :on-click #(js/console.log (-> % .-target .-id))}
        fname]])

(defn make-filemenu-body [files]
  (into [:tbody] (map make-filemenu-entry files)))


(defn file-picker []
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

(defn start []
  (rdom/render [:div
                [menu]
                [info-panel]
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



(comment
  @app-state
  (make-filemenu-body (:files @app-state))
  )

