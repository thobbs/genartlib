(defproject genartlib "0.1.21-SNAPSHOT"
  :description "Utilities and common tasks for generative artwork"
  :url "http://github.com/thobbs/genartlib"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.apache.commons/commons-math3 "3.3"]
                 [quil "3.1.0"]]
  :deploy-repositories [["releases" {:sign-releases false :url "https://clojars.org"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org"}]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot :all)
