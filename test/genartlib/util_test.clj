(ns genartlib.util-test
  (:require
    [clojure.test :refer [deftest is]]
    [genartlib.util :as u]))

(deftest test-in?
  (is (u/in? [1 2 3] 2))
  (is (u/in? '(1 2 3) 2))
  (is (u/in? #{1 2 3} 2))

  (is (not (u/in? [] 4)))
  (is (not (u/in? [1 2 3] 4)))
  (is (not (u/in? '(1 2 3) 4)))
  (is (not (u/in? #{1 2 3} 4))))

(deftest test-not-in?
  (is (not (u/not-in? [1 2 3] 2)))
  (is (not (u/not-in? '(1 2 3) 2)))
  (is (not (u/not-in? #{1 2 3} 2)))

  (is (u/not-in? [] 4))
  (is (u/not-in? [1 2 3] 4))
  (is (u/not-in? '(1 2 3) 4))
  (is (u/not-in? #{1 2 3} 4)))

(deftest test-between?
  (is (u/between? 2 1 3))
  (is (u/between? 1 1 3))
  (is (u/between? 3 1 3))
  (is (u/between? 1.0 1 3)))

(deftest test-enumerate
  (is (= [[0 :a] [1 :b]] (u/enumerate [:a :b])))
  (is (= [] (u/enumerate []))))

(deftest test-zip
  (is (= [[1 :a] [2 :b]] (u/zip [1 2] [:a :b])))
  (is (= [[1 :a] [2 :b]] (u/zip [1 2 3] [:a :b])))
  (is (= [[1 :a] [2 :b]] (u/zip [1 2] [:a :b :c])))
  (is (= [[1 :a "x"] [2 :b "y"]] (u/zip [1 2] [:a :b] ["x" "y"]))))

(deftest vec-remove
  (is (= [1 3] (u/vec-remove [1 2 3] 1))))

(deftest test-snap-to
  (is (= 2.0 (u/snap-to 1.6 1.0)))
  (is (= 2.0 (u/snap-to 1.5 1.0)))
  (is (= 1.0 (u/snap-to 1.4 1.0)))
  (is (= 100 (u/snap-to 123 100)))
  (is (= 0 (u/snap-to -12 100))))
