(ns genartlib.algebra-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [genartlib.algebra :as a]
    [genartlib.util :refer [pi]]))

(deftest avg-test
  (is (= 2 (a/avg 1 2 3)))
  (is (= 1.5 (a/avg 1.0 2.0))))

(deftest interpolate-test
  (is (= 0.0 (a/interpolate 0.0 1.0 0.0)))
  (is (= 0.5 (a/interpolate 0.0 1.0 0.5)))
  (is (= 1.0 (a/interpolate 0.0 1.0 1.0)))

  (is (= 0.0 (a/interpolate 0.0 10.0 0.0)))
  (is (= 5.0 (a/interpolate 0.0 10.0 0.5)))
  (is (= 10.0 (a/interpolate 0.0 10.0 1.0)))

  (is (= 10.0 (a/interpolate 10.0 0.0 0.0)))
  (is (= 5.0 (a/interpolate 10.0 0.0 0.5)))
  (is (= 0.0 (a/interpolate 10.0 0.0 1.0)))

  (is (= 41.66666666666667 (a/interpolate {:exponent 2} 0 100 0.5)))
  (is (= 54.25822558944361 (a/interpolate {:exponent 0.5} 0 100 0.5)))
  (is (= 75.50813375962908 (a/interpolate {:tanh-factor -0.5} 0 100 0.5)))
  (is (= 63.51489523872873 (a/interpolate {:tanh-factor 1.5} 0 100 0.5))))

(deftest rescale-test
  (is (= 5.0 (a/rescale 0.5 0.0 1.0 0.0 10.0)))
  (is (= 2.5 (a/rescale 0.75 0.0 1.0 10.0 0.0))))

(deftest line-intersection-test
  (testing "Normal intersection"
    (let [x-start 0.0
          x-end 100.0
          line1 [1.0 0 x-start x-end]
          line2 [-1.0 50 x-start x-end]]
      (is (= [25.0 25.0] (a/line-intersection line1 line2)))))

  (testing "No interesction due to x bounds"
    (let [x-start 0.0
          x-end 10.0
          line1 [1.0 0 x-start x-end]
          line2 [-1.0 50 x-start x-end]]
      (is (nil? (a/line-intersection line1 line2))))

    (let [x-start 30.0
          x-end 100.0
          line1 [1.0 0 x-start x-end]
          line2 [-1.0 50 x-start x-end]]
      (is (nil? (a/line-intersection line1 line2)))))

  (testing "No interesction due to parallel lines"
    (let [x-start 0.0
          x-end 100.0
          line1 [1.0 0 x-start x-end]
          line2 [1.0 50 x-start x-end]]
      (is (nil? (a/line-intersection line1 line2))))))

