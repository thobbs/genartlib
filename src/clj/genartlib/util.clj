(ns genartlib.util
  (:require
    [quil.core :refer [height width PI color-mode]]))

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

(defn set-color-mode
  "Set the color space to HSB, with the following ranges:
   - Hue: [0, 360)
   - Saturation: [0 100]
   - Brightness: [0 100]
   - Alpha: [0.0, 1.0]"
  []
  (color-mode :hsb 360 100 100 1.0))

(defn enumerate
  "Returns a seq of tuples like [i v], where i enumerates
   the position (starting from zero, of course) in the original
   sequence and v is the corresponding value in the original
   sequence. Example:

    user=> (enumerate [:a :b :c])
    ([0 :a] [1 :b] [2 :c])"
  [s]
  (map-indexed vector s))

(defn zip
  [& sequences]
  (apply map vector sequences))

(defn vec-remove
  "Removes an element by index from a vector"
  [v index]
  (vec (concat (subvec v 0 index) (subvec v (inc index)))))

(defn snap-to
  [v snap-width]
  (let [remainder (rem v snap-width)]
    (if (>= remainder (* snap-width 0.5))
      (+ v snap-width (* remainder -1.0))
      (- v remainder))))
