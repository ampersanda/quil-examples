(ns terrain3d.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def scale 20)
(def w 2400)
(def h 900)
(def randomness 0.16)
(def depth -100)
(def height 100)
(def walk-speed 0.1)

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :rgb)
  {:walk 0})

(defn update-state [{:keys [walk] :as state}]
  {:walk (- walk walk-speed)})

(defn draw-state [{:keys [walk] :as keys}]
  (q/background 0)

  (let [rows (/ h scale)
        cols (/ w scale)
        terrain (to-array-2d (repeat cols (repeat rows nil)))]

    (doseq [y (range rows)]
      (let [yoff (- (* y randomness) walk)]
        (doseq [x (range cols)]
          (let [xoff (* x randomness)]
            (aset terrain x y (q/map-range (q/noise xoff yoff) 0 1 depth height))))))

    (q/no-fill)
    (q/stroke 255)
    (q/with-translation [(/ w 2) (/ h 2)]
      (q/rotate-x (/ q/PI 3))
      (q/with-translation [(/ (- w) 1.6) (/ (- h) 2)]

        (doseq [y (range (dec rows))]
          (q/begin-shape :triangle-strip)

          (doseq [x (range cols)]
            (q/vertex (* x scale) (* y scale) (aget terrain x y))
            (q/vertex (* x scale)  (* (inc y) scale) (aget terrain x (inc y))))
          (q/end-shape))))))

(q/defsketch terrain3d
  :title "3d Terrain"
  :size [600 600]
  :setup setup
  :update update-state
  :draw draw-state
  :renderer :p3d
  :features [:keep-on-top]
  :middleware [m/fun-mode])
