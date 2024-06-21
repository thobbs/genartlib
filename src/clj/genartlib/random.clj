(ns genartlib.random
  (:require [genartlib.util :refer [between?]]
            [quil.core :refer [random-gaussian cos sin abs random sqrt TWO-PI]])
  (:import [org.apache.commons.math3.distribution ParetoDistribution]))

(defn gauss
  "Samples a single value from a Gaussian distribution with the given mean
   and variance"
  [mean variance]
  (+ mean (* variance (random-gaussian))))

(defn abs-gauss
  "Returns the absolute value of a single sample from a Gaussian distribution
   with the given mean and variance"
  [mean variance]
  (abs (gauss mean variance)))

(defn gauss-range
  "Returns a sequence of integers from zero to a value sampled from a Gaussian
   distribution with the given mean and variance"
  [mean variance]
  (range (int (abs-gauss mean variance))))

(defn triangular
  "Returns a single sample from a Triangular distribution"
  [lower mode upper]
  (let [lower-extent     (- mode lower)
        upper-extent     (- upper mode)
        full-extent      (- upper lower)
        x                (random 0.0 1.0)
        transition-point (/ lower-extent full-extent)]
    (if (<= x transition-point)
      (+ lower (sqrt (* x       full-extent lower-extent)))
      (- upper (sqrt (* (- 1 x) full-extent upper-extent))))))

(defn simple-triangular
  "Like triangular, but assumes a = 0 and b = c"
  [b]
  (* b (sqrt (random 0.0 1.0))))

(defn pareto-sampler
  "Returns a function to draw values from a ParetoDistribution with the given
   scale and shape."
  [scale shape]
  (let [distribution (ParetoDistribution. scale shape)]
    (fn [] (.sample distribution))))

(defn pareto-sample
  "Draws a single sample from a ParetoDistribution with the given scale and
   shape."
  [scale shape]
  (.sample (ParetoDistribution. scale shape)))

(defn random-point-in-circle
  "Picks a random point in a circle with a given center and radius"
  [x y radius]
  (let [theta (random 0 TWO-PI)
        r (simple-triangular radius)
        x-offset (* r (cos theta))
        y-offset (* r (sin theta))]
    [(+ x x-offset) (+ y y-offset)]))

(defn odds
  "Returns true with probability 'chance', where change is between 0 and 1.0"
  [chance]
  (< (random 0.0 1.0) chance))

(defn choice
  "Uniformly chooses one of the arguments"
  [& items]
  (nth items (int (random (count items)))))

(defn weighted-choice
  "Given a sequence of alternating item and weight arguments, chooses one of the
   items with a probability equal to the weight. Each weight should be a
   positive value."
  [& items-and-weights]
  (when (or (odd? (count items-and-weights))
            (< (count items-and-weights) 2))
    (throw (IllegalArgumentException. "weighted-choice expects a (non-zero) even number of arguments")))
  (let [total-weight (->> items-and-weights (rest) (take-nth 2) (reduce +))
        r (* (random 0.0 1.0) total-weight)]
    (loop [items-and-weights items-and-weights
           cumulative-weight 0]
      (let [next-item (first items-and-weights)
            next-weight (second items-and-weights)
            new-cumulative-weight (+ cumulative-weight next-weight)]
        (if (>= new-cumulative-weight r)
          next-item
          (recur (drop 2 items-and-weights) new-cumulative-weight))))))

(defn repeatable-shuffle
  "A version of shuffle that uses Processing's random fn to ensure that
   the same random seed is used."
  [items]
  (map second
       (sort (for [item items]
               [(random 0.0 1.0) item]))))

(defn- vec-swap [v i j]
  (-> v
      (assoc i (v j))
      (assoc j (v i))))

(defn limited-shuffle
  "A limited shuffle that only executes n random swaps of the
   contents of `items`. The lower n is, the closer `items` will
   be to its original order."
  [n items]
  (loop [altered-items (vec items)
         n n]
    (if (<= n 0)
      altered-items
      (let [i (int (random 0 (count items)))
            j (int (random 0 (count items)))]
        (recur (vec-swap altered-items i j) (dec n))))))
