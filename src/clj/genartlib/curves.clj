(ns genartlib.curves
  (:require [genartlib.algebra :refer [interpolate]]))

(defn- single-chaikin-step [points tightness]
  (mapcat (fn [[[start-x start-y] [end-x end-y]]]
            (let [q-x (interpolate start-x end-x (+ 0.0 tightness))
                  q-y (interpolate start-y end-y (+ 0.0 tightness))
                  r-x (interpolate start-x end-x (- 1.0 tightness))
                  r-y (interpolate start-y end-y (- 1.0 tightness))]
              [[q-x q-y] [r-x r-y]]))
          (partition 2 1 points)))

(defn chaikin-curve

  "Forms a Chaikin curve from a seq of points, returning a new
   seq of points.

   The tightness parameter controls how sharp the corners will be,
   and should be a value between 0.0 and 0.5.  A value of 0.0 retains
   full sharpness, and 0.25 creates maximum smoothness.

   The depth parameter controls how many recursive steps will occur.
   The more steps, the smoother the curve is (assuming tightness is
   greater than zero). Suggested values are between 1 and 8, with a
   good default being 4.

   When points form a closed polygon, it's recommended that the start
   point be repeated at the end of points to avoid a gap."

  ([points] (chaikin-curve points 4))
  ([points depth] (chaikin-curve points depth 0.25))

  ([points depth tightness]
   (nth (iterate #(single-chaikin-step % tightness) points) depth)))

(defn chaikin-curve-retain-ends
  "Like chaikin-curve, but retains the first and last point in the
   original `points` seq."
  ([points] (chaikin-curve points 4))
  ([points depth] (chaikin-curve points depth 0.25))
  ([points depth tightness]
   (if (<= (count points) 2)
     points
     (let [first-point (first points)
           last-point (last points)
           processed-points (chaikin-curve points depth tightness)]
       (concat [first-point]
               processed-points
               [last-point])))))
