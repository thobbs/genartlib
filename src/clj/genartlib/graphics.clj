(ns genartlib.graphics
  (:require [quil.core :as q]))

(defn copy-to
  "Copies the pixel contents of one PGraphics to another PGraphics. Automatically
   calls `update-pixels` on the destination when complete."
  [graphics-1 graphics-2]
  (let [pix1 (q/pixels graphics-1)
        pix2 (q/pixels graphics-2)]
    (System/arraycopy pix1 0 pix2 0 (alength pix1))
    (q/update-pixels graphics-2)))

(defn load-image-sync
  "Load the specified filename as a PImage synchronously. This is a workaround
   for Processing/quil 4.x only providing an annoying async version of file loading."
  [filename]
  (let [img (q/load-image filename)]
    (while (not (q/loaded? img))
      (Thread/sleep 10))
    img))