(deftest lines-intersection-point-test
  (testing "Normal intersection"
    (let [line-1-start [0.0 0.0]
          line-1-end [100.0 100.0]

          line-2-start [0.0 50.0]
          line-2-end [100.0 -50.0]]

      (is (= [25.0 25.0] (a/lines-intersection-point line-1-start line-1-end
                                                     line-2-start line-2-end)))
      (is (= [25.0 25.0] (a/lines-intersection-point line-2-start line-2-end
                                                     line-1-start line-1-end)))))

  (testing "Intersection at end point"
    (let [line-1-start [0.0 0.0]
          line-1-end [25.0 25.0]

          line-2-start [0.0 50.0]
          line-2-end [25.0 25.0]]

      (is (= [25.0 25.0] (a/lines-intersection-point line-1-start line-1-end
                                                     line-2-start line-2-end)))
      (is (= [25.0 25.0] (a/lines-intersection-point line-2-start line-2-end
                                                     line-1-start line-1-end)))))

  (testing "Intersection at start point"
    (let [line-1-start [25.0 25.0]
          line-1-end [0.0 0.0]

          line-2-start [25.0 25.0]
          line-2-end [0.0 50.0]]

      (is (= [25.0 25.0] (a/lines-intersection-point line-1-start line-1-end
                                                     line-2-start line-2-end)))
      (is (= [25.0 25.0] (a/lines-intersection-point line-2-start line-2-end
                                                     line-1-start line-1-end)))))

  (testing "No intersection due to bounds"
    (let [line-1-start [0.0 0.0]
          line-1-end [100.0 100.0]

          line-2-start [0.0 50.0]
          line-2-end [15.0 40.0]]

      (is (nil? (a/lines-intersection-point line-1-start line-1-end
                                            line-2-start line-2-end)))
      (is (nil? (a/lines-intersection-point line-2-start line-2-end
                                            line-1-start line-1-end)))))

  (testing "Intersection with one vertical line"
    (let [line-1-start [0.0 0.0]
          line-1-end [100.0 100.0]

          line-2-start [10.0 50.0]
          line-2-end [10.0 -50.0]]

      (is (= [10.0 10.0] (a/lines-intersection-point line-1-start line-1-end
                                                     line-2-start line-2-end)))
      (is (= [10.0 10.0] (a/lines-intersection-point line-2-start line-2-end
                                                     line-1-start line-1-end)))))

  (testing "No intersection with two vertical lines"
    (let [line-1-start [0.0 0.0]
          line-1-end [0.0 -100.0]

          line-2-start [10.0 50.0]
          line-2-end [10.0 -50.0]]

      (is (nil? (a/lines-intersection-point line-1-start line-1-end
                                            line-2-start line-2-end)))
      (is (nil? (a/lines-intersection-point line-2-start line-2-end
                                            line-1-start line-1-end))))))

(deftest slope-test
  (is (zero? (a/slope 0 0 10 0)))
  (is (nil? (a/slope 0 0 0 10)))
  (is (= 1 (a/slope 0 0 10 10)))
  (is (= -1 (a/slope 0 0 10 -10))))

(deftest point-slope-test
  (is (zero? (a/point-slope [0 0] [10 0])))
  (is (nil? (a/point-slope [0 0] [0 10])))
  (is (= 1 (a/point-slope [0 0] [10 10])))
  (is (= -1 (a/point-slope [0 0] [10 -10]))))

(deftest y-intercept-test
  (is (zero? (a/y-intercept 100.0 0 0)))
  (is (= 100.0 (a/y-intercept 100.0 1 200.0))))

(deftest angle-test
  (is (= (pi 0.0) (a/angle 0 0 100.0 0.0)))
  (is (= (pi 1.0) (a/angle 0 0 -100.0 0.0)))
  (is (= (pi 0.5) (a/angle 0 0 0.0 100.0)))
  (is (= (pi 1.5) (a/angle 0 0 0.0 -100.0))))

(deftest point-angle-test
  (is (= (pi 0.0) (a/point-angle [0 0] [100.0 0.0])))
  (is (= (pi 1.0) (a/point-angle [0 0] [-100.0 0.0])))
  (is (= (pi 0.5) (a/point-angle [0 0] [0.0 100.0])))
  (is (= (pi 1.5) (a/point-angle [0 0] [0.0 -100.0]))))

(defn ^:private approx=
  [p1 p2]
  (< (Math/abs
       (Math/sqrt (+ (Math/pow (- (first p1) (first p2)) 2)
                     (Math/pow (- (second p1) (second p2)) 2))))
     0.001))

(deftest angular-coords-test
  (is (approx= [1.0 0.0] (a/angular-coords 0 0 (pi 0.0) 1.0)))
  (is (approx= [2.0 0.0] (a/angular-coords 0 0 (pi 0.0) 2.0)))
  (is (approx= [-1.0 0.0] (a/angular-coords 0 0 (pi 0.0) -1.0)))

  (is (approx= [0.0 1.0] (a/angular-coords 0 0 (pi 0.5) 1.0)))
  (is (approx= [-1.0 0.0] (a/angular-coords 0 0 (pi -1.0) 1.0)))
  (is (approx= [-1.0 0.0] (a/angular-coords 0 0 (pi 1.0) 1.0))))

(deftest point-dist-test
  (is (= 1.0 (a/point-dist [0.0 0.0] [1.0 0.0]))))
