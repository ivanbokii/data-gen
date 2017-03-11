(ns data-gen.engine
  (:require [clojure.java.io :as io]))

;; default is one megabyte
(def ^:dynamic *file-check-size* (* 1024 1024))
(def ^:dynamic *rolling-check* 1000)
(def ^:dynamic *shutdown-execution* true)

(defn dec-and-get [atom] (swap! atom dec))

(defn check-file-size [file-path]
  (/ (.length (io/file file-path)) *file-check-size*))

(defn write-to-file-until-check [generator file-path]
  (let [records-counter (atom 0)]
    (with-open [writer (io/writer file-path :append true)]
      (while (< (swap! records-counter inc) *rolling-check*)
        (.append writer (str (generator)))
        (.newLine writer)))))

(defn generate-file
  [generator file-counter global-id file-size folder-path]
  (let [file-path (. (java.nio.file.Paths/get folder-path (into-array [(str "output-" global-id "-" file-counter)])) toString)]
    (write-to-file-until-check generator file-path)
    (when (< (check-file-size file-path) file-size) (recur generator file-counter global-id file-size folder-path))))

(defn run [record-generator files-counter file-size runner-id folder-path]
  (try
    (let [runner-files-counter (atom 0)]
      (while (> (dec-and-get files-counter) 0)
        (generate-file record-generator
                       (swap! runner-files-counter inc)
                       runner-id
                       file-size
                       folder-path)))
    (catch Exception e (println "thread worker" runner-id "failed with error:" e))))

(defn start [record-generator number-of-files file-size folder-path]
  (let [cores-number (.availableProcessors (Runtime/getRuntime))
        files-counter (atom (inc number-of-files))]
    (doseq [runner-id (range cores-number)]
      (future-call #(run record-generator files-counter file-size runner-id folder-path)))
    (when *shutdown-execution* (shutdown-agents))))
