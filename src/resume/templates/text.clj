(ns resume.templates.text
  "Plain text resume for automated parsing systems."
  (:require [clojure.string :as str]
            [resume.html :as html]))

(defn- datestr->mm-yyyy
  "Convert 'Oct 2023' or 'June 2020' to '10/2023' or '06/2020'."
  [datestr]
  (when-not (str/blank? datestr)
    (let [fmt-out (java.time.format.DateTimeFormatter/ofPattern "MM/yyyy")
          try-parse (fn [pat]
                      (try (java.time.YearMonth/parse
                            datestr
                            (java.time.format.DateTimeFormatter/ofPattern pat))
                           (catch Exception _ nil)))
          ym (or (try-parse "MMM yyyy")
                 (try-parse "MMMM yyyy"))]
      (some-> ym (.format fmt-out)))))

(defn- personal-info
  "Render personal info block as text."
  [data]
  (let [{:keys [name label email location]} (:basics data)
        {:keys [city state zipcode]} location
        profile-url (fn [network]
                      (->> (:basics data) :profiles
                           (filter #(= network (:network %)))
                           first :url))]
    (format
     (str "plain-text version for automated parsers, "
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
     name (html/hiccup->flat-text label) email
     (profile-url "LinkedIn")
     (profile-url "GitHub")
     city state zipcode)))

(defn- experience-entry
  "Render a single work entry as text."
  [{:keys [highlights start end company position location keywords]}]
  (format (str "Company: %s\n"
               "Job Title: %s\n"
               "Location: %s\n"
               "From: %s\n"
               (if end "To: %s\n" "%s")
               "Skills: %s\n"
               "Description:\n%s")
          company
          position
          location
          (datestr->mm-yyyy start)
          (if end (datestr->mm-yyyy end) "")
          (->> keywords (map name) (str/join ", "))
          (html/hiccup->text highlights)))

(defn render
  "Render the resume as plain text."
  [data]
  (let [sep "---\n"
        summary (->> data :basics :summary :content
                     html/hiccup->text
                     (format "# Summary:\n%s\n"))
        experience (->> data :work :content
                        (map experience-entry)
                        (str/join sep))]
    (str (personal-info data)
         summary
         "# Work history:\n" sep
         experience)))
