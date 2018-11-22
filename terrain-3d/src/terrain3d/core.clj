(ns terrain3d.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def scale 20)
(def w 2400)
(def h 900)
(def randomness 0.16)
(def depth -64)
(def height 64)
(def walk-speed 0.2)

(def table (atom {:rows    nil
                  :columns nil
                  :terrain nil}))


(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)


  (let [cols (/ w scale)
        rows (/ h scale)]

    (swap! table assoc :rows rows)
    (swap! table assoc :columns cols)
    (swap! table assoc :terrain (vec (repeat cols (vec (repeat rows nil))))))

  {:walk 0})

(defn update-state [{:keys [walk]}]
  {:walk (- walk walk-speed)})

(defn draw-state [{:keys [walk]}]
  (q/background 0)

  (let [cols (:columns @table)
        rows (:rows @table)]

    (doseq [y (range rows)]
      (let [yoff (- (* y randomness) walk)]
        (doseq [x (range cols)]
          (let [xoff (* x randomness)]
            (swap! table assoc-in [:terrain x y] (q/map-range (q/noise xoff yoff) 0 1 depth height))))))

    (q/no-fill)
    (q/stroke 255)
    (q/with-translation
      [(/ w 2) (/ h 2)]

      (q/rotate-x (/ q/PI 3))
      (q/with-translation
        [(/ (- w) 1.6) (/ (- h) 2)]

        (doseq [y (range (dec rows))]
          (q/begin-shape :triangle-strip)

          (doseq [x (range cols)]
            (let [posx (* x scale)]
              (q/vertex posx (* y scale) (nth (nth (:terrain @table) x) y))
              (q/vertex posx (* (inc y) scale) (nth (nth (:terrain @table) x) (inc y)))))
          (q/end-shape))))))

(q/defsketch
  terrain3d
  :title "3d Terrain"
  :size [600 600]
  :setup setup
  :update update-state
  :draw draw-state
  :renderer :p3d
  :features [:keep-on-top]
  :middleware [m/fun-mode])
