(ns resume.page
  (:require [hiccup.page :refer [html5 include-css]]
            [clojure.string :as str]))

(defn links-block [data]
  (let [link  (fn [{:keys [network url icon]}]
                [:li [:a {:href url}
                      (when icon [:img {:src icon}])
                      network]])
        email (-> data :basics :email)]
    [:ul.links
     (link {:url     (str "mailto:" email)
            :network email
            :icon    "email-icon.svg"})
     (map link (-> data :basics :profiles))]))

(def pdf-icon
  [:a.pdf-link {:href "resume.pdf"}
   [:img {:src "pdf-icon.svg"}]])

(defn header [{:keys [basics] :as data}]
  (let [{:keys [name label location]} basics]
    [:div.header
     [:div.title
      [:h1.name name]
      [:p.role label]
      [:p.location (:region location)]]
     (links-block data)]))

(defn keywords-block [kws]
  (let [underscore->spc #(str/replace % #"_" " ")
        kws             (map (comp underscore->spc name) kws)]
    [:div.keywords
     [:span.hidden-label "keywords:"]
     [:p (str/join ", " kws)]]))

(defn summary [data]
  (let [{:keys [title content bullet-points?]} (-> data :basics :summary)]
    [:div.summary
     [:h2 title]
     (if bullet-points?
       [:ul
        (map
         (fn [item]
           [:li item])
         content)]
       content)]))

(def prior-exp-not-provided-remark
  [:li.prior-exp-not-provided
   [:p "jobs before 2009 are not displayed; a complete list can be provided upon
   request."]])

(defn interval [start end]
  (cond
    (= start end) start
    (nil? end)    (str "Since " start)
    :else (str start "-" end)))

(defn experience [{:keys [work]}]
  (let [{:keys [title
                content
                prior-experience-not-provided-note]} work]
      [:div.experience
       [:h2 title]
       [:ul.experience
        (map
         (fn [{:keys [company
                      website
                      industry
                      location
                      position
                      start
                      end
                      highlights
                      keywords]}]
           [:li.company
            [:div
             [:div.summary
              (if website
                [:a.name {:href website} company]
                [:div.name company])
              [:div.thread-decor-h]
              (when industry
               [:span.hidden-label "industry:"]
               [:div.company-sector industry])
              [:span.hidden-label "company address:"]
              [:div.company-location location]
              [:span.hidden-label "role:"]
              [:div.role position]
              [:div.interval (interval start end)]
              (keywords-block keywords)]
             [:div.details
              [:div.thread-decor-v]
              [:div.text (map identity highlights)]]]])
         content)
        (when prior-experience-not-provided-note
          [:li.prior-exp-not-provided
           [:p prior-experience-not-provided-note]])]]))

(defn education [{:keys [education]}]
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

(def updated
  [:div.updated-date
   (str "updated:" (.format (new java.text.SimpleDateFormat "MMM yyyy") (java.util.Date.)))])

(defn content [data]
  [:div.content-container
   pdf-icon
   (for [f [header summary experience education]]
     (f data))
   updated])

(def metas
  (list
   [:meta {:name "copyright" :content "Ag Ibragimov. All registered trademarks belong to their respective owners"}]
   [:meta {:name "description" :content "Ag Ibragimov. Software Developer. Resume"}]
   [:meta {:name    "keywords"
           :content "front-end, back-end, fullstack, developer, engineer,
                    clojure, clojurescript, javascript, angular, react, object-oriented,
                    functional, oop, fp emacs, vim, d3, css3, san francisco, closure,
                    closurescript"}]
   [:meta {:name "revisit-after" :content "2 days"}]
   [:meta {:http-equiv "cache-control" :content "no-cache"}]
   [:meta {:name "viewport" :content "initial-scale=1, width=device-width"}]) )

(defn index [{:keys [basics] :as data}]
  (html5
   [:head
    metas
    (include-css "styles.css")
    (include-css "https://fonts.googleapis.com/css?family=Maven%20Pro")
    [:title (str (:name basics) "." (:label basics))]
    [:body
     (content data)]]))
