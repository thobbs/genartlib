(defproject genartlib "0.1.0-SNAPSHOT"
  :description "Utilities and common tasks for generative artwork"
  :url "http://github.com/thobbs/genartlib"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.apache.commons/commons-math3 "3.3"]
                 [incanter "1.5.5"]
                 [quil "2.3.0"]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"])
