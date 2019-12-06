(ns pi-digit-collision.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;; x is horizontal position of the block
;; y is vertical position of the block
;; w is width of the block
;; m is mass/weight of the block
;; v is velocity of the block
(defn block [x w v m color]
  {:x     x
   :y     (- (q/height) w)
   :w     w
   :m     m
   :v     v
   :color color})

(def first-block-weight 20)
(def exponent 4)

(defn draw-block [{:keys [x y w v color]}]
  (q/no-stroke)
  (apply q/fill color)
  (q/rect x y w w))

(defn is-colliding? [block other-block]
  (let [x1         (:x block)
        w1         (:w block)
        x2         (:x other-block)
        w2         (:w other-block)
        colliding? (not (or (< (+ x1 w1) x2) (> x1 (+ x2 w2))))]
    colliding?))

(defn is-hit-wall? [block]
  (<= (:x block) 0))

;; ellastic collision, https://en.wikipedia.org/wiki/Elastic_collision
(defn elastic-collision [block other-block]
  (let [blocks     [block other-block]
        v1         (:v block)
        v2         (:v other-block)
        m2         (:m other-block)
        total-mass (apply + (map :m blocks))
        diff-mass  (apply - (map :m blocks))]
    (+ (* (/ diff-mass total-mass) v1) (* (/ (* 2 m2) total-mass) v2))))

(def states (atom nil))

(defn update-block [{:keys [x y w v] :as block}]
  (assoc block :x (+ x v)))

(defn setup []
  (q/frame-rate 5)
  (q/color-mode :hsb)
  (let [euler-hack (q/pow 10 (dec exponent))]
    (reset! states
            {:blocks  [(block 100 20 0 first-block-weight [255 100 160])
                       (block 200 100 (/ -5 euler-hack) (* first-block-weight (q/pow 100 (dec exponent))) [100 100 160])]
             :counter 0})))

(defn update-state []
  (let [{:keys [blocks counter]} @states
        new-blocks               (if (apply is-colliding? blocks)
                                   (let [new-velocities [(apply elastic-collision blocks) (apply elastic-collision (reverse blocks))]]
                                     (map #(assoc % :v %2) blocks new-velocities))
                                   blocks)
        new-counter              (if (apply is-colliding? blocks)
                                   (inc counter)
                                   counter)

        collide-wall-blocks      (map #(if (is-hit-wall? %) (update % :v -) %) new-blocks)
        collide-wall-counter     (apply + (map #(if (is-hit-wall? %) 1 0) new-blocks))]

    (swap! states assoc :blocks (map #(update-block %) collide-wall-blocks))
    (swap! states assoc :counter (+ new-counter collide-wall-counter))))

(defn draw-state [state]
  (let [{:keys [blocks counter]} @states
        euler-hack               (q/pow 10 (dec exponent))]
    (q/background 240)

    (doseq [i (range euler-hack)]
      (update-state))

    (doseq [block blocks]
      (draw-block block))

    (q/fill 0)
    (q/text-size 24)
    (q/text (str counter) 16 24)))


(q/defsketch pi-digit-collision
  :title "PI Digit Collision"
  :size [500 500]
  :setup setup
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
