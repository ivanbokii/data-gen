(ns data-gen.generators
  (:require [clojure.string :as str]))

(def string-chars "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")

(defn string [length]
  ;; todo ivanbokii optimize
  (let [number-of-chars (count string-chars)
        random-nums (take length (repeatedly #(rand-int number-of-chars)))]
    (apply str (map #(nth string-chars %) random-nums))))

(defn string-with-num-placeholder [string-with-placeholder max]
  (str/replace-first string-with-placeholder #"\*" (str (rand-int max))))

(defn number [max] (rand-int max))

(defn random-from-seq [values] (rand-nth values))


