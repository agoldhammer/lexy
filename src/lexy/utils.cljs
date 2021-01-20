(ns lexy.utils)

(defn id->value
  "get value of element with specified id"
  [id]
  (let [elt (. js/document getElementById id)]
    (.-value elt)))
