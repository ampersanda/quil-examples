(ns tenprint.core
  (:require [quil.core :as q]
            [quil.middleware :as m])
  (:gen-class))


(defn setup []
  (q/background 0)
  (q/frame-rate 300)
  (q/color-mode :hsb)
  {:x 0
   :y 0
   :scale 20
   :once false})

(defn update-state [{:keys [x y scale once] :as state}]
  {:x (if (>= x (q/width)) 0 (+ x scale))
   :y (if (>= x (q/width)) (+ y scale) y)
   :scale scale
   :once once})

(defn draw-state [{:keys [x y scale once] :as state}]
  (q/stroke (* 255 (rand)) (* 255 (rand)) (* 255 (rand)))

  (if (true? once)
    (do
      (q/no-stroke)
      (q/fill 0)
      (q/rect 0 y (q/width) scale)))

  (if (>= y  (q/height))
    (swap! (q/state-atom) assoc :y 0 :once (not once))
    (if (> (rand) 0.5)
      (q/line x y (+ x scale) (+ y scale))
      (q/line x (+ y scale) (+ x scale) y))))

(defn -main [& args]
  (q/defsketch tenprint
               :title "10PRINT"
               :size [500 500]
               :setup setup
               :update update-state
               :draw draw-state
               :settings #(q/smooth 2)
               :features [:keep-on-top]
               :middleware [m/fun-mode]))