(ns lexy.login
  (:require [lexy.dbs :as dbs]))


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
          {:on-click dbs/close-login-box!} "Cancel"]]]])))
