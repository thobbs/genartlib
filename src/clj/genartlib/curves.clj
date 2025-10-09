(ns genartlib.curves
  (:require
    [genartlib.algebra :refer [interpolate point-dist lines-intersection-point]]
    [genartlib.util :refer [enumerate w h]])
  (:import
    [genartlib LineSimplification LineSimplification$Point]))

(defn ^:private single-chaikin-step
  [points tightness]
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
  ([points] (chaikin-curve-retain-ends points 4))
  ([points depth] (chaikin-curve-retain-ends points depth 0.25))
  ([points depth tightness]
   (if (<= (count points) 2)
     points
     (let [first-point (first points)
           last-point (last points)
           processed-points (chaikin-curve points depth tightness)]
       (concat [first-point]
               processed-points
               [last-point])))))

(defn curve-length
  "Returns the total length of a curve"
  [curve]
  (->> curve
       (partition 2 1)
       (map #(apply point-dist %))
       (reduce +)))

(defn split-curve-with-step
  [curve-to-split step-size]
  (if (<= (count curve-to-split) 1)
    []
    (loop [curve (rest curve-to-split)
           segments (transient [])
           current-segment (transient [(first curve-to-split)])
           current-length 0
           prev-point (first curve-to-split)]

      (if (empty? curve)
        (persistent! (conj! segments (persistent! current-segment)))

        (let [new-point (first curve)
              new-dist (point-dist prev-point new-point)
              new-length (+ current-length new-dist)]
          (if (< new-length step-size)
            (recur (rest curve) segments (conj! current-segment new-point) new-length new-point)

            ; we need to split
            (let [dist-needed (- step-size current-length)
                  t (/ dist-needed new-dist)
                  x (interpolate (first prev-point) (first new-point) t)
                  y (interpolate (second prev-point) (second new-point) t)

                  finalized-segment (persistent! (conj! current-segment [x y]))]

              (recur curve
                     (conj! segments finalized-segment)
                     (transient [[x y]])
                     0
                     [x y]))))))))

(defn split-curve-into-parts
  [curve num-parts]
  (if (<= num-parts 1)
    curve
    (let [total-length (curve-length curve)
          segment-length (/ total-length num-parts)]
      (split-curve-with-step curve segment-length))))

(defn interpolate-curve
  "Find a point along a curve, where t is between 0.0 and 1.0. For optimization
   purposes, the curve-len can be passed in as the third param."
  [curve t & [curve-len]]
  (let [first-point (first curve)
        last-point (last curve)]

    (cond
      (<= t 0)
      first-point

      (>= t 1)
      last-point

      (= 2 (count curve))
      (let [mid-x (interpolate (first first-point) (first last-point) t)
            mid-y (interpolate (second first-point) (second last-point) t)]
        [mid-x mid-y])

      :else
      (let [total-len (or curve-len (curve-length curve))
            target-len (* total-len t)]

        (loop [curve (rest curve)
               current-length 0
               prev-point first-point]

          (if (empty? curve)
            last-point ; probably shouldn't happen?

            (let [new-point (first curve)
                  new-dist (point-dist prev-point new-point)
                  new-length (+ current-length new-dist)]

              (if (< new-length target-len)
                (recur (rest curve) new-length new-point)

                ; we need to split
                (let [dist-needed (- target-len current-length)
                      inner-t (/ dist-needed new-dist)
                      x (interpolate (first prev-point) (first new-point) inner-t)
                      y (interpolate (second prev-point) (second new-point) inner-t)]
                  [x y])))))))))

(defn ^:private point-to-line-dist
  "[x1 y1] and [x2 y2] define the line, [x0 y0] defines the point."
  [[x1 y1] [x2 y2] [x0 y0]]
  (let [denom (Math/sqrt (+ (Math/pow (- y2 y1) 2)
                            (Math/pow (- x2 x1) 2)))]
    (if (zero? denom)
      0
      (/ (Math/abs (+ (*  1 (* (- y2 y1) x0))
                      (* -1 (* (- x2 x1) y0))
                      (*  1 (* x2 y1))
                      (* -1 (* y2 x1))))
         denom))))

(defn line-simplification
  "An implementation of the Ramer-Douglas-Peucker line simplification algorithm.
   A good value for `min-tolerated-dist` is probably between 0.0001 and 0.01 times
   the image width, depending on your use case."
  [points min-tolerated-dist]
  (let [args (mapv
               (fn [[x y]] (LineSimplification$Point. x y))
               points)
        results (LineSimplification/simplify args min-tolerated-dist)]
    (map #(vector (.x %) (.y %)) results)))

(defn ^:private in-bounds?
  [[x y]]
  (and (>= x 0)
       (< x (w))
       (>= y 0)
       (< y (h))))

(defn trim-curve-to-bounds
  "Returns a seq of sub-curves that are entirely within the bounds of the image"
  [points]
  (let [border-lines [[[0 0] [(w) 0]]
                      [[(w) 0] [(w) (h)]]
                      [[(w) (h)] [0 (h)]]
                      [[0 (h)] [0 0]]]

        p1 (first points)
        starting-in-bounds? (in-bounds? p1)
        initial-segment (if starting-in-bounds? [p1] [])]

    (loop [points (rest points)
           segments []
           current-segment initial-segment
           was-in-bounds? starting-in-bounds?
           prev-point p1]

      (if (empty? points)
        (let [final-segments (conj segments current-segment)]
          (vec
            (filter
              #(>= (count %) 2)
              final-segments)))

        (let [next-point (first points)
              next-in-bounds? (in-bounds? next-point)]

          (cond
            ; still in bounds
            (and was-in-bounds? next-in-bounds?)
            (recur (rest points) segments (concat current-segment [next-point]) true next-point)

            ; still out of bounds
            (and (not was-in-bounds?) (not next-in-bounds?))
            (recur (rest points) segments current-segment false next-point)

            ; entering the bounds
            (and (not was-in-bounds?) next-in-bounds?)
            (let [inter (some
                          (fn [[p1 p2]]
                            (lines-intersection-point
                              prev-point next-point
                              p1 p2))
                          border-lines)]
              (recur (rest points) segments [inter next-point] true next-point))

            ; leaving the bounds
            (and was-in-bounds? (not next-in-bounds?))
            (let [inter (some
                          (fn [[p1 p2]]
                            (lines-intersection-point
                              prev-point next-point
                              p1 p2))
                          border-lines)]
              (recur (rest points) (conj segments (concat current-segment [inter])) [] false next-point))))))))

(defn trim-curve-start
  "Removes the specified length from the start of a curve. If the specified length
   is longer than the entire curve length, an empty vector is returned."
  [points trim-length]
  (if (<= (count points) 1)
    []
    (loop [remaining-points (rest points)
           current-length 0
           prev-point (first points)]

      (if (empty? remaining-points)
        []

        (let [new-point (first remaining-points)
              new-dist (point-dist prev-point new-point)
              new-length (+ current-length new-dist)]
          (if (< new-length trim-length)
            (recur (rest remaining-points) new-length new-point)

            ; we need to split
            (let [dist-needed (- trim-length current-length)
                  t (/ dist-needed new-dist)
                  x (interpolate (first prev-point) (first new-point) t)
                  y (interpolate (second prev-point) (second new-point) t)]

              (concat [[x y]] remaining-points))))))))

(defn trim-curve-end
  "Removes the specified length from the end of a curve. If the specified length
   is longer than the entire curve length, an empty vector is returned."
  [points trim-length]
  (-> points
      (reverse)
      (trim-curve-start trim-length)
      (reverse)))
