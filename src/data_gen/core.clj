(ns data-gen.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [cheshire.core :as json]
            [data-gen.generators :as generators]
            [data-gen.engine :as engine])
  (:gen-class))

(def cli-options
  [
   ["-f" "--definitions-file DEFINITIONS" "file with definitions for generator"]
   ["-d" "--definition-name DEFINITION" "name of the definition to generate"
    :parse-fn #(keyword %)]
   ["-n" "--number-of-files NUMBER-OF-FILES" "number of output files"
    :parse-fn #(Integer/parseInt %)]
   ["-s" "--file-size FILE-SIZE" "number of output files in MBs"
    :parse-fn #(Integer/parseInt %)]
   ["-h" "--help"]])

(def generators-mapping
  {:number generators/number
   :string generators/string
   :placeholder generators/string-with-num-placeholder
   :take-random generators/random-from-seq})

(defn load-definitions-from-file [path-to-definitions-file]
  (-> path-to-definitions-file slurp (json/parse-string true)))

(defn generate-record-based-on-definition [definition]
  (reduce
   (fn [result key-name]
     (let [generator-definition-data (key-name definition)
           generator-name (-> generator-definition-data first keyword)
           generator-params (rest generator-definition-data)]
       (assoc result key-name (apply (generators-mapping generator-name) generator-params))))
   {} (keys definition)))

(defn -main
  [& args]
  (let [{:keys [definition-name definitions-file number-of-files file-size]} (:options (parse-opts args cli-options))
        selected-definition (definition-name (load-definitions-from-file definitions-file))
        record-generator (partial generate-record-based-on-definition selected-definition)]
    (engine/start number-of-files file-size record-generator)))
