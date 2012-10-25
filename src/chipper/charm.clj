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
;; todo: general deuglify
;; todo: for [n (range (count list))] should be foo [n (count list)]
;;------------------------------------------------------------------------------
(defn- get-fn-pos
  "helper to get indicies of fn-symbols
  '(not* :a => :b not* :a => :b)) => (0 4), which are the positions of fns"
  [form]
  (for [n (range (count form)) :when (fn-sym? (nth form n))]
    n))

(defn- pair-delta
  "calculate delta between two numbers in a list
   (0 6 10 15) => (6 4 5)"
  [li]
  (for [k (range (count li)) :when (> k 0)]
    (- (nth li k) (nth li (- k 1)))))

(defn- without-last
  "removes last index"
  [li]
  (take (- (count li) 1) li))

(defn make-expressions
  "creates expressions from unstructured list"
  [form]
  (let [fn-pos (get-fn-pos form)
        lx (pair-delta fn-pos)
        ly (without-last fn-pos)]
    (for [k (range (count lx))]
      (take (nth lx k)
            (drop (nth ly k) form)))))



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



;; MACROS
;; test gensyms?
;; out variable is weird, wait shouldn't that come
;; frm somewhere rather than being implicit
;; :FOO is (nand* in in), which is the second exp...
;;------------------------------------------------------------------------------
(defmacro defgate [& form]
  (let [exp# (make-expressions form)
        fform# (first exp#)
        fname# (get-fnname fform#)
        args# (make-args fform#)
        outs# (make-outs fform#)]
    `(defn ~fname# ~args# (let [out# :FOO] out#))))

;; should get some expression ex
;; not* :in => :out nand* :in :in => :out


;; tests for macros
;;------------------------------------------------------------------------------
;; is this right? not 100% sure, special behavior for nands?
(fact (defgate nand* :a :b => :out :foo (if (= (+ a b) 2) 0 1))
      =expands-to=> (clojure.core/defn nand* [a b]
                      (clojure.core/let [out (if (= (+ a b) 2) 0 1)] out)))

(fact (defgate not* :in => :out nand* :in :in => :out)
      =expands-to=> (clojure.core/defn not* [in]
                      (clojure.core/let [out (nand* in in)] out)))

;;(defgate dmux* :in :sel => :a :b
;;  not* :sel => :nsel
;;  and* :in :nsel => :a
;;  and* :in :sel => :b)


;; tests fof gates
;;------------------------------------------------------------------------------
(facts
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