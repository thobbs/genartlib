(ns genartlib.util
  (:use [quil.core :only [height width PI color-mode]]))

  (defn h
    "Returns a given percentage of the height Quil-specific."
    ([] (h 1.0))
    ([percentage] (* (height) percentage)))

  (defn w
    "Returns a given percentage of the width. Quil-specific."
    ([] (w 1.0))
    ([percentage] (* (width) percentage)))

  (defn h-int
    "A version of `h` that truncates to an int value."
    ([] (int (height)))
    ([percentage] (-> (height) (* percentage) int)))

  (defn w-int
    "A version of `w` that truncates to an int value."
    ([] (int (width)))
    ([percentage] (-> (width) (* percentage) int)))

    ([] (int (h 1.0)))

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

  (defn between?
    "Returns true if value is between end1 and end2"
    [value end1 end2]
    (and (>= value (min end1 end2))
         (<= value (max end1 end2))))

  (defn set-color-mode []
    (color-mode :hsb 360 100 100 1.0))

  (defn enumerate [s]
    (map-indexed vector s))

  (defn zip [seq1 seq2]
    (map vector seq1 seq2))
