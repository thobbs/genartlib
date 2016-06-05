(ns genartlib.geometry
  (:use [genartlib.algebra :only [angle]])
  (:use [genartlib.util :only [between?]])
  (:use [incanter.core :only [$=]])
  (:use [quil.core :only [dist cos sin]]))

  (defn rotate-polygon [theta & points]
    "Rotates a polygon about its centroid.  The theta argument determines
     how the polygon is rotated.  `points` is a sequence of [x y] pairs that
     define the polygon"
    (if (zero? theta)
      points
      (let [xs (take-nth 2 points)
            ys (take-nth 2 (rest points))
            x-centroid (/ (apply + xs) (count xs))
            y-centroid (/ (apply + ys) (count ys))
            points (map vector xs ys)]
        (mapcat (fn [[x y]]
                  (let [current-angle (angle x-centroid y-centroid x y)
                        new-angle (- current-angle theta)
                        hypot (dist x y x-centroid y-centroid)
                        x-offset (* hypot (cos new-angle))
                        y-offset (* hypot (sin new-angle))]
                    [(+ x-offset x-centroid) (+ y-offset y-centroid)]))
                points))))

  (defn polygon-contains-point? [x-verts y-verts test-x test-y]
    "Returns true if a polygon contains the given point.  The polygon
     is defined by the first two arguments: a sequence of x values
     and a sequence of y values that define the polygon."
    (let [num-verts (count x-verts)]
      (loop [i 0
             j (dec num-verts)
             c false]

        (if ($= i < num-verts)
          (let [delta-x (- (nth x-verts j) (nth x-verts i))
                y-spread (- test-y (nth y-verts i))
                delta-y (- (nth y-verts j) (nth y-verts i))]
            (if (and (not= (> (nth y-verts i) test-y)
                           (> (nth y-verts j) test-y))
                     (< test-x (+ (/ (* (- (nth x-verts j) (nth x-verts i))
                                        (- test-y (nth y-verts i)))
                                     (- (nth y-verts j) (nth y-verts i)))
                                  (nth x-verts i))))
              (recur (inc i) i (not c))
              (recur (inc i) i c)))
          c))))

  (defn shrink-polygon [points ratio]
    (let [x-points (map first points)
          y-points (map second points)

          x-centroid (apply avg x-points)
          y-centroid (apply avg y-points)]
      (for [[x y] points]
        [(interpolate x x-centroid (- 1.0 ratio))
         (interpolate y y-centroid (- 1.0 ratio))])))
