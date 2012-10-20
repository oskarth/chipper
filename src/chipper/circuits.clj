(ns chipper.circuits
  (:use chipper.core))

(defchip nand* [a b] [out]
  (if (= (+ a b) 2) {out 0} {out 1}))

(defchip not* [in] [out]
  (nand* [in in] [out]))

(defchip and* [a b] [out]
  (nand* [a b] [w])
  (not* [w] [out]))