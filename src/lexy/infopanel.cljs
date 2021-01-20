(ns lexy.infopanel
  (:require [lexy.dbs :as dbs]
            [lexy.utils :refer [tagged-text]]))

(defn info-panel
  "panel displaying info about current active settings;
   shown above definitions in main view"
  []
  (let [{:keys [lang
                batch-size
                total]} @dbs/app-state]
    [:div-level.is-size-7.is-italic.has-text-info
     (tagged-text "Database in use" lang)
     (tagged-text "Batch size" batch-size)
     (tagged-text "Total" total)]))
