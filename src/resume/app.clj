(ns resume.app
  (:require [resume.content :as content])
  (:gen-class))

(defn -main
  "Generates resume content to be hosted on GH pages"
  [& args]
  (content/generate))
