(ns data-gen.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [cheshire.core :as json]
            [data-gen.generators :as generators]
            [data-gen.engine :as engine]
            [clojure.string :as str])
  (:gen-class))

(defn file-exists [path-to-file]
  (.exists (clojure.java.io/as-file path-to-file)))

(defn supported-format [format]
  (contains? #{:csv :json} format))

(defn validate-input [params]
  (when (not (file-exists (:definitions-file params))) "Sorry, can't file definitions file")
  (when (not (supported-format (:output-format params))) "Sorry, don't support the output format"))

(def cli-options
  [["-f" "--definitions-file DEFINITIONS" "file with definitions for generator"]
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

(defn print-configuration [params definition]
  (println (->> ["Running generator with next parameters:"
                 (str "- definition file: " (:definitions-file params))
                 (str "- definition name: " (:definition-name params))
                 (str "- output folder: " (:output-folder params))
                 (str "- output format: " (:output-format params))
                 (str "- number of files: " (:number-of-files params))
                 (str "- file size (MB): " (:file-size params))
                 ""
                 "definition: "]
                (str/join \newline)))
  (clojure.pprint/pprint definition))

(defn provide-input-defaults [params]
  (merge-with #(or %1 %2) params {:definitions-file "./definitions.json"
                                  :definition-name :main
                                  :output-folder "./"
                                  :output-format :json
                                  :number-of-files 1
                                  :file-size 1
                                  :engine data-gen.engine/start}))

(defn make-generator-and-run-engine [params]
  (if-let [error (validate-input params)]
    (println error)
    (let [loaded-definitions (load-definitions-from-file (:definitions-file params))
          selected-definition ((:definition-name params) loaded-definitions)
          record-generator (partial generators/generate-record-based-on-definition selected-definition (:output-format params))]
      (print-configuration params selected-definition)
      ((:engine params) record-generator (:number-of-files params) (:file-size params) (:output-folder params)))))

(defn -main
  [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (if (:help options) (println summary)
        (make-generator-and-run-engine (provide-input-defaults options)))))
