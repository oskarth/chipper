(ns chipper.core_test
  (:use chipper.core
        midje.sweet))

;; we should prolly test our beloved core.

(fact "expand-defgate base case"
      (expand-defgate nil '[out])
      => '[out])

(fact "expand-defgate"
      (expand-defgate '((not* [w] => [out])) '[out])
      =>
      '(clojure.core/let [[out] (not* w)]
                        (chipper.core/expand-defgate nil [out])))