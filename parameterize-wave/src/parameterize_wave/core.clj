(ns parameterize-wave.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))


(def brick-width 40)
(def brick-height 15)
(def cols 20)
(def rows 24)
(def column-offset 60)
(def row-offset 30)
(def rotation-increment 0.15)

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)

  (q/background 0)
  (q/smooth)
  (q/no-fill)

  (q/stroke 143 87 101)

  (q/translate
    [30 30]

    (doseq [i (range cols)]
      (q/with-translation
        [(* i column-offset) 0]
        (let [r (atom (q/random (- q/QUARTER-PI) q/QUARTER-PI))
              dir (atom 1)]
          (doseq [j (range rows)]
            (q/with-translation
              [0 (* row-offset j)]

              (q/with-rotation
                [@r]
                (q/rect (/ (- brick-width) 2) (/ (- brick-height) 2) brick-width brick-height)))

            (swap! r #(+ % (* @dir rotation-increment)))
            (when (or (> @r q/QUARTER-PI) (< @r (- q/QUARTER-PI)))
              (swap! dir #(* % -1)))))))) {})



(q/defsketch
  parameterize-wave
  :title "Parameterize Wave"
  :size [600 600]
  :setup setup
  :features [:keep-on-top]
  :middleware [m/fun-mode])
