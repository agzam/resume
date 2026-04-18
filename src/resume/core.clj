(ns resume.core
  "Entry point. Loads data, dispatches to templates, writes output."
  (:require [clojure.edn :as edn]
            [resume.templates.full :as full]
            [resume.templates.simple :as simple]
            [resume.templates.text :as text]))

(def default-data-file "data/fullstack-dev.edn")

(defn load-data
  "Read and eval an EDN data file.
  Eval handles inline (str ...) forms in the data."
  ([] (load-data default-data-file))
  ([path]
   (-> path slurp edn/read-string eval)))

(def templates
  {:full   {:render full/render   :output "docs/index.html"}
   :simple {:render simple/render :output "docs/simple.html"}
   :text   {:render text/render   :output "docs/resume.txt"}})

(defn generate
  "Generate all resume output files from the data."
  ([] (generate (load-data)))
  ([data]
   (doseq [[k {:keys [render output]}] templates]
     (let [result (render data)]
       (spit output result)
       (println (str "  " output " (" (name k) ")"))))
   (println "done.")))

(defn -main [& _args]
  (println "generating resume...")
  (generate))
