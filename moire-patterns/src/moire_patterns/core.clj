(ns moire-patterns.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def triangle
  {:width  20
   :height 20})

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {:angle 0})

(defn update-state [state]
  (let [{:keys [angle]} state]
    {:angle (mod (+ angle 0.02) q/TWO-PI)}))

(defn draw-state [{:keys [angle]}]
  (q/background 0)
  (q/stroke 255)

  (let [{:keys [width height]} triangle]
    (doseq [x (range (/ (q/width) width))
            y (range (/ (q/height) height))]
      (let [posx (* x width)]
        (q/begin-shape :triangles)
        (q/vertex posx (* (inc y) height))
        (q/vertex (+ posx (/ width 2)) (* y height))
        (q/vertex (* (inc x) width) (* (inc y) height))
        (q/end-shape))))

  (q/translate (/ (q/width) 2) (/ (q/height) 2))
  (q/with-rotation [angle]
                   (let [{:keys [width height]} triangle]
                     (doseq [x (range (/ (q/width) width))
                             y (range (/ (q/height) height))]
                       (let [hw   (/ (q/width) 2)
                             hy   (/ (q/height) 2)
                             posx (* x width)]
                         (q/begin-shape :triangles)
                         (q/vertex (- hw posx) (- hy (* (inc y) height)))
                         (q/vertex (- hw (+ posx (/ width 2))) (- hy (* y height)))
                         (q/vertex (- hw (* (inc x) width)) (- hy (* (inc y) height)))
                         (q/end-shape))))))


(q/defsketch moire-patterns
  :title "You spin my circle right round"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
