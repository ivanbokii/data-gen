(ns data-gen.engine)

(def ROLLING_CHECK 1000)

(defn dec-and-get [atom] (swap! atom dec))

(defn generate-file
  [generator file-counter global-id check-on-records-number])

(defn run [files-counter runner-id record-generator]
  (let [runner-files-counter (atom 0)]
    (while (> (dec-and-get files-counter) 0)
      (generate-file record-generator
                     (swap! runner-files-counter inc)
                     runner-id
                     ROLLING_CHECK))))

(defn start [number-of-files file-size record-generator]
  (let [cores-number (.availableProcessors (Runtime/getRuntime))
        files-counter (atom number-of-files)]
    (println cores-number "available cores")
    (println "starting generator for" number-of-files "files each of size" file-size)))
