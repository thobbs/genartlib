(ns genartlib.curves
  (:use [genartlib.algebra :only [interpolate]]))

(defn- single-chaikin-step [points tightness]
  (loop [points points
         new-points [(first points)]]
    (if (<= (count points) 1)
      (vec (concat new-points [(last points)]))
      (let [[start-x start-y] (first points)
            [end-x end-y] (second points)
            q-x (interpolate start-x end-x (+ 0.0 tightness))
            q-y (interpolate start-y end-y (+ 0.0 tightness))
            r-x (interpolate start-x end-x (- 1.0 tightness))
            r-y (interpolate start-y end-y (- 1.0 tightness))]
        (recur (rest points) (concat new-points [[q-x q-y] [r-x r-y]]))))))

(defn chaikin-curve

  "Forms a Chaikin curve from a seq of points, returning a new
   seq of points.

   The tightness parameter controls how sharp the corners will be,
   and should be a value between 0.0 and 0.5.  A value of 0.0 retains
   full sharpness, and 0.5 creates maximum smoothness.

   The depth parameter controls how many recursive steps will occur.
   The more steps, the smoother the curve is (assuming tightness is
   greater than zero). Suggested values are between 1 and 8, with a
   good default being 4.

   When points form a closed polygon, it's recommended that the start
   point be repeated at the end of points to avoid a gap."

  ([points] (chaikin-curve points 4))
  ([points depth] (chaikin-curve points depth 0.25))

  ([points depth tightness]
    (loop [depth depth
           points points]
      (if (zero? depth)
        points
        (recur (dec depth) (single-chaikin-step points tightness))))))
