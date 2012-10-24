(defproject chipper "0.1.0-SNAPSHOT"
  :description "Toy DSL to describe and reason about logical gates."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/tools.macro "0.1.1"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]]
                   :plugins [[lein-midje "2.0.0-SNAPSHOT"]]}})