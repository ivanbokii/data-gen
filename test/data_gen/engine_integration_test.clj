(ns data-gen.engine-integration-test
  (:require [data-gen.engine :as engine]
            [clojure.test :refer :all]
            [clojure.java.io :as io]))

;; ------ utils ------
(def OUTPUT_DIR "./output")

(defn get-files-in-directory [directory]
  (->> (file-seq (io/file directory))
       (filter #(.isFile %))
       (map #(.getPath %))))

(defn get-first-file [directory]
  (first (get-files-in-directory directory)))

(defn delete-all-files [directory]
  (doseq [file (get-files-in-directory directory)] (io/delete-file file)))

(defn take-n-lines-from-file [path-to-file n]
  (with-open [reader (io/reader (.getPath (io/file path-to-file)))]
    (doall (take n (line-seq reader)))))

(defn wait [] (Thread/sleep 1000))

;; --------------------

(defn setup-test-output-folder [f]
  (io/make-parents (str OUTPUT_DIR "/output-file"))
  (f)
  (io/delete-file OUTPUT_DIR))

(defn cleanup-files [f]
  (f)
  (delete-all-files OUTPUT_DIR))

(use-fixtures :once setup-test-output-folder)
(use-fixtures :each cleanup-files)

(deftest generate-single-file []
  (binding [engine/*file-check-size* 1024
            engine/*rolling-check* 5
            engine/*shutdown-execution* false]
    (engine/start #(identity "this is test") 1 10 OUTPUT_DIR)
    (wait)
    (let [first-lines (take-n-lines-from-file (get-first-file OUTPUT_DIR) 10)]
      (is (every? #(= % "this is test") first-lines) true))))

(deftest generate-multiple-files []
  (binding [engine/*file-check-size* 1024
            engine/*rolling-check* 5]
    (engine/start #(identity "this is test") 2 10 OUTPUT_DIR)
    (wait)
    (let [[first second] (get-files-in-directory OUTPUT_DIR)
          first-lines-1 (take-n-lines-from-file first 10)
          first-lines-2 (take-n-lines-from-file second 10)]
      (is (every? #(= % "this is test") first-lines-1) true)
      (is (every? #(= % "this is test") first-lines-2) true)
      (is (= (count (get-files-in-directory OUTPUT_DIR)) 2)))))
