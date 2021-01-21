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
  [lang-or-nil]
  (client/fetch-batch lang-or-nil)
  (master-view))

(defn submit-login
  "gather values from login box and submit to server"
  []
  (let [[un pw] (mapv utils/id->value ["un" "pw"])]
    (client/login {:username un
                   :password pw}
                  (partial client/login-handler set-master-view))))


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
   ])

(defn def-view
  "view with defs and associatedbuttons"
  []
  [:div#top
   [menu]
   [info/info-panel]
   [def-panel]])

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
  (print "master view called")
  (print "app state" @dbs/app-state)
  (let [{:keys [message-showing? logged-in?]} @dbs/app-state]
    
    (if message-showing?
      (render-view (message-view))
      (if (not logged-in?)
        (render-view (login/login-box submit-login))
        (render-view (def-view))))))

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
  @app-state
  (. js/document -location)
  #_(make-filemenu-body (:files @app-state))
  @def-panel-state
  (:defs-loading? @def-panel-state)
  (:def-showing? @def-panel-state)
  (master-view)
  (render-view (def-view))
  (.open js/window "https://www.dict.cc/?s=schalten", "_blank")
  ; (defn example []
  ;   [:<> [:h1 "joe"] [:h2 "sent me"]])
  )
