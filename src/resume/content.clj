(ns resume.content
  (:require [resume.page :as page]
            [resume.styles :as styles]
            [resume.data :refer [get-data]]))

(defn generate []
  (->> (get-data)
       page/index
       (spit "target/index.html"))
  (styles/generate "target/styles.css"))
