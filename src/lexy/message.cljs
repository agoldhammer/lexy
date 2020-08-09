(ns lexy.message)

(defn message-box
  "dissmiss-action: 0-arity funcion"
  [text is-showing? dismiss-action]
  (when is-showing?
    [:div.modal.is-active
     [:div.modal-background.has-background-light-gray]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Lexy Message"]]
      [:section.modal-card-body
       [:p text]]

      [:footer.modal-card-foot
       [:button.button.is-success
        {:on-click dismiss-action} "Dismiss"]]]]))