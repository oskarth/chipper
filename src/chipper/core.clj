(ns chipper.core
  (:use [clojure.contrib.combinatorics :only (selections)]))

(defn bool-space
  "Generates every possible boolean n-tuple."
  [n]
  (selections [0 1] n))

(defmacro defchip
  "Creates a function that returns a bool (map)"
  [fname m & body]
  `(defn ~fname [& {:keys ~m :as m#}] ~@body))

;; primitive chip
(defchip nand* [a b out]
  (if (= (+ a b) 2) 0 1))

;; TESTING
;; (def foo (bool-space (- (args chip) 1))) => ((0) (1)) for not*
;; (map foo (not* :in %))
;; how spread out over several arguments?
;; cmp files

;; testing works for not
(map #(not* :in %)
     (flatten (bool-space 1)))
;; => (1 0) with (0 1) as input
;; want this though:
;; in out
;; 0  1
;; 1  0
;;
;; losing info, (not* :in 0) => 1, should be {:out 1}?
;; ARGH SYNTAX MAKES NO SENSE
;; (not {:in 0})
;; anything else is a SHORTHAND, maps are much easier to work with.

(defchip not* [in out]
  (nand* :a in :b in))

(defchip and* [a b out]
  (nand* :a a :b b))

;; print truth-table

(defchip or* [a b out]
  (not* :in a :out nota)
  (not* :in b :out notb)
  (and* :a nota :b notb :out w)
  (not* :in w)) ;; or :out out to visualize it explicitly

(comment (defchip AND
           "AND gate: out = 1 if a=b=1, 0 otherwise."
           [:a :b :out>]
           (NAND [:a :b :out> w])
           ;; bind return value to w, and use it below?
           (NOT [:a w :out> out])))

;; NOTES
;; should have a very intuitive way of specifying m-way n-bit
;; Few primitives and some interactions
;; Reason about CHIPS and PINS
;; Make all of these composable
;; how do we make a nand gate from scratch?
;; can we leverage core.logic somehow? "this must be the case" 
;; eg lein, defproject:
;; [project-name version & {:as args}] (last part are args?)
;; (AND {:a 1 :b 1} => {:out 1}) (support for vectors too)
;; Principles?
;; merge somehow?
;; keywords are pins
;; distinguish output pins with >
;; bind variables to output pins
;; atom to keep track of pin state?

;; TODO
;; - map fn to (bool-space 2) to generate chiptests
;; - pprint truth tables (a b | out // ...)
;; - input and output maps as args
;; - defchip macro, name, input-map, output map | seq of pa}rts
;; - change keys to be specific
;; - add docstring? see defpartial for that

;; LATER
;; - concatenate truth tables with pins (~lvars?)
;; - import .cmp
;; - define type / leverage builtin bools? (bool arrays?)))