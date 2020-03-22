(ns genartlib.geometry-test
  (:require
    [clojure.test :refer [deftest is]]
    [genartlib.geometry :as g]
    [genartlib.util :refer [pi]]))

(deftest rotate-polygon-test
  (let [poly [[-1.0 1.0]
              [1.0 1.0]
              [1.0 -1.0]
              [-1.0 -1.0]]

        rotated (g/rotate-polygon (pi 0.25) poly)]

    (is (< 0.001 (Math/abs (first (nth rotated 0)))))
    (is (< 0.001 (Math/abs (second (nth rotated 1)))))
    (is (< 0.001 (Math/abs (first (nth rotated 2)))))
    (is (< 0.001 (Math/abs (second (nth rotated 3)))))))

(deftest polygon-contains-point-test
  (let [poly [[-1.0 1.0]
              [1.0 1.0]
              [1.0 -1.0]
              [-1.0 -1.0]]
        x-verts (map first poly)
        y-verts (map second poly)]
    (is (g/polygon-contains-point? x-verts y-verts 0 0))
    (is (g/polygon-contains-point? x-verts y-verts -1.0 0))
    (is (g/polygon-contains-point? x-verts y-verts -1.0 0.999))

    (is (not (g/polygon-contains-point? x-verts y-verts -1.0 1.0)))
    (is (not (g/polygon-contains-point? x-verts y-verts -1.0 1.001)))
    (is (not (g/polygon-contains-point? x-verts y-verts 1.0 1.001)))))

(deftest shrink-polygon-test
  (let [poly [[-1.0 1.0]
              [1.0 1.0]
              [1.0 -1.0]
              [-1.0 -1.0]]]
    (is (= [[-0.5 0.5]
              [0.5 0.5]
              [0.5 -0.5]
              [-0.5 -0.5]]
           (g/shrink-polygon poly 0.5)))))
