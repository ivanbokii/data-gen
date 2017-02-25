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
   ["-h" "--help"]])

(defn load-definitions-from-file [path-to-definitions-file]
  (-> path-to-definitions-file slurp (json/parse-string true)))

(defn -main
  [& args]
  (let [{:keys [definition-name definitions-file number-of-files file-size output-format]} (:options (parse-opts args cli-options))
        selected-definition (definition-name (load-definitions-from-file definitions-file))
        record-generator (partial generators/generate-record-based-on-definition selected-definition output-format)]
    (engine/start record-generator number-of-files file-size)))
