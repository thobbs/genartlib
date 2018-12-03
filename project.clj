(defproject genartlib "0.1.17-SNAPSHOT"
  :description "Utilities and common tasks for generative artwork"
  :url "http://github.com/thobbs/genartlib"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.commons/commons-math3 "3.3"]
                 [quil "2.7.1"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot [genartlib.geometry genartlib.util genartlib.random genartlib.algebra genartlib.curves])
