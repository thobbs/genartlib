(ns sketch.core
  (:require [quil.core :as q])
  (:require [sketch.dynamic :as dynamic])
  (:gen-class))

(q/defsketch example
             :title "Sketch"
             :setup dynamic/setup
             :draw dynamic/draw
             ; :renderer :p2d
             :size [1400 1400])

(defn refresh []
  (use :reload 'sketch.dynamic)
  (.redraw example))

(defn get-applet []
  example)
