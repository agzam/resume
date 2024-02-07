(ns resume.content
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [resume.page :as page]
            [resume.styles :as styles]
            [integrant.core :as ig]
            [hiccup.core :as h]
            [clojure.string :as str])
  (:import [org.jsoup Jsoup]))

(defn get-data []
  (->> "fullstack-dev.edn"
       slurp
       edn/read-string
       eval))

(defn hiccup->text [hiccup]
  (cond
    (string? hiccup) hiccup
    (sequential? hiccup)
    (let [tag  (first hiccup)
          rest (next hiccup)]
      (case tag
        :p  (str (str/join " " (map hiccup->text rest)) "\n")
        :ul (str/join "" (map #(str "- " (hiccup->text %) "\n") rest))
        (str/join " " (map hiccup->text rest))))
    :else ""))

(defn generate-plain-text
  "Generate plain text resume, for automated resume parsing systems."
  [data]
  (let [personal-info (format
                       (str
                        "%s\n%s\n%s\n"
                        "plain-text version for automated parsers, "
                        "for a human-readable, visit:\n"
                        "https://agzam.github.io/resume\n\n")
                       (-> data :basics :name)
                       (-> data :basics :label)
                       (-> data :basics :email))
        summary (->> data :basics :summary
                    :content hiccup->text
                    (format "# Summary:\n%s\n"))
        parse-exp (fn [{:keys [highlights
                               start end
                               company position
                               keywords]}]
                    (format (str "Company: %s\n"
                                 "Title: %s\n"
                                 "From: %s\n"
                                 (if end
                                  "To: %s\n"
                                  "%s")
                                 "Keywords: %s\n"
                                 "Description:\n%s\n")
                            company
                            position
                            start
                            (or end "")
                            (->>
                             keywords
                             (map name)
                             (str/join ", "))
                            (hiccup->text highlights)))
        experience (->> data :work :content
                        (map parse-exp)
                        (str/join ""))]
    (str personal-info
         summary
         "# Work experience:\n\n"
         experience)))

(defn generate []
  (let [data (get-data)]
    (->> data generate-plain-text
         (spit "docs/resume.txt"))
   (->> data
        page/index
        (spit "docs/index.html"))
   (styles/generate "docs/styles.css")))

(defmethod ig/init-key ::generate [_ _]
  (generate))

(defmethod ig/halt-key! ::generate [_ _]
  (io/delete-file "docs/styles.css" :silently)
  (io/delete-file "docs/index.html" :silently))
