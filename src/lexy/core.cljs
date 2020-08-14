(ns lexy.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            #_[reagent.session :as session]
            #_[require reagent.cookies :as cookies]
            [lexy.client :as client]
            [lexy.message :refer [message-box]]))

(def DEBUG false)

;; forward defs
#_(declare picker-view)
(declare render-view)
(declare def-view)
(declare master-view)
(declare stop)

;; for development
(defrecord Slug [rowid src target supp lrd-from lrd-to nseen])

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (reagent/atom {:active-file nil
                                  :total 0
                                  :batch-size 50
                                  :direction :fwd
                                  :logged-in? false
                                  :message-showing? false
                                  :message-text ""
                                  :login-showing? true}))

(defonce default-panel-state {:slugs []
                              :cursor 0
                              :dir 0
                              :defs-loading? true
                              :def-showing? false})

(def def-panel-state (reagent/atom default-panel-state))

;; state helper fns
;; ----------------

(defn set-message-flag-and-text
 "set message-showing? flag in app-state, with text if needed"
 ([t-or-f]
  (set-message-flag-and-text t-or-f ""))
  ([t-or-f text]
   (swap! app-state merge {:message-showing? t-or-f
                           :message-text text})))

(defn set-login-showing!
  "set login-showing? flag in app-state"
  [t-or-f]
  (swap! app-state assoc :login-showing? t-or-f))

(defn previous-word!
  "set cursor back 1"
  []
  (let [cur (:cursor @def-panel-state)]
    (swap! def-panel-state assoc-in [:cursor] (max 0 (dec cur)))))

;; TODO: add a login failed element
(defn close-login-box!
  "set :login-showing? flag to false"
  []
  (swap! app-state assoc :login-showing? false)
  #(.open js/window "/"))

(defn reset-def-panel! []
  (reset! def-panel-state default-panel-state))

(defn set-def-showing!
  "set def-showing? in def-panel-state"
  [t-or-f]
  (swap! def-panel-state assoc :def-showing? t-or-f))

(defn slug-handler
  "set slugs in def-panel-state"
  [response]
  #_(when DEBUG (print "slug-handler: resp: " response))
  (let [slugs (mapv #(apply ->Slug %) (:slugs response))
        response1 (merge response {:slugs slugs})
        new-state (merge response1 {:cursor 0
                                    :defs-loading? false
                                    :def-showing? false})]
    (when DEBUG
      (print "slug-handler: 2slugs: " (take 2 slugs))
      (print "loading: " (:defs-loading? new-state)))
    ;; clear out any slugs remaining from previous log
    #_(print "slughandler: before: " (take 2 (:slugs def-panel-state)))
    (swap! def-panel-state assoc :slugs [])
    (when DEBUG 
      (print "d-p-s slugs: shd be empty: " (take 2 (:slugs def-panel-state)))
      (print "new state" (take 2 (:slugs new-state))))
    (swap! def-panel-state merge new-state)))

(defn set-active-file
  "set active file name in app-state"
  [fname]
  (swap! app-state assoc-in [:active-file] fname)
  #_(client/set-db fname)
  (client/get-endpoint (str "/fetch") slug-handler)
  (print "set-active-file done")
  (master-view))

(defn have-active-file?
  "has active file been set in app-state?"
  []
  (not (nil? (:active-file @app-state))))

;; view fns
;; --------

(defn tagged-text
  "elt of info panel
    displays tag with text if not nil, else none"
  [tag text-or-nil]
  (if-let [text text-or-nil]
    [:span.ml-4 (str tag ": " text)]
    [:span.ml-4 (str tag ": None")]))

(defn info-panel
  "panel displaying info about current active settings"
  []
  (let [{:keys [active-file
                batch-size
                direction
                total]} @app-state]
    [:div-level.is-size-7.is-italic.has-text-info
     (tagged-text "Active file" active-file)
     (tagged-text "Batch size" batch-size)
     (tagged-text "Dir" (name direction))
     (tagged-text "Lexicon size" total)]))

(defn word-box
  "element for displaying word def, and supplement"
  [myword]
  [:div.control.my-3.ml-4.mr-6
   [:input.input.is-medium.is-primary.mx-2.is-size-4
      {:value myword
       :type "text"
       :on-change #()}]])

(defn bump-cursor
  "bump cursor on slugs list in def-panel-state"
  []
  (swap! def-panel-state update-in [:cursor] inc))

(defn right-action
  "on clicking right button"
  []
  (set-def-showing! false)
  (swap! def-panel-state assoc :dir (rand-int 2))
  (bump-cursor))

(defn wrong-action
  "on clicking wrong button"
  []
  (set-def-showing! false)
  (swap! def-panel-state assoc :dir (rand-int 2))
  (bump-cursor))

(defn show-def-button []
  [:button.button.is-rounded.is-warning
   {:on-click #(set-def-showing! true)}
   "ShowDef"])

(defn previous-word-button []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click previous-word!}
   "Previous word"])

(defn right-button []
  [:button.button.is-rounded.is-success.ml-4
   {:on-click right-action}
   "Right"])

(defn wrong-button []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click wrong-action}
   "Wrong"])

(defn open-dictcc-button [src]
  [:button.button.is-rounded.has-background-light.ml-2.is-small
   {:on-click #(.open js/window
                      (str "https://www.dict.cc/?s=" src)
                      "_blank")}
   "Lkup dict.cc"])

(defn open-glosbe-button [src glosbe-url]
  [:button.button.is-rounded.has-background-light.ml-4.is-small
   {:on-click #(.open js/window
                      (str glosbe-url src)
                      "_blank")}
   "Lkup Glosbe"])

(defn fetch-more-button []
  [:button.button.is-rounded.is-success.ml-4
   {:on-click #(set-active-file (:active-file @app-state))}
   "Done, fetch more"])

;; TODO: add logout endpoint
(defn logout-button []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click #(.open js/window "/")}
   "Logout"])

(defn def-panel
  "view with word and defs; choose dir randomly"
  []
  (let [{:keys [slugs dir cursor def-showing? defs-loading?]} @def-panel-state
        slug (nth slugs cursor nil)
        [src, target, supp] (if (= dir 0)
                              ((juxt :src :target :supp) slug)
                              ((juxt :target :src :supp) slug))
        logged-in? [:logged-in? @app-state]
        glosbe-url (if (= dir 0)
                     "https://glosbe.com/de/en/"
                     "https://glosbe.com/en/de/")]
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
                   (show-def-button)
                   (previous-word-button)]
                  [:div.field.is-grouped
                   (right-button)
                   (wrong-button)])]
               [:div.field.is-grouped  ;; else def-showing? is false
                (open-dictcc-button src)
                (open-glosbe-button src glosbe-url)]]
        ;; else if slug is nil
              [:div 
               (fetch-more-button)
               (logout-button)]))
