(ns chipper.gates
  (:use chipper.core))

(defgate nand* [a b] [out]
  (if (= (+ a b) 2) {out 0} {out 1}))

(defgate not* [in] [out]
  (nand* [in in] [out]))

(defgate and* [a b] [out]
  (nand* [a b] [w])
  (not* [w] [out]))

(defgate or* [a b] [out]
  (not* [a] [na])
  (not* [b] [nb])
  (and* [na nb] [w])
  (not* [w] [out]))

(defgate xor* [a b] [out]
  (not* [a] [na])
  (not* [b] [nb])
  (and* [a nb] [anb])
  (and* [b na] [bna])
  (or* [anb bna] [out]))

(defgate mux* [a b sel] [out]
  (not* [sel] [nsel])
  (and* [a nsel] [w1])
  (and* [b sel] [w2])
  (or* [w1 w2] [out]))

(defgate dmux* [in sel] [a b]
  (not* [sel] [nsel])
  (and* [in nsel] [a])
  (and* [in sel] [b]))