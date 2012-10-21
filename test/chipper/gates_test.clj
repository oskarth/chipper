(ns chipper.gates_test
  (:use clojure.test
        chipper.gates))

(deftest nand-test
  (is (= (nand* [0 1] [:bar])
         {:bar 1})))

(deftest or-test
  (is (= (and* [0 1] [:foo])
         {:foo 1})))

(deftest and-test
  (is (= (and* [0 1] [:foo])
         {:foo 0})))