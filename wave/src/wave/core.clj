(ns wave.core
  (:require [quil.core :as q]
            [quil.middleware :as m])
  (:gen-class))


(defn setup []
  (q/smooth)
  (q/frame-rate 60)
  (q/color-mode :rgb)
  (q/stroke-weight 5)
  {:theta 0})

(defn update-state [{:keys [theta] :as state}]
  {:theta (+ 0.0523 theta)})

(defn draw-state [{:keys [theta] :as state}]
  (q/background 20)
  (let [w (q/width)
        h (q/height)
        count 20
        step 22]
    (q/with-translation [(/ w 2) (* h 0.75)]
      (doseq [x (range count)]
        (let [offset (* (/ q/TWO-PI count) x)
              size (* x step)
              arc-end (q/map-range (q/sin (+ theta offset)) -1, 1, q/PI, q/TWO-PI)]
          (q/stroke 255)
          (q/no-fill)
          (q/arc 0 0 size size q/PI arc-end))) (q/reset-matrix))))

(defn -main [& args]
  (q/defsketch wave
               :title "Wave"
               :size [600 400]
               :setup setup
               :update update-state
               :draw draw-state
               :features [:keep-on-top]
               :middleware [m/fun-mode]))
