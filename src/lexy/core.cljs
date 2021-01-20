(ns lexy.core
  (:require ; [reagent.core :as reagent]
   [reagent.dom :as rdom]
   #_[reagent.session :as session]
   #_[require reagent.cookies :as cookies]
   [lexy.actions :as ax]
   [lexy.client :as client]
   [lexy.dbs :as dbs]
   [lexy.infopanel :as info]
   [lexy.message :refer [message-box]]
   [lexy.cmpts :refer [lkup-button]]))

(def DEBUG false)

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

;; view fns
;; --------


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
        [src, target, supp] (if (= dir 0)
                              ((juxt :src :target :supp) slug)
                              ((juxt :target :src :supp) slug))
        logged-in? [:logged-in? @dbs/app-state]
        lang (:active-file @dbs/app-state)]
    (when DEBUG
      (print "def-panel: " defs-loading? slug cursor
             (first slugs)))
    (if defs-loading?
      [:div [:span "Defs loading"]]
      ;; else not loading
      (if logged-in?
          (do
            (print "Def panel logged in")
            (if slug
              [:div.field.ml-2.mr-10
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

(defn id->value
  "get value of element with specified id"
  [id]
  (let [elt (. js/document getElementById id)]
    (.-value elt)))

(defn login-handler
  "handle response from the login endpoint"
  [response]
  (print "login-handler: " response)
  (if (= (:login response) "rejected")
    (do (print "bad login")
        (dbs/set-language! nil)
        (swap! dbs/app-state assoc :logged-in? false)
        (dbs/set-message-flag-and-text true "Bad Login"))
    (do
      #_(dbs/set-language! (:active-db response))
      (swap! dbs/app-state merge {:logged-in? true
                              :total (:total response)})
      (dbs/set-def-showing! false)
      (print "good login")))
  (dbs/close-login-box!)
  (set-master-view (:active-db response)))

(defn submit-login
  "gather values from login box and submit to server"
  []
  (let [[un pw lang] (mapv id->value ["un" "pw" "lang"])]
    (client/login {:username un
                   :password pw
                   :lang lang}
                  login-handler)))

(defn login-box
  "login element"
  []
  (let [login-showing? (:login-showing? @dbs/app-state)]
    (when login-showing?
      [:div.modal.is-active
       [:div.modal-background.has-background-light-gray]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Lexy Login"]]
        [:section.modal-card-body
         [:div.field
          [:label.label "Username"]
          [:div.control
           [:input#un.input {:type "text"
                             :placeholder "username"}]]
          [:label.label "Password"]
          [:div.control
           [:input#pw.input {:type "password"
                             :placeholder "password"}]]
          [:label.label "Language"]
          [:div.select
           [:div.control
            [:select#lang
             [:option "German"]
             [:option "Italian"]
             [:option "redux2"]]]]]]

        [:footer.modal-card-foot
         [:button.button.is-success
          {:on-click submit-login} "Login"]
         [:button.button
          {:on-click dbs/close-login-box!} "Cancel"]]]])))

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

;; TODO: should vary with type of message, now does nothing
(defn msg-dismiss-action
  "what to do when message dissmissed"
  []
  (print "msg-dismiss-action")
  (dbs/set-message-flag-and-text false)
  (master-view))

(defn message-view
  "display modal message box"
  []
  (let [text (:message-text @dbs/app-state)]
    (message-box text true msg-dismiss-action)))

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
        (render-view (login-box))
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
  (master-view)
  #(.open js/window "/"))
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
