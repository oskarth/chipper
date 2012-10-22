(ns chipper.utils
  (:use [clojure.contrib.combinatorics :only (selections)]))

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

(defn collect-binds [forms]
  (letfn [(collect-bind [acc form]
            (conj acc
                  (wrap-key form)
                  (syms->kws form)))]
    (reduce collect-bind
            [] forms)))