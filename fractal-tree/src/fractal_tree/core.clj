(ns fractal-tree.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def branch-length 120)

(defn branch [length angle]
  (q/line 0 0 0 (- length))
  (q/translate 0 (- length))

  (prn (str length))

  (when (> length 4)
    (do
      (q/with-rotation [angle]
        (branch (* length 0.67) angle))
      (q/with-rotation [(- angle)]
        (branch (* length 0.67) angle)))))

(defn setup []
  (q/frame-rate 1)
  (q/color-mode :rgb)
  {:angle (/ q/PI 4)})

(defn update-state [{:keys [angle] :as state}]
  {:angle (- angle (* q/PI 0.11))})

(defn draw-state [{:keys [angle] :as state}]
  (q/background 0)

  (q/stroke 255)

  (q/translate (/ (q/width) 2) (q/height))
  (branch branch-length angle))


(q/defsketch fractal-tree
  :title "Fractal Tree"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
