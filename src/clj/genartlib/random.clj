(ns genartlib.random
  (:use [genartlib.util :only [between?]])
  (:use [quil.core :only [random-gaussian cos sin abs random sqrt TWO-PI]])
  (:use [incanter.core :only [$=]])
  (:import [org.apache.commons.math3.distribution ParetoDistribution]))

(defn gauss [mean variance]
  "Samples a single value from a Gaussian distribution with the given mean
   and variance"
  (+ mean (* variance (random-gaussian))))

(defn abs-gauss [mean variance]
  "Returns the absolute value of a single sample from a Gaussian distribution
   with the given mean and variance"
  (abs (gauss mean variance)))

(defn gauss-range [mean variance]
  "Returns a sequence of integers from zero to a value sampled from a Gaussian
   distribution with the given mean and variance"
  (range (int (abs-gauss mean variance))))

(defn triangular [a b c]
  "Returns a single sample from a Triangular distribution where:
   - 'a' is the lower limit
   - 'c' is the upper limit
   - 'b' is the mode
  "
  (let [x (random 0.0 1.0)
        transition-point ($= (c - a) / (b - a))]
    (if ($= x <= transition-point)
      (+ a (sqrt (* x (- b a) (- c a))))
      (- b (sqrt (* (1 - x) (- b a) (- b c)))))))

(defn simple-triangular [b]
  "Like triangular, but assumes a = 0 and b = c"
  (* b (sqrt (random 0.0 1.0))))

(defn pareto-sampler [scale shape]
  "Returns a function to draw values from a ParetoDistribution with the given
   scale and shape."
  (let [distribution (ParetoDistribution. scale shape)]
    (fn [] (.sample distribution))))

(defn pareto-sample [scale shape]
  "Draws a single sample from a ParetoDistribution with the given scale and
   shape."
  (.sample (ParetoDistribution. scale shape)))

(defn random-point-in-circle [x y radius]
  "Picks a random point in a circle with a given center and radius"
  (let [theta (random 0 TWO-PI)
        r (simple-triangular radius)
        x-offset (* r (cos theta))
        y-offset (* r (sin theta))]
    [(+ x x-offset) (+ y y-offset)]))

(defn odds [chance]
  "Returns true with probability 'chance', where change is between 0 and 1.0"
  (< (random 0.0 1.0) chance))

(defn choice [& items]
  "Uniformly chooses one of the arguments"
  (nth items (int (random (count items)))))

(defn weighted-choice [& items-and-weights]
  "Given a sequence of alternating item, weight arguments, chooses one of the
   items with a probability equal to the weight.  Each weight should be
   between 0.0 and 1.0, and all weights should sum to 1.0."
  (let [r (random 0 1.0)]
    (loop [weight-seen 0
           remaining-items items-and-weights]
      (if (= 2 (count remaining-items))
        (first remaining-items)
        (if (between? r weight-seen (+ weight-seen (second remaining-items)))
          (first remaining-items)
          (recur (+ weight-seen (second remaining-items)) (nthrest remaining-items 2)))))))
