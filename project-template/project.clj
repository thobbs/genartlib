(defproject sketch "0.1.0-SNAPSHOT"
  :description "A template for a sketch with Quil/Processing"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.apache.commons/commons-math3 "3.3"]
                 [quil "2.7.1"]
                 [genartlib "0.1.17"] ; utility functions
                 [com.seisw/gpcj "2.2.0"]] ; polygon clipping
  :jvm-opts ["-Xms4000m" "-Xmx4000M" "-server"] ; 4GB heap size
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot [sketch.dynamic])
