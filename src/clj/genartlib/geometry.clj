(ns genartlib.geometry
  (:require
    [genartlib.algebra :refer [angle avg interpolate]]
    [quil.core :refer [dist cos sin]])
  (:import
    [genartlib PolyUtils]))

(defn rotate-polygon
  "Rotates a polygon clockwise about its centroid.  The theta argument determines
   how the polygon is rotated.  `points` is a sequence of [x y] pairs that
   define the polygon"
  [theta points]
  (if (zero? theta)
    points
    (let [xs (map first points)
          ys (map second points)
          min-x (apply min xs)
          max-x (apply max xs)
          min-y (apply min ys)
          max-y (apply max ys)
          x-centroid (/ (+ min-x max-x) 2.0)
          y-centroid (/ (+ min-y max-y) 2.0)
          points (map vector xs ys)]
      (map (fn [[x y]]
             (let [current-angle (angle x-centroid y-centroid x y)
                   new-angle (+ current-angle theta)
                   hypot (dist x y x-centroid y-centroid)
                   x-offset (* hypot (cos new-angle))
                   y-offset (* hypot (sin new-angle))]
               [(+ x-offset x-centroid) (+ y-offset y-centroid)]))
             points))))

(defn polygon-contains-point?
  "Returns true if a polygon contains the given point.  The polygon
   is defined by the first two arguments: a sequence of x values
   and a sequence of y values that define the polygon."
  [x-verts y-verts test-x test-y]
  (PolyUtils/polygonContainsPoint (double-array x-verts) (double-array y-verts) (double test-x) (double test-y)))

(defn shrink-polygon
  "Pulls all points in a polygon towards the centroid. The provided
   ratio controls how far the points are pulled, with 0 producing no
   change and 1.0 pulling all the way to the centroid."
  [points ratio]
  (let [x-points (map first points)
        y-points (map second points)

        x-centroid (apply avg x-points)
        y-centroid (apply avg y-points)]
    (for [[x y] points]
      [(interpolate x x-centroid (- 1.0 ratio))
       (interpolate y y-centroid (- 1.0 ratio))])))
