(ns chart.core
  (:require [reagent.core :as reagent :refer [atom]]
            ["react-vis" :as rvis]))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(def chart-data [{:x 1 :y 1}
                 {:x 2 :y 2}
                 {:x 3 :y 4}
                 {:x 4 :y 5}
                 {:x 5 :y 4}
                 {:x 6 :y 6}
                 {:x 7 :y 8}
                 {:x 8 :y 6}
                 {:x 9 :y 5}
                 {:x 10 :y 5}])


(defn line-chart [data]
  [:> rvis/XYPlot
   {:width 800 :height 225}
   [:> rvis/LineSeries {:data data}]])

(defn app-scaffold []
  [:div
   [line-chart chart-data]])

(defn render-app []
  (reagent/render [app-scaffold]
            (.getElementById js/document "app")))

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this and watch it change!!!"]
   [app-scaffold]])

(defn start []
  (reagent/render-component [hello-world]
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
