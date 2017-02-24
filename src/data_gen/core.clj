(ns data-gen.core
  (:require [clojure.tools.cli :refer [parse-opts]])
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

(defn load-from-file [path-to-definitions-file]
  {:second "fuck you"})


(defn -main
  [& args]
  (let [{:keys [definition-name definitions-file]} (:options (parse-opts args cli-options))
        selected-definition (definition-name (load-from-file definitions-file))]
    (println selected-definition)))
