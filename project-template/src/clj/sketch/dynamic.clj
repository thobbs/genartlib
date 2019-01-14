(ns sketch.dynamic
  (:require [clojure.java.shell :refer [sh]]
            [clojure.pprint :refer [pprint]]
            [genartlib.algebra :refer :all]
            [genartlib.curves :refer :all]
            [genartlib.geometry :refer :all]
            [genartlib.random :refer :all]
            [genartlib.util :refer :all]
            [quil.applet :refer [current-applet]]
            [quil.core :refer :all])
  (:import [sketch Example]
           [com.seisw.util.geom PolySimple PolyDefault Clip Point2D]))

(defn setup []
  (smooth)
  ; avoid some saving issues
  (hint :disable-async-saveframe))

(declare actual-draw)

(defn draw []
  ; disable animation, just one frame
  (no-loop)

  ; set color space to HSB with hue in [0, 360], saturation in [0, 100],
  ; brightness in [0, 100], and alpha in [0.0, 1.0]
  (set-color-mode)

  ; make it easy to generate multiple images
  (doseq [img-num (range 1)]
    (let [cur-time (System/currentTimeMillis)
          seed (System/nanoTime)]

      ; use "bash -c" to support my WSL setup
      (sh "bash" "-c" "mkdir versioned-code")
      (let [code-dirname (str "versioned-code/" seed)]
        (sh "bash" "-c" (str "cp -R src/ " code-dirname)))

      (println "setting seed to:" seed)
      (random-seed seed)

      (try
        (actual-draw)
        (catch Throwable t
          (println "Exception in draw function:" t)))

      (println "gen time:" (/ (- (System/currentTimeMillis) cur-time) 1000.0) "s")
      (let [img-filename (str "img-" img-num "-" cur-time "-" seed ".tif")]
        (save img-filename)
        (println "done saving" img-filename)

        ; Some part of image saving appears to be async on windows. This is lame, but
        ; for now, add a sleep to help avoid compressing partially-written files.
        (Thread/sleep 500)
        (sh "bash" "-c" (str "convert -compress lzw " img-filename " " img-filename))
        (println "done compressing")))))

(defn actual-draw []
  ; art goes here
  )
