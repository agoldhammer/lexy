(ns lexy.input
  (:require [lexy.client :as client]))

(defn- clear-btn-with-id
  "clear text from button with given id"
  [id]
  (let [elt (. js/document getElementById id)]
    #_(print "cbwid" elt)
    #_(print "value" (. elt -value))
    (set! (. elt -value) nil)))

(defn- enable-btn-with-id
  "set enabled prop of btn with given id"
  [id enable?]
  (print "enable-btn-with-id called" id enable?)
  (let [elt (. js/document getElementById id)]
    #_(set! (. elt -disabled) (not t-or-f))
    (if (not enable?)
      (.setAttribute elt "disabled" true)
      (.removeAttribute elt "disabled"))))

(defn- clear-all-inputs
  "clear all edit input buttons"
  []
  (mapv clear-btn-with-id ["edsrc" "edtgt" "edsupp"])
  (enable-btn-with-id "edsubmit" false))

(defn- clear-button
  "clear all edit boxes"
  []
  [:button#eclear.button.is-rounded.is-danger.ml-4
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
  (mapv get-val-of-btn-with-id ["edsrc" "edtgt" "edsupp"]))

(defn- non-blank?
  "Is string blank?"
  [s]
  (not= s ""))

(defn- ready-to-submit?
  "are at least src and target non blank?"
  []
  (let [[src tgt _] (collect-values)]
       (and (non-blank? src) (non-blank? tgt))))

(defn- collect-values-and-submit
  "collect and submit"
  [e]
  (print "cvas" e)
  (let [vals (collect-values)]
    (print "collect vals and submit called" vals)
    ;; TODO write submit function!!!
    (client/submit-new-slug vals)
    (clear-all-inputs)))

(defn- submit-button
  "submit the new slug"
  []
  [:button#edsubmit.button.is-rounded.is-success.ml-4
   {:on-click #(collect-values-and-submit %)
    ;; TODO: this should start disabled, enable only when condx satisfied
    :disabled true}
   "Submit"])

(defn- edit-box-change
  "called when any edit box changes"
  [e]
  (print "edit box change" e)
  (let [submit-btn-enable? (ready-to-submit?)]
    (enable-btn-with-id "edsubmit" submit-btn-enable?)))

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
       :on-change #(edit-box-change %)}]]))

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
  (get-val-of-btn-with-id "edsrc")
  (ready-to-submit?)
  (collect-values-and-submit nil)
  (. (. js/document getElementById "edsubmit") -onclick))

