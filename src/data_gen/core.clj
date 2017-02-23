(ns data-gen.core
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def cli-options
  [
   [nil "--definitions DEFINITIONS" "file with definitions for generator"]
   ["-d" "--definition DEFINITION" "name of the definition to generate"]
   ["-n" "--number-of-files NUMBER-OF-FILES" "number of output files"
    :parse-fn #(Integer/parseInt %)]
   ["-s" "--file-size FILE-SIZE" "number of output files in MBs"
    :parse-fn #(Integer/parseInt %)]
   ["-h" "--help"]
   ])

(defn -main
  [& args]
  (let [parsed-args (parse-opts args cli-options)]
    ))
