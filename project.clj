(defproject genartlib "1.1.1"
  :description "Utilities and common tasks for generative artwork"
  :url "http://github.com/thobbs/genartlib"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.11.3"]
                 [org.apache.commons/commons-math3 "3.3"]
                 [quil "4.3.1563"]]
  :deploy-repositories [["clojars" {:sign-releases false}]
                        ["releases" {:sign-releases false :url "https://clojars.org"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org"}]]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot :all)
