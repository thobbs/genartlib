(ns genartlib.capture
  (:require [clojure.java.io :refer [writer]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as s]
            [genartlib.util :refer [w h]]
            [quil.core :as q]))

(defmacro capture-helper
  [& body]
  `(let [full-width# (w 1.0)
         full-height# (h 1.0)
         commands# (atom [])
         current-fill# (atom nil)
         current-stroke# (atom nil)
         current-stroke-weight# (atom nil)
         current-shape# (atom nil)

         og-fill# q/fill
         og-no-fill# q/no-fill
         og-stroke# q/stroke
         og-no-stroke# q/no-stroke
         og-stroke-weight# q/stroke-weight
         og-begin-shape# q/begin-shape
         og-vertex# q/vertex
         og-curve-vertex# q/curve-vertex
         og-end-shape# q/end-shape
         og-line# q/line
         og-ellipse# q/ellipse
         og-background# q/background]

     (with-redefs [q/fill (fn [& color#]
                            (reset! current-fill# color#)
                            (apply og-fill# color#))
                   q/no-fill (fn []
                               (reset! current-fill# nil)
                               (og-no-fill#))
                   q/stroke (fn [& color#]
                              (reset! current-stroke# color#)
                              (apply og-stroke# color#))
                   q/no-stroke (fn []
                                 (reset! current-stroke# nil)
                                 (og-no-stroke#))
                   q/stroke-weight (fn [s#]
                                     (reset! current-stroke-weight# (/ s# full-width#))
                                     (og-stroke-weight# s#))
                   q/begin-shape (fn []
                                   (reset! current-shape# [])
                                   (og-begin-shape#))
                   q/vertex (fn [x# y#]
                              (swap! current-shape# conj {:type :vertex
                                                          :x (/ x# full-width#)
                                                          :y (/ y# full-height#)})
                              (og-vertex# x# y#))
                   q/curve-vertex (fn [x# y#]
                                    (swap! current-shape# conj {:type :curve-vertex
                                                                :x (/ x# full-width#)
                                                                :y (/ y# full-height#)})
                                    (og-curve-vertex# x# y#))
                   q/end-shape (fn [& [close#]]
                                 (swap!
                                   commands#
                                   conj
                                   {:type :shape
                                    :close? (boolean close#)
                                    :points @current-shape#
                                    :fill @current-fill#
                                    :stroke @current-stroke#
                                    :stroke-weight @current-stroke-weight#})
                                 (if close#
                                   (og-end-shape# close#)
                                   (og-end-shape#)))
                   q/line (fn [start-x# start-y# end-x# end-y#]
                            (swap!
                              commands#
                              conj
                              {:type :line
                               :start-x (/ start-x# full-width#)
                               :start-y (/ start-y# full-height#)
                               :end-x (/ end-x# full-width#)
                               :end-y (/ end-y# full-height#)
                               :stroke @current-stroke#
                               :stroke-weight @current-stroke-weight#})
                            (og-line# start-x# start-y# end-x# end-y#))
                   q/ellipse (fn [center-x# center-y# ellipse-width# ellipse-height#]
                               (swap!
                                 commands#
                                 conj
                                 {:type :ellipse
                                  :center-x (/ center-x# full-width#)
                                  :center-y (/ center-y# full-height#)
                                  :ellipse-width (/ ellipse-width# full-width#)
                                  :ellipse-height (/ ellipse-height# full-height#)
                                  :fill @current-fill#
                                  :stroke @current-stroke#
                                  :stroke-weight @current-stroke-weight#})
                               (og-ellipse# center-x# center-y# ellipse-width# ellipse-height#))
                   q/background (fn [& color#]
                                  (reset! commands# [{:type :background
                                                      :color color#}])
                                  (apply og-background# color#))]
       ~@body)
     @commands#))

(defn with-command-capture
  [output-file & body]
  `(let [commands# (capture-helper ~@body)]
     (println "Going to save commands to" ~output-file)
     (pprint commands# (clojure.java.io/writer ~output-file))))

(def svg-opening
  "<svg xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"%s\" height=\"%s\" viewBox=\"0 0 %d %d\">\n  <g fill=\"none\" stroke=\"#000000\" stroke-width=\"1\">\n")

(defn make-path
  [points]
  (->> points
       (map #(s/join "," %))
       (s/join " ")
       (format "    <path d=\"M %s\"/>\n")))

(defmacro with-plotter-svg-capture
  [output-file width height & body]
  `(let [commands# (capture-helper ~@body)]
     (println "Going to write SVG at" ~output-file)
     (with-open [wrt# (writer ~output-file)]
       (.write wrt# (format svg-opening ~width ~height (int (w)) (int (h))))
       (doseq [cmd# commands#]
         (try
           (case (:type cmd#)
             :line
             (when (:stroke cmd#)
               (.write wrt# (make-path [[(w (:start-x cmd#)) (h (:start-y cmd#))]
                                        [(w (:end-x cmd#)) (h (:end-y cmd#))]])))

             :shape
             (when (:stroke cmd#)
               (.write wrt# (make-path (for [p# (:points cmd#)]
                                         [(w (:x p#)) (h (:y p#))]))))

             ; else
             nil)))

       (.write wrt# "  </g>\n</svg>\n"))))

(defmacro with-plotter-svg-capture-axidraw-workaround
  "A temporary workaround for an axidraw bug that prevents pausing the plotter
   in the middle of a multi-segment curve"
  [output-file width height & body]
  `(let [commands# (capture-helper ~@body)]
     (println "Going to write SVG at" ~output-file)
     (with-open [wrt# (writer ~output-file)]
       (.write wrt# (format svg-opening ~width ~height (int (w)) (int (h))))
       (doseq [cmd# commands#]
         (try
           (case (:type cmd#)
             :line
             (when (:stroke cmd#)
               (.write wrt# (make-path [[(w (:start-x cmd#)) (h (:start-y cmd#))]
                                        [(w (:end-x cmd#)) (h (:end-y cmd#))]])))

             :shape
             (when (:stroke cmd#)
               (doseq [[p1# p2#] (partition 2 1 (:points cmd#))]
                 (.write wrt# (make-path [[(w (:x p1#)) (h (:y p1#))]
                                          [(w (:x p2#)) (h (:y p2#))]]))))

             ; else
             nil)))

       (.write wrt# "  </g>\n</svg>\n"))))

(defn command-replay
  ([cmds] (command-replay cmds (w) (h) 0 0))
  ([cmds full-width full-height] (command-replay cmds full-width full-height 0 0))
  ([cmds full-width full-height x-offset y-offset]
   (let [wlen (fn [x]
                (* full-width x))
         hlen (fn [y]
                (* full-height y))
         wpos (fn [x]
                (+ x-offset (* full-width x)))
         hpos (fn [y]
                (+ y-offset (* full-height y)))]
     (doseq [cmd cmds]
       (try
         (case (:type cmd)
           :background
           (apply q/background (:color cmd))

           :line
           (when (:stroke cmd)
             (apply q/stroke (:stroke cmd))
             (q/stroke-weight (wlen (:stroke-weight cmd)))
             (q/line (wpos (:start-x cmd)) (hpos (:start-y cmd))
                     (wpos (:end-x cmd)) (hpos (:end-y cmd))))

           :ellipse
           (do
             (if (:stroke cmd)
               (do
                 (apply q/stroke (:stroke cmd))
                 (q/stroke-weight (wlen (:stroke-weight cmd))))
               (q/no-stroke))
             (if (:fill cmd)
               (apply q/fill (:fill cmd))
               (q/no-fill))
             (q/ellipse (wpos (:center-x cmd)) (hpos (:center-y cmd))
                        (wlen (:ellipse-width cmd)) (hlen (:ellipse-width cmd))))

           :shape
           (do
             (if (:stroke cmd)
               (do
                 (apply q/stroke (:stroke cmd))
                 (q/stroke-weight (wlen (:stroke-weight cmd))))
               (q/no-stroke))
             (if (:fill cmd)
               (apply q/fill (:fill cmd))
               (q/no-fill))
             (q/begin-shape)
             (doseq [p (:points cmd)]
               (case (:type p)
                 :vertex
                 (q/vertex (wpos (:x p)) (hpos (:y p)))

                 :curve-vertex
                 (q/curve-vertex (wpos (:x p)) (hpos (:y p)))))

             (if (:close? cmd)
               (q/end-shape :close)
               (q/end-shape)))

           (println "Got bad command type:" (:type cmd)))
         (catch Exception exc
           (println "Error executing cmd:" exc)
           (println exc)))))))

(defn command-replay-file
  ([cmd-filename] (command-replay-file cmd-filename (w) (h) 0 0))
  ([cmd-filename full-width full-height] (command-replay-file cmd-filename full-width full-height 0 0))
  ([cmd-filename full-width full-height x-offset y-offset]
   (-> cmd-filename slurp read-string (command-replay full-width full-height x-offset y-offset))))
