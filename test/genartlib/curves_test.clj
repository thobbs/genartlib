(ns genartlib.curves-test
  (:require [clojure.test :refer [deftest is]]
            [genartlib.curves :as c]))

(deftest chaikin-curve-test
  (let [f #'c/chaikin-curve]
    (is (= (repeat 4 [0.0 0.0])
           (f (repeat 3 [0 0]) 1 0.25)))

    (is (= [[1.0 1.0] [3.0 3.0] [5.0 3.0] [7.0 1.0]]
           (f [[0 0] [4 4] [8 0]] 1 0.25)))

    (is (= [[2.0 2.0] [2.0 2.0] [6.0 2.0] [6.0 2.0]]
           (f [[0 0] [4 4] [8 0]] 1 0.5)))

    (is (= [[1.0 1.0] [3.0 3.0]]
           (f [[0 0] [4 4]] 1 0.25)))

    (is (empty? (f [[0 0]] 1 0.5)))))
