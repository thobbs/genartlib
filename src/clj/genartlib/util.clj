(ns genartlib.util
  (:use [quil.core :only [height width PI color-mode]])
  (:use [incanter.core :only [$=]]))

  (defn h
    ([] (h 1.0))
    ([percentage] (* (height) percentage)))

  (defn w
    ([] (w 1.0))
    ([percentage] (* (width) percentage)))

  (defn pi [x]
    (* PI x))

  (defn in?
    "true if seq contains elm"
    [seq elm]
    (some #(= elm %) seq))

  (defn not-in?
    "true if seq does not contain elm"
    [seq elm]
    (not-any? #(= elm %) seq))

  (defn between? [value end1 end2]
    "Returns true if value is between end1 and end2"
    (and ($= value >= ((min end1 end2)))
         ($= value <= ((max end1 end2)))))

  (defn set-color-mode []
    (color-mode :hsb 360 100 100 1.0))

  (defn enumerate [s]
    (map-indexed vector s))
