(ns chipper.utils)

(defn kws->syms
  "Turns [:a :b] into [a b]"
  [coll]
  (vec (map #(symbol (name %)) coll)))

(defn fn-sym?
  "predicate to check if x is sym and not =>"
  [x]
  (and (symbol? x)
       (not= (name x) "=>")))

(defn arrow?
  "predicate returns true if sym is =>"
  [x]
  (and (symbol? x)
       (= (name x) "=>")))

(defn- get-fnname
  "gets the function name from a form"
  [form]
  (first form))

(defn get-kw-ins
  "get the input pins from a form"
  [form]
  (take-while keyword? (rest form)))

(defn get-kw-outs
  "get the output pins from a form"
  [form]
  (rest (drop-while keyword? (drop-while fn-sym? form))))