;; not logged in
          [:div "not logged in"]))))

(defn id->value
  "get value of element with specified id"
  [id]
  (let [elt (. js/document getElementById id)]
    (.-value elt)))

(declare set-active-file)

(defn login-handler
  "handle response from the login endpoint"
  [response]
  (print "login-handler: " response)
  (if (= (:login response) "rejected")
    (do (print "bad login")
        (set-active-file nil)
        (set-message-flag-and-text true "Bad Login"))
    (do
      (set-active-file (:active-db response))
      (swap! app-state merge {:logged-in? true
                              :total (:total response)})
      (set-def-showing! false)
      (print "good login")))
  (close-login-box!)
  (master-view))

(defn submit-login
  "gather values from login box and submit to server"
  []
  (let [[un pw lang] (mapv id->value ["un" "pw" "lang"])]
    #_(print un pw lang)
    #_(set-active-file lang)
    (client/login {:username un
                   :password pw
                   :lang lang}
                  login-handler)))

(defn login-box
  "login element"
  []
  (let [login-showing? (:login-showing? @app-state)]
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
             [:option "Test"]]]]]]

        [:footer.modal-card-foot
         [:button.button.is-success
          {:on-click submit-login} "Login"]
         [:button.button
          {:on-click close-login-box!} "Cancel"]]]])))

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
   #_[:div.navbar-menu
      [:div.navbar-start
       [:a.navbar-item {:href "#"
                        :on-click #(populate-files :de)}
        "German"]
       [:a.navbar-item {:href "#"
                        :on-click #(populate-files :it)}
        "Italian"]
       [:a.navbar-item {:href "#"
                        :on-click #(populate-files :test)}
        "Test"]]]])

#_(defn start-panel
  "initial view before active file chosen"
  []
  [:div
   [login-box]
   [menu]
   [info-panel]])

(defn def-view
  "view with defs and associatedbuttons"
  []
  [:div#top
   [menu]
   [info-panel]
   [def-panel]])

;; TODO: should vary with type of message, now does nothing
(defn msg-dismiss-action
  "what to do when message dissmissed"
  []
  (print "msg-dismiss-action")
  nil)

(defn message-view
  "display modal message box"
  []
  (let [text (:message-text @app-state)]
    (message-box text true msg-dismiss-action)))

(defn render-view
  "render a defined view"
  [view]
  (rdom/render view (. js/document (getElementById "app"))))

(defn master-view
  []
  (print "master view called")
  (let [{:keys [message-showing? logged-in?]} @app-state]
    
    (if message-showing?
      (render-view (message-view))
      (if (not logged-in?)
        (render-view (login-box))
        (render-view (def-view))))))

(defn start
  "render the initial view"
  []
  (reset-def-panel!)
  #_(render-view (start-panel))
  (master-view))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (reset-def-panel!)
  (js/console.log "stop"))



(comment
  @app-state
  (. js/document -location)
  #_(make-filemenu-body (:files @app-state))
  (:defs-loading? @def-panel-state)
  (:def-showing? @def-panel-state)
  (master-view)
  (render-view (def-view))
  (.open js/window "https://www.dict.cc/?s=schalten", "_blank"))

;; https://stackoverflow.com/questions/42142239/how-to-create-a-appendchild-reagent-element))

;; NEXT STEPS implement batch size, display counts. deal with fwd and bkwd
;; 
;; 
#_(defn make-filemenu-entry
    "helper to create file table row"
    [fname]
    [:tr [:th ^{:key fname}
          {:id fname
           :on-click #(set-active-file (-> % .-target .-id))}
          fname]])

#_(defn make-filemenu-body
    "helper to create file table body"
    [files]
    (into [:tbody] (map make-filemenu-entry files)))

#_(defn file-picker
    "view for choosing files"
    []
    (let [files (:files @app-state)]
      [:div#picker.columns.mt-2
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

#_(defn picker-view
    "view with file-picker showing"
    []
    [:div#top
     [menu]
     [info-panel]
     [:div#picker
      [file-picker]]])

#_(defn file-list-handler [response]
    (when DEBUG (print response))
    (swap! app-state merge response))

#_(defn populate-files
    "add nemes of available files to the db"
    [lang]
    (let [handler file-list-handler
          pattern (cond
                    (= lang :de) "german"
                    (= lang :it) "italian"
                    (= lang :test) "test")]
      (client/get-endpoint (str "/files/" pattern) handler))
    (render-view picker-view))