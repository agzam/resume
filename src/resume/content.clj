(ns resume.content
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [resume.page :as page]
            [resume.styles :as styles]
            [integrant.core :as ig]
            [clojure.string :as str])
  (:import [java.text SimpleDateFormat]))

(defn get-data []
  (->> "fullstack-dev.edn"
       slurp
       edn/read-string
       eval))


(defn datestr->mm-yyyy
  "Converts date-string. Oct 2023 -> 2023-10"
  [datestr]
  (let [old-fmt (SimpleDateFormat. "MMM yyyy")
        new-fmt (SimpleDateFormat. "MM/yyyy")
        old-date (when-not (str/blank? datestr)
                   (.parse old-fmt datestr))]
    (when old-date
      (.format new-fmt old-date))))

(defn generate-plain-text
  "Generate plain text resume, for automated resume parsing systems."
  [data]
  (let [personal-info
        (let [{:keys [name label email location]
               :as basics} (-> data :basics)
              {:keys [city state zipcode]} location
              li (->> basics :profiles
                      (filter #(-> % :network (= "LinkedIn")))
                      first :url)
              gh (->> basics :profiles
                      (filter #(-> % :network (= "LinkedIn")))
                      first :url)]
         (format
          (str
           "plain-text version for automated parsers, "
           "for a human-readable, visit:\n"
           "https://agzam.github.io/resume\n\n"
           "Name: %s\n"
           "Title: %s\n"
           "Email: %s\n"
           "LinkedIn: %s\n"
           "GitHub: %s\n"
           "City: %s\n"
           "State: %s\n"
           "Zipcode: %s\n\n")
          name label email li gh city state zipcode))
        summary (->> data :basics :summary
                     :content page/hiccup->text
                     (format "# Summary:\n%s\n"))
        parse-exp (fn [{:keys [highlights
                               start end
                               position company location
                               keywords]}]
                    (format (str "Title: %s\n"
                                 "Company: %s\n"
                                 "Location: %s\n"
                                 "From: %s\n"
                                 (if end
                                   "To: %s\n"
                                   "%s")
                                 "Skills: %s\n"
                                 "Description:\n%s")
                            position
                            company
                            location
                            (datestr->mm-yyyy start)
                            (or (datestr->mm-yyyy end) "")
                            (->>
                             keywords
                             (map name)
                             (str/join ", "))
                            (page/hiccup->text highlights)))
        experience (->> data :work :content
                        (map parse-exp)
                        (str/join "--------------------------------------------------\n"))]
    (str personal-info
         summary
         "# Work history:\n--------------------------------------------------\n"
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
