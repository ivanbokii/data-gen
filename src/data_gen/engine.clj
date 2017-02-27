(ns data-gen.engine
  (:require [clojure.java.io :as io]))

(def ROLLING_CHECK 1000)

(defn dec-and-get [atom] (swap! atom dec))

(defn file-size-in-mb [file-path]
  (/ (.length (io/file file-path)) (* 1024 1024)))

(defn file-size-in-kb [file-path]
  (/ (.length (io/file file-path)) 1024))

(defn write-to-file-until-check [generator file-path]
  (let [records-counter (atom 0)]
    (with-open [writer (io/writer file-path :append true)]
      (while (< (swap! records-counter inc) ROLLING_CHECK)
        (.append writer (str (generator)))
        (.newLine writer)))))

(defn generate-file
  [generator file-counter global-id file-size folder-path]
  (let [file-path (. (java.nio.file.Paths/get folder-path (into-array [(str "output-" global-id "-" file-counter)])) toString)]
    (write-to-file-until-check generator file-path)
    (when (< (file-size-in-mb file-path) file-size) (recur generator file-counter global-id file-size folder-path))))

(defn run [record-generator files-counter file-size runner-id folder-path]
  (try
    (let [runner-files-counter (atom 0)]
      (while (> (dec-and-get files-counter) 0)
        (generate-file record-generator
                       (swap! runner-files-counter inc)
                       runner-id
                       file-size
                       folder-path)))
    (catch Exception e (println "thread worker" runner-id "failed with error:" (.getMessage e)))))

(defn start [record-generator number-of-files file-size folder-path]
  (let [cores-number (.availableProcessors (Runtime/getRuntime))
        files-counter (atom (inc number-of-files))]
    (doseq [runner-id (range cores-number)]
      (future-call #(run record-generator files-counter file-size runner-id folder-path)))
    (shutdown-agents)))
