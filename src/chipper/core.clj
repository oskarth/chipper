(ns chipper.core
  (:use [clojure.contrib.combinatorics :only (selections)])
  (:use [clojure.pprint :only (pprint)])
  (:require [clojure.tools.macro :as macro]))

(defn bool-space
  "Generates every possible boolean n-tuple."
  [n]
  (selections [0 1] n))

(defn kws->syms
  "Turns [:a :b] into [a b]"
  [coll]
  (vec (map #(symbol (name %)) coll)))

(defn- syms->kws
  "Replaces symbols with keywords in a form"
  [form]
  (let [outs (nth form 2)
        kwouts (vec (map keyword outs))]
    (replace {outs kwouts} form)))

(defn- replace-keys
  "Replaces keys in coll with corresponding
       values from map"
  [m coll]
  (replace (select-keys m coll) coll))

(defn- wrap-key [[_ _ bindings]]
  {:keys (kws->syms bindings)})

(defn- collect-binds [forms]
  (letfn [(collect-bind [acc form]
            (conj acc
                  (wrap-key form)
                  (syms->kws form)))]
    (reduce collect-bind
            [] forms)))

(defn- thread-form [forms]
  (let [lastform (last forms)
        bindings (collect-binds (butlast forms))]
    `(let ~bindings ~lastform)))

(defn truth-table
  "TODO Should generate a truth table for a logical function."
  [f]
  (map #(f {:in %}) (flatten (bool-space 1))))

(defmacro defchip
  "Creates a logic gate which takes a vector of inputs
   and a vector of outputs."
  [fname & args]
  (let [[fname args] (macro/name-with-attributes fname args)
        [ins outs & body] args
        params (vector ins outs)]
    `(defn ~fname ~params ~(thread-form body))))

(defmacro >>
  "Threads chip bindings through forms"
  [& forms]
  (thread-form forms))

;; CHIPS

(defchip nand* [a b] [out]
  (if (= (+ a b) 2) {out 0} {out 1}))

(defchip not* [in] [out]
  (nand* [in in] [out]))

(defchip and* [a b] [out]
  (nand* [a b] [w])
  (not* [w] [out]))

;; TESTS

(and* [1 1] [:foo])
;;(and* [0 0] [out]) ;; no work
;;(and* [0 0]) ;; no work

;; more explicit use of threading in defchip
;; possible use threading macro (>> & forms)
;; replace ~(thread-form body) with ~@body then
;; as threading is done with the >> macro

;; TODO
;; truth table