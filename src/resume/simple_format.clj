(ns resume.simple-format
  (:require
   [clojure.string :as str]
   [hiccup.page :refer [html5 include-css]]
   [resume.page :as page.normal :refer [metas]]))

(defn interval [start end]
  (cond
    (= start end) start
    (nil? end) (str "Since " start)
    :else (str start "-" end)))

(defn experience [{{:keys [title
                           content]} :work}]
  [:div.experience
   [:h2 title]
   [:ul.experience
    (->>
     content
     (map
      (fn [{:keys [company
                   website
                   position
                   start
                   end
                   highlights]}]
        [:li.company
         [:div.summary
          [:div.name-and-role
           (if website
             [:a.name {:href website} company]
             [:div company])
           [:div.role position]]
          [:div.interval (interval start end)]]
         [:div.details
          (map identity highlights)]])))]])

(defn content [data]
  [:div.simple-format.content-container
   page.normal/pdf-icon
   (for [f [
            page.normal/header
            page.normal/summary
            page.normal/skills
            experience
            ;; projects
            ;; education
            ]]
     (f data))])

(defn page [{:keys [basics] :as data}]
  (html5
   [:head
    (metas data)
    (include-css "styles.css")
    (include-css "https://fonts.googleapis.com/css?family=Maven%20Pro")
    [:title (str (:name basics) ". " (:label basics))]
    [:body (content data)]]))

(defn generate [data]
  (->> data page (spit "docs/simple.html")))
