(ns chipper.core-test
  (:use clojure.test
        chipper.core))

(deftest and-test
  (is (= (and* [0 1] [:foo])
         {:foo 0})))

(run-tests)