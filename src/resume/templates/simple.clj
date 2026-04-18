(ns resume.templates.simple
  "Simple resume template - flatter layout, no thread decorations."
  (:require [resume.components :as c]
            [resume.html :as html]))

(defn- experience
  "Render work experience in a compact, flat layout."
  [{{:keys [title content]} :work}]
  [:div.experience
   [:h2 title]
   [:ul.experience
    (map
     (fn [{:keys [company website position
                  start end highlights]}]
       [:li.company
        [:div.summary
         [:div.name-and-role
          (if website
            [:a.name {:href website} company]
            [:div company])
          [:div.role position]]
         [:div.interval (c/interval start end)]]
        [:div.details
         (map identity highlights)]])
     content)]])

(defn- content [data]
  [:div.simple-format.content-container
   c/pdf-icon
   (for [f [c/header c/summary c/skills experience]]
     (f data))])

(defn render
  "Render the simple resume as an HTML string."
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
