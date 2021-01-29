(ns lexy.utils)

(defn id->value
  "get value of element with specified id"
  [id]
  (let [elt (. js/document getElementById id)]
    (.-value elt)))

(defn tagged-text
  "elt of info panel
    displays tag with text if not nil, else none"
  [tag text-or-nil]
  (if-let [text text-or-nil]
    [:span.ml-2 (str tag ": " text)]
    [:span.ml-2 (str tag ": None")]))

(defn red-green-led
  "show red/green for input 0/1"
  [flag]
  (if (zero? flag)
    [:span.icon.ml-2.has-text-danger
     [:img.fas.fa-circle]]
    [:span.icon.ml-2.has-text-success
     [:img.fas.fa-circle]]))

(defn traffic-light
  "show red/yellow/green accrdng to input"
  [input]
  (cond
    (< input 5) (red-green-led 1)
    (> input 9) (red-green-led 0)
    :else [:span.icon.ml-2.has-text-warning
           [:img.fas.fa-circle]]))

(comment
  (traffic-light 11)
  (red-green-led 1))
