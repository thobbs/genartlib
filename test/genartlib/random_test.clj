(ns genartlib.random-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [genartlib.random :as r])
  (:import [java.util Random]))

(defn- rng [] (Random. 42))

(deftest test-uniform
  (let [v (r/uniform 1.0 10.0 (rng))]
    (is (number? v))
    (is (<= 1.0 v))
    (is (< v 10.0))))

(deftest test-gauss
  (is (number? (r/gauss 0.0 1.0 (rng)))))

(deftest test-abs-gauss
  (let [v (r/abs-gauss 0.0 1.0 (rng))]
    (is (number? v))
    (is (>= v 0.0))))

(deftest test-gauss-range
  (let [s (r/gauss-range 5.0 1.0 (rng))]
    (is (sequential? s))
    (is (every? integer? s))))

(deftest test-triangular
  (testing "covers both branches across many samples"
    (let [rng-inst (rng)
          samples (repeatedly 200 #(r/triangular 0.0 5.0 10.0 rng-inst))]
      (is (every? number? samples))
      (is (every? #(<= 0.0 % 10.0) samples))))
  (testing "asymmetric distribution exercises both branches"
    (let [rng-inst (rng)
          samples (repeatedly 200 #(r/triangular 0.0 1.0 10.0 rng-inst))]
      (is (some #(< % 1.0) samples))
      (is (some #(> % 1.0) samples)))))

(deftest test-simple-triangular
  (let [v (r/simple-triangular 5.0 (rng))]
    (is (number? v))
    (is (<= 0.0 v 5.0))))

(deftest test-pareto-sampler
  (let [sampler (r/pareto-sampler 1.0 2.0)]
    (is (fn? sampler))
    (is (number? (sampler)))
    (is (>= (sampler) 1.0))))

(deftest test-pareto-sample
  (let [v (r/pareto-sample 1.0 2.0)]
    (is (number? v))
    (is (>= v 1.0))))

(deftest test-random-point-in-circle
  (let [[x y] (r/random-point-in-circle 100.0 200.0 10.0 (rng))]
    (is (number? x))
    (is (number? y))
    (let [dx (- x 100.0)
          dy (- y 200.0)]
      (is (<= (Math/sqrt (+ (* dx dx) (* dy dy))) 10.0001)))))

(deftest test-odds
  (let [rng-inst (rng)]
    (is (boolean? (r/odds 0.5 rng-inst)))
    (is (true? (r/odds 1.0 rng-inst)))
    (is (false? (r/odds 0.0 rng-inst)))))

(deftest test-choice
  (let [rng-inst (rng)
        items [:a :b :c :d]
        picks (repeatedly 50 #(apply r/choice rng-inst items))]
    (is (every? (set items) picks))))

(deftest test-weighted-choice
  (testing "returns one of the items"
    (let [rng-inst (rng)
          picks (repeatedly 50 #(r/weighted-choice rng-inst :a 1.0 :b 2.0 :c 3.0))]
      (is (every? #{:a :b :c} picks))))
  (testing "throws on odd number of args"
    (is (thrown? IllegalArgumentException
                 (r/weighted-choice (rng) :a 1.0 :b))))
  (testing "throws on too few args"
    (is (thrown? IllegalArgumentException
                 (r/weighted-choice (rng))))))

(deftest test-repeatable-shuffle
  (let [items [1 2 3 4 5 6 7 8 9 10]
        shuffled (r/repeatable-shuffle items (rng))]
    (is (= (sort items) (sort shuffled))))
  (testing "same seed produces same order"
    (let [items [1 2 3 4 5 6 7 8 9 10]
          a (r/repeatable-shuffle items (Random. 123))
          b (r/repeatable-shuffle items (Random. 123))]
      (is (= a b)))))

(deftest test-limited-shuffle
  (let [items [1 2 3 4 5 6 7 8 9 10]
        shuffled (r/limited-shuffle 3 items (rng))]
    (is (= (sort items) (sort shuffled)))
    (is (= (count items) (count shuffled))))
  (testing "n=0 returns items unchanged"
    (let [items [1 2 3 4 5]
          shuffled (r/limited-shuffle 0 items (rng))]
      (is (= items (vec shuffled))))))
