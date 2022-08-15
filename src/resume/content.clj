(ns resume.content
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [resume.page :as page]
            [resume.styles :as styles]
            [integrant.core :as ig]))

(defn get-data []
  (->> "resources/fullstack-dev.edn"
       slurp
       edn/read-string))

(defn generate []
  (->> (get-data)
       page/index
       (spit "resources/public/index.html"))
  (styles/generate "resources/public/styles.css"))

(defmethod ig/init-key ::generate [_ _]
  (generate))

(defmethod ig/halt-key! ::generate [_ _]
  (io/delete-file "resources/public/styles.css" :silently)
  (io/delete-file "resources/public/index.html" :silently))
