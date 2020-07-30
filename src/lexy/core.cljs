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
    [:button.button.is-success.is-rounded "txt"]]
   #_[app-scaffold]])

(defn menu []
  [:nav.navbar.is-primary
   {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item {:href "#"} "lexy"]
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

(defn start []
  (rdom/render [menu] #_[hello-world]
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
