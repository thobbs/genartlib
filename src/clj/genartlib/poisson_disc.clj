(ns genartlib.poisson-disc
  (:import [java.util Random]
           [genartlib PoissonDisc]))

(defn poisson-disc-sample
  "Returns a seq of points sampled from a Poisson disc set. The points have
   a minimum distance specified by the `spacing` parameter. The `max-attempts`
   parameter specifies how many attempts are made in each local area to place
   a new dot. A reasonable value is typically in the range of 8 to 16. The
   bounds of the area within which to generate the points must be specified.
   The final (optional) argument is a java.util.Random instance to use for
   generating random locations for potential points."
  ([spacing max-attempts left-x right-x top-y bot-y]
   (poisson-disc-sample spacing max-attempts left-x right-x top-y bot-y (Random.)))
  ([spacing max-attempts left-x right-x top-y bot-y random]
   (let [d (PoissonDisc. spacing left-x right-x top-y bot-y)
         points (.generate d max-attempts random)]
     (for [p points]
       [(.x p) (.y p)]))))
