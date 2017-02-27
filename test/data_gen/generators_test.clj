(ns data-gen.generators-test
  (:require [data-gen.generators :as g]
            [clojure.test :refer :all]))

(deftest string-generator
  (is (= (count (g/string 10)) 10))
  (is (not= (g/string 3) (g/string 3))))

(deftest string-within-range-generator []
  (is (<= 1 (count (g/string-within-range 1 10)) 10))
  (is (not= (g/string-within-range 1 3) (g/string-within-range 1 3))))

(deftest string-with-num-placeholder-generator
  (let [generated (g/string-with-num-placeholder "test-value-*" 10)]
    (is (= (.contains generated "*") false))))
