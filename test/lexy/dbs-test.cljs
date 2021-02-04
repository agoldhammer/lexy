(ns test-dbs
  (:require #_[lexy.dbs :as dbs]
            [cljs.test :refer-macros [deftest is #_testing run-tests]]))

(deftest test-dummy
  (is (= 1 1)))

(run-tests)
#_(deftest test-app-state
  "test app state"
  []
  (let [state @dbs/app-state])
  )
