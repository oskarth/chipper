(ns chipper.core
  (:require [clojure.tools.macro :as macro]))

;; todo
(defn truth-table
  "Generate a truth table for a logical function."
  [f]
  (map #(f {:in %}) (flatten (bool-space 1))))

(defn- thread-form [forms]
  (let [lastform (last forms)
        bindings (collect-binds (butlast forms))]
    `(let ~bindings ~lastform)))

(defmacro defgate
  "Creates a logic gate which takes a vector of inputs
   and a vector of outputs."
  [fname & args]
  (let [[fname args] (macro/name-with-attributes fname args)
        [ins outs & body] args
        params (vector ins outs)]
    `(defn ~fname ~params ~(thread-form body))))