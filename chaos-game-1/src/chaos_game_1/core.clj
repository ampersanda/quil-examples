(ns chaos-game-1.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))


(def coords (atom 0))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)

  (q/background 255)
  (q/stroke 0)
  (q/stroke-weight 8)

  (let [w       (q/width)
        h       (q/height)]
    (reset! coords
            {:ax (/ w 2)
             :ay 0
             :bx 0
             :by h
             :cx w
             :cy h

             :x  (q/random w)
             :y  (q/random h)})

    (q/point (@coords :ax) (@coords :ay))
    (q/point (@coords :bx) (@coords :by))
    (q/point (@coords :cx) (@coords :cy))))

(defn draw-state [state]
  (doseq [i (range 100)]
    (q/stroke 0)
    (q/stroke-weight 2)
    (q/point (@coords :x) (@coords :y))

    (let [r                               (int (Math/floor (q/random 3)))
          {:keys [x y ax ay bx by cx cy]} @coords
          amt                             0.5]
      (cond
        (= r 0) (do
                  (swap! coords assoc :x (q/lerp x ax amt))
                  (swap! coords assoc :y (q/lerp y ay amt)))
        (= r 1) (do
                  (swap! coords assoc :x (q/lerp x (@coords :bx) amt))
                  (swap! coords assoc :y (q/lerp y (@coords :by) amt)))
        (= r 2) (do
                  (swap! coords assoc :x (q/lerp x (@coords :cx) amt))
                  (swap! coords assoc :y (q/lerp y (@coords :cy) amt)))))))

(q/defsketch chaos-game-1
  :title "You spin my circle right round"
  :size [500 500]
  :setup setup
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
