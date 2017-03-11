(ns data-gen.generators-test
  (:require [data-gen.generators :as g]
            [clojure.test :refer :all]
            [clojure.string :as str]))

;; ------ generators -------

(deftest string-generator-test
  (is (= (count (g/string 1000)) 1000))
  (is (not= (g/string 3) (g/string 3))))

(deftest string-within-range-generator-test []
  (is (<= 1 (count (g/string-within-range 1 10)) 10))
  (is (not= (g/string-within-range 1 3) (g/string-within-range 1 3))))

(deftest string-with-num-placeholder-generator-test
  (let [generated (g/string-with-num-placeholder "test-value-*" 10)]
    (is (= (.contains generated "*") false))))

(deftest number-generator-test
  (is (not= (g/number 1000) (g/number 1000))))

(deftest random-from-seq-generator-test
  (let [input-seq [1 2 3 4 5]
        picked-item (g/random-from-seq input-seq)]
    (is (= (some #{picked-item} input-seq) picked-item))))

;; --------------------------
;; ------ transformer -------

(deftest to-json-transformer-test
  (is (= (g/to-json {:hello "test"}) "{\"hello\":\"test\"}")))

(deftest to-csv-transformer-test
  (is (= (g/to-csv {:second "the-second" :first "the-first"}) "the-first the-second")))

;; ------- map generators based on defintion -------

(deftest generate-record-based-on-definition-test
  (let [definition {:number ["number", 100],
                    :string ["string", 3],
                    :string-with-range ["string-range", 3, 8],
                    :string-with-placeholder ["placeholder", "hello-*-test", 100],
                    :random-from-collection ["take-random", ["first", "second", "third", "fourth"]]}
        generated-value (g/generate-record-based-on-definition definition)]
    (is (= (keys generated-value) [:number :string :string-with-range :string-with-placeholder :random-from-collection]))
    (is (= (.getClass (:number generated-value)) java.lang.Integer))
    (is (= (distinct (map #(.getClass %) [(:string generated-value) (:string-with-range generated-value) (:string-with-placeholder generated-value)])) [java.lang.String]))
    (is (= (some #{(:random-from-collection generated-value)} ["first", "second", "third", "fourth"]) (:random-from-collection generated-value)))))

(deftest generate-record-based-on-definition-with-transofrmer-test
  (let [definition {:number ["number", 100],
                    :string ["string", 3],
                    :string-with-range ["string-range", 3, 8],
                    :string-with-placeholder ["placeholder", "hello-*-test", 100],
                    :random-from-collection ["take-random", ["first", "second", "third", "fourth"]]}
        generated-value (g/generate-record-based-on-definition definition :csv)]
    (is (= (count (str/split generated-value #" ")) 5))))
