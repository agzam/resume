(ns resume.templates.full
  "Full resume template with thread decorations, projects, and education."
  (:require [resume.components :as c]
            [resume.html :as html]))

(defn- experience
  "Render work experience with thread decorations and keyword tags."
  [{:keys [work]}]
  (let [{:keys [title content prior-experience-not-provided-note]} work]
    [:div.experience
     [:h2 title]
     [:ul.experience
      (map
       (fn [{:keys [company website position
                    start end highlights keywords]}]
         [:li.company
          [:div
           [:div.summary
            (if website
              [:a.name {:href website} company]
              [:div.name company])
            [:div.thread-decor-h]
            [:span.hidden-label "company address:"]
            [:span.hidden-label "role:"]
            [:div.role position]
            [:div.interval (c/interval start end)]
            (c/keywords-block keywords)]
           [:div.details
            [:div.thread-decor-v]
            [:div.text (map identity highlights)]]]])
       content)
      (when prior-experience-not-provided-note
        [:li.prior-exp-not-provided
         [:p prior-experience-not-provided-note]])]]))

(defn- projects
  "Render personal projects section."
  [{:keys [projects]}]
  (let [{:keys [title content note]} projects]
    [:div.projects
     [:h2 title]
     [:ul.projects
      (map
       (fn [{:keys [name link description]}]
         [:li.project
          [:div
           [:div.summary
            [:a.name {:href link} name]
            [:div.thread-decor-h]]
           [:div.description
            [:div.thread-decor-v]
            [:div.text description]]]])
       content)
      [:li.note [:p note]]]]))

(defn- education
  "Render education section."
  [{:keys [education]}]
  (let [{:keys [title content]} education]
    [:div.education
     [:h2 title]
     [:ul
      (map
       (fn [{:keys [institution website area study-type]}]
         [:li
          [:p (str study-type " of " area)]
          [:p.institution [:a {:href website} institution]]])
       content)]]))

(defn- updated
  "Render the 'last updated' footer date."
  []
  (let [now (java.time.YearMonth/now)
        fmt (java.time.format.DateTimeFormatter/ofPattern "MMM yyyy")]
    [:div.updated-date
     (str "updated:" (.format now fmt))]))

(defn- content [data]
  [:div.content-container
   c/pdf-icon
   (for [f [c/header c/summary c/skills
            experience projects education]]
     (f data))
   (updated)])

(defn render
  "Render the full resume as an HTML string."
  [{:keys [basics] :as data}]
  (html/html5
   [:head
    (c/metas data)
    (html/include-css "styles.css")
    (html/include-css "https://fonts.googleapis.com/css?family=Maven%20Pro")
    [:title (str (:name basics) ". "
                 (html/hiccup->flat-text (:label basics)))]]
   [:body
    (content data)]))
