(ns chipper.charm
  (:use midje.sweet))

;; DSL Lessons Learned
;;------------------------------------------------------------------------------
;; 1) don't try to be too clever - type it out first.
;; 2) program with values for feedback.
;; 3) write tests.
;; 4) unify and find patterns.
;; 5) The first rule of macros: don't write macros.

;; Debugging LL
;;------------------------------------------------------------------------------
;; Had a fn which used "exp" instead of form (which was a var), but
;; exp was bound at the repl - how catch this leaky scope next time?

;; SPEC
;;------------------------------------------------------------------------------
;; Grammar: (defgate ...) where ... is gate & :ins => & :outs
;; :a :b :c => :foo :bar

;; stuff
;;------------------------------------------------------------------------------
;; lists, recursive think, first and rest, base case basics
;; ~compiler, traverse all, pull out nice info, topological sort~ to
;; get most primitive, reduce into more primitive forms
;; books, macro like writing a compiler, defining instrc set, vm
;; turtles, books (tree chapters)
;; start with low rigid thing, then syntax, then write compiler to
;; travel
;; start by lispifying with s-expressions, jsut tree
;; translate .hdl files straight to s-exp without thinking about
;; macros, only later

;; truth table
;; destruct name n rest
;; split with to get ins and rest, then chop off to get outs


;; nand* a b => out
;; bool-space 2 (00 01 10 11)
;; (meta nand*) => a b | out

;; add ins and outs to meta

;; fn that takes a fn, an #ins #outs expected, invok permutations
;; use meta to find out # args expects, if know args then ok, easier
;; (with-tt 'nand*) ;; => symbol meta name
;; a b | out
;; 0 0 | 1
;; .......
;; 1 1 | 0

;; just boilerplate then remove
;; list manipulations, recursie solutions




;; util functions
;;------------------------------------------------------------------------------
(defn- kws->syms
  "Turns [:a :b] into [a b]"
  [coll]
  (vec (map #(symbol (name %)) coll)))

(defn- fn-sym?
  "predicate to check if x is sym and not =>"
  [x]
  (and (symbol? x)
       (not= (name x) "=>")))

(defn- arrow?
  "pred returns true if sym is =>"
  [x]
  (and (symbol? x)
       (= (name x) "=>")))

(defn- get-fnname [form]
  (first form))

(defn- get-kw-args [form]
  (take-while keyword? (rest form)))

(defn- get-kw-outs [form]
  (rest (drop-while keyword? (drop-while fn-sym? form))))
  
(defn- make-args [kwargs]
  (kws->syms (get-kw-args kwargs)))

(defn- make-outs [kwouts]
  (kws->syms (get-kw-args kwouts)))



;; parsing multiple forms into one
;;------------------------------------------------------------------------------
#_(defn make-expressions
  "turns (not* :in => :out nand* :in :in => :out)                                                                                                                                                  into ((not* [in] [out]) (nand* [in in] [out]))"
  [form]
  (if (empty? form) nil
      (let [id (first form)
            [ins [arrow & rest1]] (split-with (complement arrow?) (rest form))
            [outs remainder] (split-with (complement fn-sym?) rest1)]
       (cons (list id (kws->syms ins) (kws->syms outs)) (make-expressions remainder)))))


;; gates, standard form
;;------------------------------------------------------------------------------
(defn nand* [a b]
  (let [out (if (= (+ a b) 2) 0 1)]
    out))

(defn not* [a]
  (let [out (nand* a a)]
    out))

(defn and* [a b]
  (let [w (nand* a b)
        out (not* w)]
    out))

(defn or* [a b]
  (let [na (not* a)
        nb (not* b)
        w (and* na nb)
        out (not* w)]
    out))

(defn xor* [a b]
  (let [na (not* a)
        nb (not* b)
        anb (and* a nb)
        bna (and* b na)]
    (or* anb bna)))

(defn mux* [a b sel]
  (let [nsel (not* sel)
        w1 (and* a nsel)
        w2 (and* b sel)
        out (or* w1 w2)]
    out))

(defn dmux* [in sel]
  (let [nsel (not* sel)
        a (and* in nsel)
        b (and* in sel)
        out [a b]]
    out))


;; Macro its helpers
;; diff macro for nandgate, defprimitive
;;------------------------------------------------------------------------------
(defmacro defgate [gate ins _ outs & forms]
  `(defn ~gate ~ins
     ~(expand-defgate forms outs)))

(defn expand-defgate [forms outs]
  (if (empty? forms)
    outs
    (let [[[name & body] & rest] forms
          [args [_ & outputs]] (split-with (complement arrow?) body)]
      `(let [~(vec outputs) (~name ~@args)]
         (expand-defgate rest outs)))))



;; tests for macros
;;------------------------------------------------------------------------------
;; circumvent ugly midje meta data addition with =expand-to=>
;; otherwise expands to form ... :postion (midje....)
#_(fact (macroexpand-1 '(defgate not* :in => :out nand* :in :in => :out))
      => '(not* :in => :out nand* :in :in => :out))

;; is this right? not 100% sure, special behavior for nands?
(fact (macroexpand-1 '(defgate nand* :a :b => :out :foo (if (= (+ a b) 2) 0 1))
                     => '(clojure.core/defn nand* [a b]
                           (clojure.core/let [out (if (= (+ a b) 2) 0 1)] out))))

(fact (macroexpand-1 '(defgate not* :in => :out nand* :in :in => :out)
                     => '(clojure.core/defn not* [in]
                           (clojure.core/let [out (nand* in in)] out))))

;; (defgate dmux* [in sel => a b]
;;   (not* sel => nsel)
;;   (and* in nsel => a)
;;   (and* in sel => b))

;; todo: refactor with truth-table?
(facts "logical gates"
 (nand* 0 0) => 1
 (nand* 0 1) => 1
 (nand* 1 0) => 1
 (nand* 1 1) => 0
 (not* 0) => 1
 (not* 1) => 0
 (and* 0 0) => 0
 (and* 0 1) => 0
 (and* 1 0) => 0
 (and* 1 1) => 1
 (or* 0 0) => 0
 (or* 0 1) => 1
 (or* 1 0) => 1
 (or* 1 1) => 1
 (xor* 0 0) => 0
 (xor* 0 1) => 1
 (xor* 1 0) => 1
 (xor* 1 1) => 0
 (mux* 0 0 0) => 0
 (mux* 0 0 1) => 0
 (mux* 0 1 0) => 0
 (mux* 0 1 1) => 1
 (mux* 1 0 0) => 1
 (mux* 1 0 1) => 0
 (mux* 1 1 0) => 1
 (mux* 1 1 1) => 1
 (dmux* 0 0) => [0 0]
 (dmux* 0 1) => [0 0]
 (dmux* 1 0) => [1 0]
 (dmux* 1 1) => [0 1]
 )