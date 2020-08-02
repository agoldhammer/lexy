(ns lexy.client
  (:require
   [ajax.core :as ajax :refer [GET]]))

(defonce test-server "http://localhost:5000")

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
  (.log js/console response)
  response)

(defn error-handler [{:keys [status status-text]}]
  (print "ajax error:" status status-text)
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn get-files
  "get list of files matching pattern* from server"
  [pattern]
  (GET (str test-server "/files/" pattern)
    {:handler 
     debug-handler
     :error-handler error-handler
     :timeout 1000
     :response-format :json
     :keywords? true})
  )

(defn set-db
  "set db to use on server"
  [dbname]
  (GET (str test-server "/seldb/" dbname)
    (default-request-map)))

(defn fetch
  "fetch slugs from selected db"
  []
  (GET (str test-server "/fetch")
    (default-request-map)))

(defn get-counts
  "get counts of active db"
  []
  (GET (str test-server "/getcount")
    (default-request-map)))


