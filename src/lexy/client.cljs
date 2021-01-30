(ns lexy.client
  (:require [ajax.core :as ajax]
            [lexy.dbs :as dbs]))

(declare debug-handler)
(declare error-handler)

(defrecord Slug [wid src target supp])

(def DEBUG false)

(defn- default-request-map
  []
  {:handler debug-handler
   :error-handler error-handler
   :timeout 1000
   :with-credentials true
   :response-format :json
   :keywords? true})


(defn- debug-handler
  "just log the response"
  [response]
  (print "Debug handler: " response)
  #_(.log js/console response))

(defn- error-handler [{:keys [status status-text]}]
  (print "lexy ajax error:" status status-text)
  #_(.log js/console (str "AJAX error" status " " status-text)))

(defn- slug-handler
  "set slugs in def-panel-state"
  [response]
  #_(print "slug-handler: resp: " (dissoc  response :slugs))
  (let [slugs (mapv #(apply ->Slug %) (:slugs response))
        response1 (merge response {:slugs slugs})
        new-state (merge response1 {:cursor 0
                                    :defs-loading? false
                                    :def-showing? false})]
    
    ;; clear out any slugs remaining from previous log
    #_(print "slughandler: before: " (take 2 (:slugs def-panel-state)))
    #_(swap! dbs/def-panel-state assoc :slugs [])
    #_(print "slug-handler new state" (dissoc new-state :slugs))
    (swap! dbs/def-panel-state merge new-state)))

(defn- fetch-score-handler
  "modify current-score in db"
  [response]
  #_(print (str "fsh: " response))
  (reset! (dbs/current-score) response))

(defn get-endpoint
  "get endpoint from vocab server"
  [endpoint handler]
  #_{:clj-kondo/ignore [:unresolved-var]}
  (ajax/GET endpoint
    (merge (default-request-map) {:handler handler})))

(defn post-endpoint
  "post endpoint from vocab server"
  [endpoint params handler]
  #_{:clj-kondo/ignore [:unresolved-var]}
  (ajax/POST endpoint
    (merge (default-request-map) {:handler handler
                                  :format :json
                                  :params params})))

(defn update-score
  "send the updated score to the backend server"
  [new-score]
  #_(print "update-score" new-score)
  (post-endpoint "/updatescore" new-score debug-handler))

(defn update-slug
  "send the updated slug to the backend server"
  [new-slug]
  #_(print "update-slug" new-slug)
  (post-endpoint "/updateslug" new-slug debug-handler))

(defn fetch-batch
  "get next batch of slugs"
  []
  #_(print "fetch-batch called")
  (dbs/set-defs-loading true)
  (get-endpoint (str "/fetch") slug-handler))

(defn fetch-score
  "get score for wid and current user"
  [wid]
  (when (not (nil? wid))
    #_(print "fetching score for wid: " wid)
    (get-endpoint (str "/getscore/" wid) fetch-score-handler)))

(defn login
  "send login data to server"
  [params handler]
  (post-endpoint "/login" params handler))

(defn login-handler
  "handle response from the login endpoint
   view fn will be set-master-view from core
   call as (partial login-handler set-master-view)"
  [view-fn response]
  #_(print "login-handler: " response)
  (if (= (:login response) "rejected")
    (do (print "bad login")
        (dbs/set-language! nil)
        (swap! dbs/app-state assoc :logged-in? false)
        (dbs/set-message-flag-and-text true "Bad Login"))
    (do
      #_(print "login handler: login branch" response)
      ;; #_(swap! dbs/app-state merge {:logged-in? true
      ;;                             :lang 
      ;;                             :total (:total response)})
      (swap! dbs/app-state merge (merge response {:logged-in? true}))
      (dbs/set-def-showing! false)
      #_(print "good login app-state" @dbs/app-state)))
  (dbs/close-login-box!)
  (view-fn (:active-db response)))

;; to silence spurious warning from clojure-lsp
(comment
  (fetch-score 357)
  (Slug []))


