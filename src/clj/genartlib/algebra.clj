(ns genartlib.algebra
  (:require [genartlib.util :refer [between?]]
            [quil.core :refer [HALF-PI PI TWO-PI atan2 cos sin dist]]))

  (defn avg
    "Returns the average of the arguments"
    [& values]
    (/ (apply + values) (count values)))

  (defn interpolate
    "Interpolates a point between [start, finish] at point t, where
     t is between 0.0 and 1.0"
    [start finish t]
    (+ (* (- 1.0 t) start) (* t finish)))

  (defn rescale
    "Rescales value from range [old-min, old-max] to [new-min, new-max]"
    [value old-min old-max new-min new-max]
    (let [old-spread (- old-max old-min)
          new-spread (- new-max new-min)]
      (+ (* (- value old-min) (/ new-spread old-spread))
         new-min)))

  (defn line-intersection
    "Finds the intersection of two lines.  Each line argument is a vector
     of the form [slope y-intersect start-x end-x]"
    [line1 line2]
    (let [[m b start-x-1 end-x-1] line1
          [n c start-x-2 end-x-2] line2]
      (when (> (Math/abs (- m n)) 0.001)
        (let [x (/ (- c b) (- m n))
              y (+ (* m x) b)]
          (when (and (between? x start-x-1 end-x-1)
                     (between? x start-x-2 end-x-2))
            [x y])))))

  (declare slope)
  (declare y-intercept)

  (defn lines-intersection-point [[start-x-1 start-y-1] [end-x-1 end-y-1]
                                  [start-x-2 start-y-2] [end-x-2 end-y-2]]

    (let [[[start-x-1 start-y-1] [end-x-1 end-y-1]] (if (> end-x-1 start-x-1)
                                                      [[start-x-1 start-y-1] [end-x-1 end-y-1]]
                                                      [[end-x-1 end-y-1] [start-x-1 start-y-1]])

          [[start-x-2 start-y-2] [end-x-2 end-y-2]] (if (> end-x-2 start-x-2)
                                                      [[start-x-2 start-y-2] [end-x-2 end-y-2]]
                                                      [[end-x-2 end-y-2] [start-x-2 start-y-2]])

          slope-1 (slope start-x-1 start-y-1 end-x-1 end-y-1)
          y-intercept-1 (if (nil? slope-1)
                          nil
                          (y-intercept slope-1 start-x-1 start-y-1))
          slope-2 (slope start-x-2 start-y-2 end-x-2 end-y-2)
          y-intercept-2 (if (nil? slope-2)
                          nil
                          (y-intercept slope-2 start-x-2 start-y-2))]

      (if (nil? slope-1)

        (if (nil? slope-2)
          nil
          ; first line is vertical, second line is not; solve for y in second equation
          (let [line-2-point-y (+ y-intercept-2 (* slope-2 start-x-1))]
            (if (and (between? start-x-1 start-x-2 end-x-2)
                     (between? line-2-point-y start-y-1 end-y-1))
              [start-x-1 line-2-point-y]
              nil)))

        (if (nil? slope-2)
          ; second line is vertical, first line is not; solve for y in first equation
          (let [line-1-point-y (+ y-intercept-1 (* slope-1 start-x-2))]
            (if (and (between? start-x-2 start-x-1 end-x-1)
                     (between? line-1-point-y start-y-2 end-y-2))
              [start-x-2 line-1-point-y]
              nil))

          ; neither line is vertical
          (line-intersection [slope-1 y-intercept-1 start-x-1 end-x-1]
                             [slope-2 y-intercept-2 start-x-2 end-x-2])))))

  (defn slope
    "Returns the slope of a line between two points: (x1, y1), (x2, y2)"
    [x1 y1 x2 y2]
    (if (= x1 x2)
      nil
      (/ (- y2 y1) (- x2 x1))))

  (defn point-slope
    "Returns the slope of a line between two points: (x1, y1), (x2, y2)"
    [[x1 y1] [x2 y2]]
    (slope x1 y1 x2 y2))

  (defn y-intercept
    "Returns the y-intercept of a line with a given slope
     and one point on the line (x1, y1)"
    [slope x1 y1]
    (- y1 (* slope x1)))

  (defn angle [x1 y1 x2 y2]
    "Returns the angle between two points in radians. Values are between
     0 and 2 * PI."
    (let [a (atan2 (- y2 y1) (- x2 x1))]
      (if (neg? a)
        (+ a (* PI 2.0))
        a)))

  (defn point-angle
    "Returns the angle between two points in radians"
    [[x1 y1] [x2 y2]]
    (angle x1 y1 x2 y2))

  (defn angular-coords
    "Returns an [x y] vector representing a point that is
     offset from a center ([center-x center-y]) by a given
     angle and magnitude"
    [center-x center-y theta magnitude]
    [(+ center-x (* magnitude (cos theta)))
     (+ center-y (* magnitude (sin theta)))])

  (defn point-dist
    "Returns the euclidean distance between two points"
    [[x1 y1] [x2 y2]]
    (dist x1 y1 x2 y2))
