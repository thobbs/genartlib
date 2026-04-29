(ns genartlib.random
  (:require [genartlib.util :refer [between?]]
            [quil.core :as q])
  (:import [org.apache.commons.math3.distribution ParetoDistribution]))

(defn uniform
  "Samples a single value from a uniform distribution within the given range.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [min-value max-value & [random-obj]]
  (if (nil? random-obj)
    (q/random min-value max-value)
    (+ min-value (* (- max-value min-value) (.nextDouble random-obj)))))

(defn gauss
  "Samples a single value from a Gaussian distribution with the given mean
   and variance.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [mean variance & [random-obj]]
  (if (nil? random-obj)
    (+ mean (* variance (q/random-gaussian)))
    (+ mean (* variance (.nextGaussian random-obj)))))

(defn abs-gauss
  "Returns the absolute value of a single sample from a Gaussian distribution
   with the given mean and variance.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [mean variance & [random-obj]]
  (abs (gauss mean variance random-obj)))

(defn gauss-range
  "Returns a sequence of integers from zero to a value sampled from a Gaussian
   distribution with the given mean and variance.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [mean variance & [random-obj]]
  (range (int (abs-gauss mean variance random-obj))))

(defn triangular
  "Returns a single sample from a Triangular distribution.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [lower mode upper & [random-obj]]
  (let [lower-extent     (- mode lower)
        upper-extent     (- upper mode)
        full-extent      (- upper lower)
        x                (if (nil? random-obj)
                           (uniform 0.0 1.0)
                           (.nextDouble random-obj))
        transition-point (/ lower-extent full-extent)]
    (if (<= x transition-point)
      (+ lower (Math/sqrt (* x       full-extent lower-extent)))
      (- upper (Math/sqrt (* (- 1 x) full-extent upper-extent))))))

(defn simple-triangular
  "Like triangular, but assumes a = 0 and b = c.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [b & [random-obj]]
  (* b (Math/sqrt (uniform 0.0 1.0 random-obj))))

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
  "Picks a random point in a circle with a given center and radius.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [x y radius & [random-obj]]
  (let [theta (uniform 0 (* Math/PI 2) random-obj)
        r (simple-triangular radius random-obj)
        x-offset (* r (Math/cos theta))
        y-offset (* r (Math/sin theta))]
    [(+ x x-offset) (+ y y-offset)]))

(defn odds
  "Returns true with probability 'chance', where change is between 0 and 1.0.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [chance & [random-obj]]
  (< (uniform 0.0 1.0 random-obj) chance))

(defn choice
  "Uniformly chooses one of the arguments.
   Optionally takes a java.util.Random as the first argument. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [& items]
  (let [have-random? (instance? java.util.Random (first items))
        random-obj (when have-random? (first items))
        items (if have-random? (rest items) items)]
    (nth items (int (uniform 0 (count items) random-obj)))))

(defn weighted-choice
  "Given a sequence of alternating item and weight arguments, chooses one of the
   items with a probability equal to the weight. Each weight should be a
   positive value.
   Optionally takes a java.util.Random as the first argument. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [& items-and-weights]
  (let [have-random? (instance? java.util.Random (first items-and-weights))
        random-obj (when have-random? (first items-and-weights))
        items-and-weights (if have-random? (rest items-and-weights) items-and-weights)]
    (when (or (odd? (count items-and-weights))
              (< (count items-and-weights) 2))
      (throw (IllegalArgumentException. "weighted-choice expects a (non-zero) even number of arguments")))
    (let [total-weight (->> items-and-weights (rest) (take-nth 2) (reduce +))
          r (* (uniform 0.0 1.0 random-obj) total-weight)]
      (loop [items-and-weights items-and-weights
             cumulative-weight 0]
        (let [next-item (first items-and-weights)
              next-weight (second items-and-weights)
              new-cumulative-weight (+ cumulative-weight next-weight)]
          (if (>= new-cumulative-weight r)
            next-item
            (recur (drop 2 items-and-weights) new-cumulative-weight)))))))

(defn repeatable-shuffle
  "A version of shuffle that uses Processing's random fn to ensure that
   the same random seed is used.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [items & [random-obj]]
  (map second
       (sort (for [item items]
               [(uniform 0.0 1.0 random-obj) item]))))

(defn- vec-swap
  [v i j]
  (-> v
      (assoc i (v j))
      (assoc j (v i))))

(defn limited-shuffle
  "A limited shuffle that only executes n random swaps of the
   contents of `items`. The lower n is, the closer `items` will
   be to its original order.
   Optionally takes a java.util.Random as the RNG source. Defaults
   to using Quil's random functions (and thus, Processing's random functions) if
   this is not supplied."
  [n items & [random-obj]]
  (loop [altered-items (vec items)
         n n]
    (if (<= n 0)
      altered-items
      (let [i (int (uniform 0 (count items) random-obj))
            j (int (uniform 0 (count items) random-obj))]
        (recur (vec-swap altered-items i j) (dec n))))))
