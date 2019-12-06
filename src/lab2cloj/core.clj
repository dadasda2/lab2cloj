(ns lab2cloj.core
  (:require [promesa.core :as p])
  (:require [clojure.core.async :as a]))

(defn urn [n]
  (take n (repeatedly #(rand-int n))))

(def c1 (a/chan 10))
(a/onto-chan c1 (urn 10))
(def c2 (a/chan 10))

(defn foo! [c1 c2 n]
  (a/go-loop [prev 0]
    (when-some [value (a/<! c1)]
      (println "value " value)
      (println "diff " (- value prev))

      (when (< n (- value prev))
        (a/>! c2 value)
        )
      (recur value)
      )
    )
  )

(foo! c1 c2 2)

(defn consumer [c]
  (Thread/sleep 1000)
  (println c "'s insides")
  (a/go-loop []
    (when-some [value (a/<! c)]
      (println "received " value)
      (recur))))

(consumer c1)
(consumer c2)

