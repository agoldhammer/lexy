(ns lexy.cmpts)

(def lkup-urls {:glosbe-fwd "https://glosbe.com/de/en/"
                :glosbe-rev "https://glosbe.com/en/de/"
                :dict-cc-fwd "https://www.dict.cc/?s="
                :dict-cc-rev "https://www.dict.cc/?s="
                :reit-fwd "https://dictionary.reverso.net/italian-english/"
                :reit-rev "https://dictionary.reverso.net/english-italian/"})

(def labels {:glosbe-fwd "Glosbe De-En"
             :glosbe-rev "Glosbe En-De"
             :dict-cc-fwd "DictCC->Eng"
             :dict-cc-rev "DictCC->Src"
             :reit-fwd "Ital-Eng"
             :reit-rev "Eng-Ital"})

(defn lkup-url-key
  "helper function to determine url key into above maps
   for lkup buttons"
  [lang dict dir]
  #_(print "lkup-url-key" lang dict dir)
  (if (or (= lang "german") (= lang "redux2")
          (= lang "newgerman"))
    (cond
      (and (= dict :other) (= dir :fwd)) :glosbe-fwd
      (and (= dict :other) (= dir :rev)) :glosbe-rev
      (and (= dict :dict-cc) (= dir :fwd)) :dict-cc-fwd
      (and (= dict :dict-cc) (= dir :rev)) :dict-cc-rev)
    
    (cond
      (and (= dict :dict-cc) (= dir :fwd)) :dict-cc-fwd
      (and (= dict :dict-cc) (= dir :rev)) :dict-cc-rev
      (and (= dict :other) (= dir :fwd)) :reit-fwd
      (and (= dict :other) (= dir :rev)) :reit-rev)))

(defn lkup-button
  "lookup button displayed below word and definition
   depends on the language specified; main and alternate lookups"
  [words lang dict dir]
  (let [url-key (lkup-url-key lang dict dir)
        url (get lkup-urls url-key)
        label (get labels url-key)]
    [:button.button.is-rounded.has-background-light.ml-2.is-small
     {:key label  ;; needed to stop react from squawking
      :on-click #(.open js/window
                        (str url words)
                        "_blank")}
     label]))