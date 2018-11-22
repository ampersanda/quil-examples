(ns lissajous.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def circle-width 62.5)
(def rules (atom {:columns 0
                  :rows    0
                  :curves  nil}))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)

  (let [cols (dec (/ (q/width) circle-width))
        rows (dec (/ (q/height) circle-width))]

    (swap! rules assoc :columns cols)
    (swap! rules assoc :rows rows)
    (swap! rules assoc :curves (vec (repeat cols (vec (repeat rows {:x nil :y nil :points []}))))))

  {:angle 0})

(defn update-state [{:keys [angle]}]
  {:angle (+ angle 0.04)})

(defn create-circles-and-guides [angle mode]
  (let [hcw (/ circle-width 2)]
    (doseq [i (range (mode @rules))]
      (let [def-cx (+ (* i circle-width) hcw circle-width)
            cx (cond (= :columns mode) def-cx
                     (= :rows mode) hcw)

            cy (cond (= :columns mode) hcw
                     (= :rows mode) def-cx)

            diameter (- circle-width 10)
            r (/ diameter 2)

            angle-speed (* angle (inc i))
            angle-set-to-zero (- angle-speed q/HALF-PI)

            x (* r (q/cos angle-set-to-zero))
            y (* r (q/sin angle-set-to-zero))

            point-x (+ cx x)
            point-y (+ cy y)

            create-circle (fn []
                            (q/stroke-weight 1)
                            (q/ellipse cx cy diameter diameter))

            create-point (fn []
                           (q/stroke-weight 8)
                           (q/point point-x point-y))

            create-line (fn []
                          (q/stroke-weight 1)
                          (cond (= :columns mode) (q/line point-x 0 point-x (q/height))
                                (= :rows mode) (q/line 0 point-y (q/width) point-y)))]


        (create-circle)
        (create-point)
        (create-line)

        (cond
          (= :columns mode) (doseq [j (range (:rows @rules))] (swap! rules assoc-in [:curves j i :x] point-x))
          (= :rows mode) (doseq [j (range (:columns @rules))] (swap! rules assoc-in [:curves i j :y] point-y)))))))

(defn draw-state [{:keys [angle]}]
  (q/background 0)

  (q/no-fill)
  (q/stroke 255)

  (create-circles-and-guides angle :columns)
  (create-circles-and-guides angle :rows)

  (doseq [r (range (:rows @rules))
          c (range (:columns @rules))]
    (let [draw-lissajous (fn [points]
                           (q/stroke 255)
                           (q/stroke-weight 1)
                           (q/no-fill)
                           (q/begin-shape)

                           (doseq [x (range (count points))]
                             (let [p (nth points x)
                                   posx (:x p)
                                   posy (:y p)]
                               (q/vertex posx posy)))

                           (q/end-shape))

          current (nth (nth (:curves @rules) r) c)]
      (swap! rules update-in [:curves r c :points] conj {:x (:x current) :y (:y current)})
      (draw-lissajous (:points current)))))


(q/defsketch lissajous
             :title "Ampersanda - Lissajous"
             :size [500 500]
             :setup setup
             :update update-state
             :draw draw-state
             :features [:keep-on-top]
             :middleware [m/fun-mode])
