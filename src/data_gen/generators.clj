(ns data-gen.generators
  (:require [clojure.string :as str]
            [cheshire.core :as json]))

;; primitive generators
(def string-chars "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")

(defn string [length]
  ;; this should be optimized
  (let [number-of-chars (count string-chars)
        random-nums (take length (repeatedly #(rand-int number-of-chars)))]
    (apply str (map #(nth string-chars %) random-nums))))

(defn string-within-range [min max]
  (let [length (+ min (rand-int (+ 1 (- max min))))]
    (string length)))

(defn string-with-num-placeholder [string-with-placeholder max]
  (str/replace-first string-with-placeholder #"\*" (str (rand-int max))))

(defn number [max] (rand-int max))

(defn random-from-seq [values] (rand-nth values))

(def generators-mapping
  {:number number
   :string string
   :string-range string-within-range
   :placeholder string-with-num-placeholder
   :take-random random-from-seq})
;; --------------------------
;; transformers

(defn to-json [record] (json/generate-string record))

(defn to-csv [record]
  (let [vs (vals (into (sorted-map) record))]
    (str/join " " vs)))

(def transformers-mapping
  {:csv to-csv
   :json to-json})
;; --------------------------
(defn generate-record-based-on-definition
  ([definition transform-tag]
   ((transformers-mapping transform-tag) (generate-record-based-on-definition definition)))

  ([definition]
   (reduce
    (fn [result key-name]
      (let [generator-definition-data (key-name definition)
            generator-name (-> generator-definition-data first keyword)
            generator-params (rest generator-definition-data)]
        (assoc result key-name (apply (generators-mapping generator-name) generator-params))))
    {} (keys definition))))
