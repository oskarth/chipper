(ns chipper.core-test
  (:use clojure.test
        chipper.core))

(defn- name-attr
  "pulls out meta info about function name from a form"
  [form]
  (:name (meta form)))

(defn- arglists
  "pulls out meta info about args from a form"
  [form]
  (first (:arglists (meta form))))

(deftest defgate-test
  "true if defgate renders correct form (name and arglists)"
  (let [form (defgate gate [foo bar] [bax quux])]
    (is (= 'gate
           (name-attr form)))
    (is (= '[[foo bar] [bax quux]]
           (arglists form)))))