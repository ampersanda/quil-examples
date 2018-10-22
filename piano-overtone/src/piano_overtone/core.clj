(ns piano-overtone.core
  (:use overtone.live
        overtone.inst.piano)
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def t-width 32)
(def t-height 112)

(def white-notes '(60 62 64 65 67 69 71 72))

(defn white-tut [x]
  (q/fill 255)

  (let [w (* x t-width)]
    (q/rect w 0 t-width t-height)))

(defn black-tut [x]
  (q/fill 0)
  (let [scaled-t-width (* t-width 0.5)]
    (q/rect (+ (* x t-width) t-width (- (/ scaled-t-width 2))) 0 scaled-t-width (/ t-height 2))))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :rgb))

(defn mouse-pressed [state event]
  (q/no-loop)
  ; (prn (nth white-notes (dec (Math/ceil (/ (q/mouse-x) t-width)))))
  (piano (nth white-notes (dec (Math/ceil (/ (q/mouse-x) t-width))))))

(defn draw-state [state]
  (q/background 150)

  ;; white keys
  (doseq [x (range 8)]
    (white-tut x))

  ;; black keys
  (doseq [x (range 6)]
    (if (not (= 3 (inc x))) (black-tut x))))


(q/defsketch piano-overtone
  :title "Ampersando"
  :size [256 112]
  :setup setup
  :mouse-pressed mouse-pressed

  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
