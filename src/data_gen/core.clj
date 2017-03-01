(ns data-gen.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [cheshire.core :as json]
            [data-gen.generators :as generators]
            [data-gen.engine :as engine])
  (:gen-class))

;; todo provide defaults for everything you can

(def cli-options
  [
   ["-f" "--definitions-file DEFINITIONS" "file with definitions for generator"]
   ["-d" "--definition-name DEFINITION" "name of the definition to generate"
    :parse-fn #(keyword %)]
   ["-n" "--number-of-files NUMBER-OF-FILES" "number of output files"
    :parse-fn #(Integer/parseInt %)]
   ["-s" "--file-size FILE-SIZE" "number of output files in MBs"
    :parse-fn #(Integer/parseInt %)]
   ["-o" "--output-format FORMAT" "output format of a single record. json or csv"
    :parse-fn keyword]
   ["-u" "--output-folder FOLDER-PATH" "folder to which app saves output files"]
   ["-h" "--help"]])

(defn load-definitions-from-file [path-to-definitions-file]
  (-> path-to-definitions-file slurp (json/parse-string true)))

(defn make-generator-and-run-engine [params]
  (let [selected-definition ((:definition-name params) (load-definitions-from-file (:definitions-file params)))
        record-generator (partial generators/generate-record-based-on-definition selected-definition (:output-format params))]
    ((:engine params) record-generator (:number-of-files params) (:file-size params) (:output-folder params))))

(defn -main
  [& args]
  (let [{:keys [definition-name definitions-file number-of-files file-size output-format output-folder]} (:options (parse-opts args cli-options))]
    (make-generator-and-run-engine {:definitions-file definitions-file
                                    :definition-name definition-name
                                    :output-folder output-folder
                                    :output-format output-format
                                    :number-of-files number-of-files
                                    :file-size file-size
                                    :engine data-gen.engine/start})))
