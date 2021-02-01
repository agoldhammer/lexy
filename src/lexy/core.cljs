(ns lexy.core
  (:require [lexy.actions :refer [msg-dismiss-action]]
            [lexy.client :as client]
            [lexy.defpanel :refer [def-panel]]
            [lexy.dbs :as dbs]
            [lexy.infopanel :as info]
            [lexy.login :as login]
            [lexy.message :refer [message-box]]
            [lexy.utils :as utils]
            [reagent.dom :as rdom]))

;; forward defs
(declare render-view)
(declare def-view)
(declare master-view)

;; state helper fns
;; ----------------


(defn set-master-view
  "set active db in app-state and initiate the master view, showing word def"
  []
  (client/fetch-batch)
  (master-view))

(defn submit-login
  "gather values from login box and submit to server"
  []
  (let [[un pw] (mapv utils/id->value ["un" "pw"])]
    (client/login {:username un
                   :password pw}
                  (partial client/login-handler set-master-view))))

(defn mode-button
  "switch between practice and addvocab modes"
  []
  (let [adding-vocab? (:addvocab-showing?  @dbs/app-state)
        text (if adding-vocab? "Practice" "Add Vocab")]
    [:button.navbar-item.button.is-ghost.is-small.mt-3.ml-6.has-text-danger
     text])
  )


(defn menu
  "fixed menu view"
  []
  [:nav.navbar.is-primary.is-full
   {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item.is-size-4
     [:i.fas.fa-bookmark.ml-2]]
    [:a.navbar-item.has-text-danger.is-size-4.is-italic
     {:href "#"} "lexy"]
    [mode-button]
    [:a.navbar-burger.burger {:role "button"
                              :aria-label "menu"
                              :aria-expanded "false"}
     [:span {:aria-hidden "true"}]
     [:span {:aria-hidden "true"}]
     [:span {:aria-hidden "true"}]]]
   ])

(defn def-view
  "view with defs and associatedbuttons
   or: addvocab view"
  []
  (let [adding-vocab? (:addvocab-showing? @dbs/app-state)]
    [:div#top
     [menu]
     [info/info-panel]
     (print adding-vocab?)
     (if (not  adding-vocab?)
       [def-panel]
       [:p "change this to addvocab panel"])]))

(defn message-view
  "display modal message box"
  []
  (let [text (:message-text @dbs/app-state)]
    (message-box text true #(msg-dismiss-action master-view))))

(defn render-view
  "render a defined view"
  [view]
  (rdom/render view (. js/document (getElementById "app"))))

(defn master-view
  []
  #_(print "master view called")
  #_(print "app state" @dbs/app-state)
  (let [{:keys [message-showing? logged-in?]} @dbs/app-state]
    
    (if message-showing?
      (render-view [message-view])
      (if (not logged-in?)
        (render-view [login/login-box submit-login])
        (render-view [def-view])))))

(defn start
  "render the initial view"
  []
  (dbs/reset-def-panel!)
  (master-view))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

#_{:clj-kondo/ignore [:unused-public]}
(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (dbs/reset-def-panel!)
  (dbs/reset-app-state!)
  (master-view)
  #_#(.open js/window "/"))
  (js/console.log "stop")

;; stuff to stop spurious warnings
(when nil
  (stop)
  (init))



(comment
  @dbs/app-state
  (. js/document -location)
  #_(make-filemenu-body (:files @app-state))
  @dbs/def-panel-state
  (:defs-loading? @dbs/def-panel-state)
  (:def-showing? @dbs/def-panel-state)
  (master-view)
  (render-view (def-view))
  (.open js/window "https://www.dict.cc/?s=schalten", "_blank")
  ; (defn example []
  ;   [:<> [:h1 "joe"] [:h2 "sent me"]])
  )
