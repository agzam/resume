(ns resume.data
  (:require [clojure.edn :refer [read-string]]))

(defn get-data []
  (->> "resources/fullstack-dev.edn"
       slurp
       read-string))
