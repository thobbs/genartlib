(ns genartlib.random
  (:use [genartlib.util :only [between?]])
  (:use [quil.core :only [random-gaussian cos sin abs random sqrt TWO-PI]])
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

(defn triangular [lower mode upper]
  "Returns a single sample from a Triangular distribution"
  (let [lower-extent     (- mode lower)
        upper-extent     (- upper mode)
        full-extent      (- upper lower)
        x                (random 0.0 1.0)
        transition-point (/ lower-extent full-extent)]
    (if (<= x transition-point)
      (+ lower (sqrt (* x       full-extent lower-extent)))
      (- upper (sqrt (* (- 1 x) full-extent upper-extent))))))

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
  (assert (zero? (mod (count items-and-weights) 2)))
  (assert (>= (count items-and-weights) 2))
  (let [r (random 0 1.0)]
    (loop [weight-seen 0
           remaining-items items-and-weights]
      (if (<= (count remaining-items) 2)
        (first remaining-items)
        (let [; -- (println "remaining items:" remaining-items)
              ; -- (assert (> (count remaining-items) 2))
              ; -- (assert (not (nil? weight-seen)))
              new-weight (second remaining-items)
              end-bound (+ weight-seen new-weight)]
          (if (between? r weight-seen end-bound)
            (first remaining-items)
            (recur (+ weight-seen (second remaining-items)) (drop 2 remaining-items))))))))

(defn repeatable-shuffle [items]
  "A version of shuffle that uses Processing's random fn to ensure that
   the same random seed is used."
  (map second
       (sort (for [item items]
               [(random 0.0 1.0) item]))))
