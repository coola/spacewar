(ns spacewar.ui.strategic-scan
  (:require [quil.core :as q]
            [spacewar.ui.config :refer :all]
            [spacewar.game-logic.config :refer :all]
            [spacewar.ui.protocols :as p]))

(defn- draw-background [state]
  (let [{:keys [w h]} state]
    (q/fill 0 0 0)
    (q/rect-mode :corner)
    (q/rect 0 0 w h)))

(defn- draw-grid [state]
  (let [{:keys [w h]} state
        rows (second known-space-sectors)
        columns (first known-space-sectors)
        column-width (/ w columns)
        row-height (/ h rows)]
    (q/stroke-weight 1)
    (q/stroke 255 255 255)
    (doseq [col (range 1 columns)]
      (let [cx (* col column-width)]
        (q/line cx 0 cx h)))
    (doseq [row (range 1 rows)]
      (let [ry (* row row-height)]
        (q/line 0 ry w ry)))))

(defn- draw-stars [state]
  (let [{:keys [w h stars]} state
        x-pixel-width (/ w known-space-x)
        y-pixel-width (/ h known-space-y)]
    (when stars
      (apply q/fill grey)
      (q/no-stroke)
      (q/ellipse-mode :center)
      (doseq [{:keys [x y]} stars]
        (q/ellipse (* x x-pixel-width) (* y y-pixel-width) 4 4)))
    )
  )

(defn- draw-klingons [state]
  (let [{:keys [w h klingons]} state
        x-pixel-width (/ w known-space-x)
        y-pixel-width (/ h known-space-y)]
    (when klingons
      (q/no-fill)
      (apply q/stroke klingon-red)
      (q/stroke-weight 2)
      (q/ellipse-mode :center)
      (doseq [{:keys [x y]} klingons]
        (q/with-translation
          [(* x x-pixel-width)
           (* y y-pixel-width)]
          (q/ellipse 0 0 6 6)
          (q/line 0 0 10 -6)
          (q/line 10 -6 14 -3)
          (q/line 0 0 -10 -6)
          (q/line -10 -6 -14 -3))))
    )
  )

(deftype strategic-scan [state]
  p/Drawable
  (draw [_]
    (let [{:keys [x y]} state]
      (q/with-translation
        [x y]
        (draw-background state)
        (draw-grid state)
        (draw-stars state)
        (draw-klingons state))))

  (setup [this] this)

  (update-state [_ {:keys [global-state]}]
    (p/pack-update
      (strategic-scan.
        (assoc state :stars (:stars global-state)
                     :klingons (:klingons global-state))))))