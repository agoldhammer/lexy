(ns lexy.login
  (:require [lexy.dbs :as dbs]))


(defn batsize
  "radio button calling for batch of size value"
  #_:clj-kondo/ignore
  [value]
  (let [selected (:batch-size @dbs/app-state)]
    (fn [value]
        [:label.radio.ml-2
         [:input {:id (str "bats" value)
                  :type "radio"
                  :name "batsize"
                  :value value
                  :defaultChecked (= value selected)
                  :on-change #(swap! dbs/app-state assoc :batch-size value)}]

         (str value)])))


;; Calls submit-fn to submit credentials and batch size to server
(defn login-box
  "login element"
  [submit-fn]
  (let [login-showing? (:login-showing? @dbs/app-state)]
    (when login-showing?
      [:div.modal.is-active
       [:div.modal-background.has-background-light-gray]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Lexy Login"]]
        [:section.modal-card-body
         [:div.field
          [:label.label "Username"]
          [:div.control
           [:input#un.input {:type "text"
                             :placeholder "username"}]]
          [:label.label "Password"]
          [:div.control
           [:input#pw.input {:type "password"
                             :placeholder "password"}]]]]

        [:footer.modal-card-foot
         [:button.button.is-success
          {:on-click submit-fn} "Login"]
         [:button.button
          {:on-click dbs/close-login-box!} "Cancel"]
         [:label.label.ml-4.mr-3 "Batch size:"]
         [batsize 10] [batsize 25] [batsize 50]]]])))
