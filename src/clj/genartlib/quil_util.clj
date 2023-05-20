(ns genartlib.quil-util
  (:require [quil.core :as q]))

(defn load-image-sync
  "A synchronous alternative to the highly annoying `load-image` in Quil"
  [filename]
  (let [img (q/load-image filename)
        counter (atom 0)]
    (while (and (not (q/loaded? img))
                (< @counter 50))
      (swap! counter inc)
      (Thread/sleep 50))
    (if (= 50 @counter)
      (throw (Exception. (str "Timed out trying to load " filename " after 2500ms")))
      img)))
