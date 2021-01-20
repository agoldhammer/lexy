(ns lexy.client
  (:require
   [lexy.dbs :as dbs]
   [ajax.core :as ajax :refer [GET POST]]))

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

(defn- debug-handler [response]
  (print "Debug handler: " response)
  #_(.log js/console response))

(defn- error-handler [{:keys [status status-text]}]
  (print "lexy ajax error:" status status-text)
  #_(.log js/console (str "AJAX error" status " " status-text)))

(defn- slug-handler
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
    (swap! dbs/def-panel-state assoc :slugs [])
    (when DEBUG
      (print "d-p-s slugs: shd be empty: " (take 2 (:slugs dbs/def-panel-state)))
      (print "new state" (take 2 (:slugs new-state))))
    (swap! dbs/def-panel-state merge new-state)))

(defn get-endpoint
  "get endpoint from vocab server"
  [endpoint handler]
  (GET endpoint
    (merge (default-request-map) {:handler handler})))

(defn post-endpoint
  "post endpoint from vocab server"
  [endpoint params handler]
  (POST endpoint
    (merge (default-request-map) {:handler handler
                                  :format :json
                                  :params params})))

(defn fetch-batch
  ""
  [lang-or-nil]
  (print "fetch-batch called with lang" lang-or-nil)
  (when (not (:lang @dbs/app-state))
    (dbs/set-language! lang-or-nil))
  #_(client/set-db fname)
  (get-endpoint (str "/fetch") slug-handler))

(defn login
  "send login data to server"
  [params handler]
  (post-endpoint "/login" params handler))

;; to silence spurious warning from clojure-lsp
(comment
  (Slug []))


