(ns lexy.cmpts
  #_(:require [clojure.str :as str]))

(def lkup-urls {:glosbe-fwd "https://glosbe.com/de/en/"
                :glosbe-rev "https://glosbe.com/en/de/"
                :dict-cc-fwd "https://www.dict.cc/?s="
                :dict-cc-rev "https://www.dict.cc/?s="
                :reit-fwd "https://dictionary.reverso.net/italian-english/"
                :reit-rev "https://dictionary.reverso.net/english-italian/"})

(def labels {:glosbe-fwd "Glosbe De-En"
             :glosbe-rev "Glosbe En-De"
             :dict-cc-fwd "DictCC De-Eng"
             :dict-cc-rev "DictCC Eng->De"
             :reit-fwd "Ital-Eng"
             :reit-rev "Eng-Ital"})

(defn lkup-url-key
  "helper function to determine url key into above maps
   for lkup buttons"
  [lang dict direction]
  #_(print "lkup-url-key" lang dict flipped)
  ;; TODO fix this for different language choices; lang is set by active-db in
  ;;   dbs/app-state
  (if (not (.startsWith  lang "italian"))
    (cond
      (and (= dict :other) (= direction :fwd)) :glosbe-fwd
      (and (= dict :other) (= direction :rev)) :glosbe-rev
      (and (= dict :dict-cc) (= direction :fwd)) :dict-cc-fwd
      (and (= dict :dict-cc) (= direction :rev)) :dict-cc-rev)
    
    (cond
      (and (= dict :dict-cc) (= direction :fwd)) :dict-cc-fwd
      (and (= dict :dict-cc) (= direction :rev)) :dict-cc-rev
      (and (= dict :other) (= direction :fwd)) :reit-fwd
      (and (= dict :other) (= direction :rev)) :reit-rev)))

(defn- word-to-lookup
  "based on direction and flipped, decide which word to look up"
  [src target direction flipped]
  (if (= direction :fwd)
    (if flipped target src)
    (if flipped src target)))

(defn lkup-button
  "lookup button displayed below word and definition
   depends on the language specified; main and alternate lookups"
  [src target lang dict direction flipped]
  #_(print "lb" src target lang dict direction flipped)
  (let [url-key (lkup-url-key lang dict direction)
        url (get lkup-urls url-key)
        label (get labels url-key)
        word (word-to-lookup src target direction flipped)]
    #_(print "args" url-key url label)
    [:button.button.is-rounded.has-background-light.ml-2.is-small
     {:key label  ;; needed to stop react from squawking
      :on-click #(.open js/window
                        (str url word)
                        "_blank")}
     label]))