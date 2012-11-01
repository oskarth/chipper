(ns chipper.gates_test
  (:use midje.sweet
        chipper.gates))

;; macroexpansions
(fact "defprimitive nand* - primitive vase"
      (macroexpand-1
       '(defprimitive nand* [a b] => [out]
          (if (= (+ a b) 2) [0] [1])))
      =>
      '(clojure.core/defn nand* [a b]
         (if (= (+ a b) 2) [0] [1])))

(fact "defgate not* - basic case"
      (macroexpand-1
       '(defgate not* [in] => [out]
          (nand* [in in] => [out])))
      =>
      '(clojure.core/defn not* [in]
         (clojure.core/let [[out] (nand* in in)]
                           (chipper.core/expand-defgate nil [out]))))

(fact "defgate and* - multiple expressions"
      (macroexpand-1
       '(defgate and* [a b] => [out]
          (nand* [a b] => [w])
          (not* [w] => [out])))
      =>
      '(clojure.core/defn and* [a b]
         (clojure.core/let [[w] (nand* a b)]
                           (chipper.core/expand-defgate ((not* [w] => [out]))
                                                        [out]))))

(fact "defgate or* - nested lets"
      (macroexpand-1
       '(defgate or* [a b] => [out]
          (not* [a] => [na])
          (not* [b] => [nb])
          (and* [na nb] => [w])
          (not* [w] [out])))
       =>
       '(clojure.core/defn or* [a b]
          (clojure.core/let
           [[na] (not* a)]
           (clojure.core/let
            [[nb] (not* b)]
            (clojure.core/let
             [[w] (and* na nb)]
             (clojure.core/let [[] (not* w)] [out]))))))

;; logical gatesgates
(facts "logical gates"
 (nand* 0 0) => [1]
 (nand* 0 1) => [1]
 (nand* 1 0) => [1]
 (nand* 1 1) => [0]
 (not* 0) => [1]
 (not* 1) => [0]
 (and* 0 0) => [0]
 (and* 0 1) => [0]
 (and* 1 0) => [0]
 (and* 1 1) => [1]
 (or* 0 0) => [0]
 (or* 0 1) => [1]
 (or* 1 0) => [1]
 (or* 1 1) => [1]
 (xor* 0 0) => [0]
 (xor* 0 1) => [1]
 (xor* 1 0) => [1]
 (xor* 1 1) => [0]
 (mux* 0 0 0) => [0]
 (mux* 0 0 1) => [0]
 (mux* 0 1 0) => [0]
 (mux* 0 1 1) => [1]
 (mux* 1 0 0) => [1]
 (mux* 1 0 1) => [0]
 (mux* 1 1 0) => [1]
 (mux* 1 1 1) => [1]
 (dmux* 0 0) => [0 0]
 (dmux* 0 1) => [0 0]
 (dmux* 1 0) => [1 0]
 (dmux* 1 1) => [0 1])