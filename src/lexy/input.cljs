(ns lexy.input
  (:require [lexy.client :as client]
            [reagent.core :as reagent]))

(defonce submittable? (reagent/atom false))

(defn- clear-btn-with-id
  "clear text from button with given id"
  [id]
  (let [elt (. js/document getElementById id)]
    (set! (. elt -value) nil)))

(defn- clear-all-inputs
  "clear all edit input buttons"
  []
  (mapv clear-btn-with-id ["edsrc" "edtgt" "edsupp"])
  (reset! submittable? false))

(defn- clear-button
  "clear all edit boxes"
  []
  [:button#eclear.button.is-rounded.is-danger.ml-4
   {:on-click clear-all-inputs}
   "Clear"])

(defn- get-val-of-elt-with-id
  "get the value of the button with given id"
  [id]
  (let [elt (. js/document getElementById id)]
    (if elt
      (. elt -value)
      "")))

(defn- collect-values
  "collect values of all the input boxes"
  []
  (mapv get-val-of-elt-with-id ["edsrc" "edtgt" "edsupp"]))

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
  []
  (let [vals (collect-values)]
    #_(print "collect vals and submit called" vals)
    (client/submit-new-slug vals)
    (clear-all-inputs)
    ;; focus the source box
    (.focus (.getElementById js/document "edsrc"))
    ))

(defn- submit-button
  "submit the new slug"
  [enabled?]
  #_(print "submit-button called with" enabled?)
  [:button#edsubmit.button.is-rounded.is-success.ml-4
   {:on-click collect-values-and-submit
    :disabled (not enabled?)}
   "Submit"])

(defn- edit-box-change
  "called when any edit box changes"
  []
  #_(print "edit box change" e)
  (if (ready-to-submit?)
    (reset! submittable? true)
    (reset! submittable? false)))

(defn- edit-box
  ""
  [id]
  (let [ph (condp = id
             "edsrc" "New foreign word"
             "edtgt" "New English definition"
             "edsupp" "New supplementary info")
        props {:id id
               :placeholder ph
               :type "text"
               :on-change edit-box-change}]
    [:div.control.my-3.ml-4.mr-6
     [:input.input.is-medium.is-primary.mx-2.is-size-4
      (if (= id "edsrc")
        (merge props {:auto-focus true})
        props)]]))

(defn edit-panel
  "view function for edit-panel"
  []
  (let [can-submit? @submittable?]
    [:div.field.ml-2.mr-10
     [edit-box "edsrc"]
     [edit-box "edtgt"]
     [edit-box "edsupp"]
     [:div
      [submit-button can-submit?] [clear-button]]]))

(comment
  (clear-btn-with-id "edsrc")
  (get-val-of-elt-with-id "edsrc")
  (ready-to-submit?)
  (collect-values-and-submit)
  (. (. js/document getElementById "edsubmit") -onclick))

