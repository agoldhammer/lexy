(ns lexy.input
  (:require [lexy.client :as client]))

(defn- clear-btn-with-id
  "clear text from button with given id"
  [id]
  (let [elt (. js/document getElementById id)]
    #_(print "cbwid" elt)
    #_(print "value" (. elt -value))
    (set! (. elt -value) nil)))

(defn- clear-all-inputs
  "clear all edit input buttons"
  []
  (mapv clear-btn-with-id ["edsrc" "edtgt" "edsupp"]))

(defn clear-button
  "clear all edit boxes"
  []
  [:button.button.is-rounded.is-danger.ml-4
   {:on-click clear-all-inputs}
   "Clear"])

(defn- get-val-of-btn-with-id
  "get the value of the button with given id"
  [id]
  (let [elt (. js/document getElementById id)]
    (. elt -value)))

(defn- collect-values
  "collect values of all the input boxes"
  []
  (let [vals
        (mapv get-val-of-btn-with-id ["edsrc" "edtgt" "edsupp"])]
    #_(print vals)
    ;; TODO write submit function!!!
    (client/submit-new-slug vals)
    (clear-all-inputs)))

(defn- submit-button
  "submit the new slug"
  []
  [:button.button.is-rounded.is-success.ml-4
   {:on-click collect-values
    ;; TODO: this should start disabled, enable only when condx satisfied
    :disabled false}
   "Submit"])

(defn- edit-box
  ""
  [id]
  (let [ph (condp = id
             "edsrc" "New foreign word"
             "edtgt" "New English definition"
             "edsupp" "New supplementary info")]
    [:div.control.my-3.ml-4.mr-6
     [:input.input.is-medium.is-primary.mx-2.is-size-4
      {:id id
       #_#_:value ""
       :placeholder ph
       :type "text"
       #_#_:on-change #(modify-slug id %)}]]))

(defn edit-panel
  "view function for edit-panel"
  []
  [:div.field.ml-2.mr-10
   [edit-box "edsrc"]
   [edit-box "edtgt"]
   [edit-box "edsupp"]
   [:div
    [submit-button] [clear-button]]])

(comment
  (clear-btn-with-id "edsrc")
  (get-val-of-btn-with-id "edsrc"))

