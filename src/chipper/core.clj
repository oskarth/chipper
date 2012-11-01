(ns chipper.core
  (:use chipper.utils))

(defn expand-defgate
  "Recursive function that expands forms into regular functions.
   The outs argument is used to collect the desired output pins."
  [forms outs]
  (if (empty? forms)
    outs
    (let [[[name & body] & rest] forms
          [[args] [_ & [outputs]]] (split-with (complement arrow?) body)]
      `(let [~(vec outputs) (~name ~@args)]
         ~(expand-defgate rest outs)))))

(defmacro defprimitive
  "Defines a primitive gate in terms of a regular Clojure function.
   Currently only used to define a nand gate."
  [gate ins _ outs body]
  `(defn ~gate ~ins ~body))

(defmacro defgate
  "Defines a logical gate with input and output pins, and a implementation
   expressed in terms of other logical gates."
  [gate ins _ outs & forms]
  `(defn ~gate ~ins
     ~(expand-defgate forms outs)))