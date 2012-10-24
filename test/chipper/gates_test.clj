(ns chipper.gates_test
  (:use midje.sweet
        chipper.gates))

(facts "nand gate"
       (nand* [0 0] [:bar]) => {:bar 1}
       (nand* [0 1] [:bar]) => {:bar 1}
       (nand* [1 0] [:bar]) => {:bar 1}
       (nand* [1 1] [:bar]) => {:bar 0})

(facts "not gate"
       (not* [0] [:bar]) => {:bar 1}
       (not* [1] [:bar]) => {:bar 0})

(facts "and gate"
       (and* [0 0] [:bar]) => {:bar 0}
       (and* [0 1] [:bar]) => {:bar 0}
       (and* [1 0] [:bar]) => {:bar 0}
       (and* [1 1] [:bar]) => {:bar 1})

(facts "or gate"
       (or* [0 0] [:bar]) => {:bar 0}
       (or* [0 1] [:bar]) => {:bar 1}
       (or* [1 0] [:bar]) => {:bar 1}
       (or* [1 1] [:bar]) => {:bar 1})

(facts "xor gate"
       (xor* [0 0] [:bar]) => {:bar 0}
       (xor* [0 1] [:bar]) => {:bar 1}
       (xor* [1 0] [:bar]) => {:bar 1}
       (xor* [1 1] [:bar]) => {:bar 0})

(facts "mux gate"
       (mux* [0 0 0] [:bar]) => {:bar 0}
       (mux* [0 0 1] [:bar]) => {:bar 0}
       (mux* [0 1 0] [:bar]) => {:bar 0}
       (mux* [0 1 1] [:bar]) => {:bar 1}
       (mux* [1 0 0] [:bar]) => {:bar 1}
       (mux* [1 0 1] [:bar]) => {:bar 0}
       (mux* [1 1 0] [:bar]) => {:bar 1}
       (mux* [1 1 1] [:bar]) => {:bar 1})

(facts "dmux gate"
       (dmux* [0 0] [:foo :bar]) => {:foo 0 :bar 0}
       (dmux* [0 1] [:foo :bar]) => {:foo 0 :bar 0}
       (dmux* [1 0] [:foo :bar]) => {:foo 1 :bar 0}
       (dmux* [1 1] [:foo :bar]) => {:foo 0 :bar 1})