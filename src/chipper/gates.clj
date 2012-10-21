(ns chipper.gates
  (:use chipper.core))

(defgate nand* [a b] [out]
  (if (= (+ a b) 2) {out 0} {out 1}))

(defgate not* [in] [out]
  (nand* [in in] [out]))

(defgate and* [a b] [out]
  (nand* [a b] [w])
  (not* [w] [out]))