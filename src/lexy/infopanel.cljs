(ns lexy.infopanel
  (:require [lexy.dbs :as dbs]))

(defn tagged-text
  "elt of info panel
    displays tag with text if not nil, else none"
  [tag text-or-nil]
  (if-let [text text-or-nil]
    [:span.ml-4 (str tag ": " text)]
    [:span.ml-4 (str tag ": None")]))

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
