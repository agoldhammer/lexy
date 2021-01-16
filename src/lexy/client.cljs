(ns lexy.client
  (:require
   [ajax.core :as ajax :refer [GET POST]]))

(declare debug-handler)
(declare error-handler)

(defn default-request-map
  []
  {:handler debug-handler
   :error-handler error-handler
   :timeout 1000
   :with-credentials true
   :response-format :json
   :keywords? true})

(defn debug-handler [response]
  (print "Debug handler: " response)
  #_(.log js/console response))

(defn error-handler [{:keys [status status-text]}]
  (print "lexy ajax error:" status status-text)
  #_(.log js/console (str "AJAX error" status " " status-text)))

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

;; #_(defn set-db
;;   "set db to use on server"
;;   [dbname]
;;   (GET (str "/seldb/" dbname)
;;     (default-request-map)))

;; (defn fetch
;;   "fetch slugs from selected db"
;;   []
;;   (print "fetching")
;;   (GET (str "/fetch")
;;     (default-request-map)))

;; (defn get-counts
;;   "get counts of active db"
;;   []
;;   (GET (str "/getcount")
;;     (default-request-map)))

(defn login
  "send login data to server"
  [params handler]
  (post-endpoint "/login" params handler))


