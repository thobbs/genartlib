(ns sketch.dynamic
  (:require [clojure.java.shell :refer [sh]]
            ; [genartlib.algebra :refer :all]
            ; [genartlib.curves :refer :all]
            ; [genartlib.geometry :refer :all]
            ; [genartlib.random :refer :all]
            [genartlib.util :refer [set-color-mode w h]]
            [quil.core :as q])
  ; you can import Java classes like so:
  ; (:import [sketch Example]))
  )

; the setup function is only called once by Processing during startup
(defn setup
  []
  (q/smooth)
  ; avoid some saving issues
  (q/hint :disable-async-saveframe)

  ; create a directory for storing versioned code
  ; This uses "bash -c" to support my WSL setup, but if on Linux or Mac
  ; you could just call mkdir directly
  (sh "bash" "-c" "mkdir versioned-code"))

(declare actual-draw)

; the draw function will be called every time we refresh (i.e. reload the code
; and re-execute it)
(defn draw
  []
  ; disable animation, just draw one frame
  (q/no-loop)

  ; set color space to HSB with hue in [0, 360], saturation in [0, 100],
  ; brightness in [0, 100], and alpha in [0.0, 1.0]
  (set-color-mode)

  ; make it easy to generate multiple images
  (doseq [img-num (range 1)]
    (let [cur-time (System/currentTimeMillis)
          ; Grab the current time in nanos to use for a seed. This guarantees
          ; that we get a new seed every run, and it also increases every time
          ; (unless we restart the process)
          seed (System/nanoTime)]

      ; only save the versioned code the first time in this loop (because the code
      ; will never change inside of this loop)
      (when (zero? img-num)
        (let [code-dirname (str "versioned-code/" seed)]
          ; only copy source files, to avoid duplicating temporary files opened
          ; by vim, etc. Also zip them, since they will rarely be read, compression
          ; ratio is good, and the number of total files will stay lower
          (sh "bash" "-c" (str "zip -r " code-dirname " src -i '*.clj' '*.java'"))))

      (println "setting seed to:" seed)
      (q/random-seed seed) ; used by Processing for most random functions
      (q/noise-seed seed) ; used by Processing for perlin noise

      (try
        (actual-draw)
        (catch Throwable t
          (println "Exception in draw function:" t)))

      (println "gen time:" (/ (- (System/currentTimeMillis) cur-time) 1000.0) "s")
      (let [img-filename (str "img-" img-num "-" cur-time "-" seed ".tif")]
        (q/save img-filename)
        (println "done saving" img-filename)

        ; Some part of image saving appears to be async on Windows. This is lame, but
        ; for now, add a sleep to help avoid compressing partially-written files.
        (Thread/sleep 500)

        ; The 'convert' command comes from ImageMagick. By default, processing will save
        ; un-compressed tif files, which tend to be quite large. This applies LZW compression,
        ; which is lossless and reasonably compact. This command needs to be available on the
        ; command line for this to be successful.
        (let [convert-cmd (str "convert -compress lzw " img-filename " " img-filename)
              results (sh "bash" "-c" convert-cmd)]
          (if (zero? (:exit results))
            (println "done compressing")
            (println "WARNING: compression failed."
                     "Command was: bash -c '" convert-cmd "'; err:" (:err results))))))))

(defn actual-draw
  []
  ; Art goes here. For example:
  (q/background 40 2 98) ; off-white background
  (q/stroke 0 0 5) ; line color is nearly black
  (q/stroke-weight (w 0.001)) ; line thickness is 0.1% of the image width

  ; draw a horizontal line
  (q/line (w 0.1) (h 0.5) (w 0.9) (h 0.5)))
