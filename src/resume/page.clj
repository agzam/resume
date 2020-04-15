(ns resume.page
  (:require [hiccup.page :refer [html5 include-css]]
            [clojure.string :as str]))

(defn links [{:keys [links]}]
  [:ul.links
   (map
    (fn [{:keys [url label icon]}]
      [:li [:a {:href url}
            (when icon [:img {:src icon}])
            label]])
    links)])

(def pdf-icon
  [:a.pdf-link {:href "resume.pdf"}
   [:img {:src "pdf-icon.svg"}]])

(defn header [{:keys [header] :as data}]
  (let [{:keys [first-name
                last-name
                role
                location]} header]
    [:div.header
     [:div.title
      [:h1.name (str first-name " " last-name)]
      [:p.role role]
      [:p.location location]]
     (links data)]))

(defn keywords [kws]
  (let [underscore->spc #(str/replace % #"_" " ")
        kws             (map (comp underscore->spc name) kws)]
    [:div.keywords
     [:span.hidden-label "keywords:"]
     [:p (str/join ", " kws)]]))

(defn summary [{:keys [main-summary]}]
  (let [{:keys [title content bullet-points?]} main-summary]
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

(defn experience [{:keys [experience]}]
  (let [{:keys [title
                content
                prior-experience-not-provided-note]} experience]
      [:div.experience
       [:h2 title]
       [:ul.experience
        (map
         (fn [{:keys [name
                      link
                      industry
                      location
                      role
                      start
                      end
                      details
                      components]}]
           [:li.company
            [:div
             [:div.summary
              (if link
                [:a.name {:href link} name]
                [:div.name name])
              [:div.thread-decor-h]
              (when industry
               [:span.hidden-label "industry:"]
               [:div.company-sector industry])
              [:span.hidden-label "company address:"]
              [:div.company-location location]
              [:span.hidden-label "role:"]
              [:div.role role]
              [:div.interval (interval start end)]
              (keywords components)]
             [:div.details
              [:div.thread-decor-v]
              [:div.text (map identity details)]]]])
         content)
        (when prior-experience-not-provided-note
          [:li.prior-exp-not-provided
           [:p prior-experience-not-provided-note]])]]))

(defn education [{:keys [education]}]
  (let [{:keys [title content]} education]
    [:div.education
     [:h2 title]
     content]))

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

(defn index [data]
  (html5
   [:head
    metas
    (include-css "styles.css")
    (include-css "https://fonts.googleapis.com/css?family=Maven%20Pro")
    [:title "Ag Ibragimov. Software Developer"]
    [:body
     (content data)]]))
