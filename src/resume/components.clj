(ns resume.components
  "Shared hiccup components used across resume templates."
  (:require [clojure.string :as str]
            [resume.html :as html]))

(defn- links-block
  "Render profile links (email, github, linkedin, etc.)."
  [data]
  (let [link (fn [{:keys [url icon label]}]
               [:li [:a {:href url}
                     (when icon [:img {:src (str "img/" icon)}])
                     (cond
                       label label
                       url (str/replace url #"https://|mailto:" ""))]])
        email (-> data :basics :email)]
    [:ul.links
     (link {:url (str "mailto:" email)
            :icon "email-icon.svg"})
     (map link (-> data :basics :profiles))]))

(def pdf-icon
  [:a.pdf-link {:href "resume.pdf"}
   [:img {:src "img/pdf-icon.svg"}]])

(defn header
  "Render resume header with name, label, and profile links."
  [{:keys [basics] :as data}]
  (let [{:keys [name label]} basics]
    [:div.header
     [:div.title
      [:h1.name name]
      [:p.role label]]
     (links-block data)]))

(defn summary
  "Render summary section."
  [data]
  (let [{:keys [title content bullet-points?]} (-> data :basics :summary)]
    [:div.summary
     [:h2 title]
     (if bullet-points?
       [:ul (map (fn [item] [:li item]) content)]
       content)]))

(defn skills
  "Render skills section."
  [data]
  [:div.skills
   [:h2 "Skills"]
   [:p (-> data :basics :skills)]])

(defn interval
  "Format a date range for display."
  [start end]
  (cond
    (= start end) start
    (nil? end) (str "Since " start)
    :else (str start "-" end)))

(defn keywords-block
  "Render a keywords/tags block for a work entry."
  [kws]
  (let [underscore->spc #(str/replace % #"_" " ")
        kws (->> kws
                 (map (comp underscore->spc name))
                 sort)]
    [:div.keywords
     [:span.hidden-label "keywords:"]
     [:p (str/join ", " kws)]]))

(defn- first-sentence [s]
  (-> s (str/split #"\. ") first str/trim))

(defn metas
  "Render meta tags for SEO and OpenGraph."
  [data]
  (list
   [:meta {:name "copyright"
           :content "Ag Ibragimov. All registered trademarks belong to their respective owners"}]
   [:meta {:name "description"
           :content "Ag Ibragimov. Software Developer. Resume"}]
   [:meta {:name "keywords"
           :content (str "front-end, back-end, fullstack, developer, engineer, clojure, clojurescript, "
                         "javascript, react, object-oriented, functional, FP, Emacs, Vim, "
                         "d3, css3, cybersecurity.")}]
   [:meta {:name "revisit-after" :content "2 days"}]
   [:meta {:http-equiv "cache-control" :content "no-cache"}]
   [:meta {:name "viewport" :content "initial-scale=1, width=device-width"}]
   (for [[prop content] {:title "Ag Ibragimov. Software Developer."
                         :type "website"
                         :url "https://agzam.github.io/resume"
                         :description (-> data :basics :summary :content
                                         html/hiccup->text first-sentence)
                         :image "img/avatar.jpeg"}]
     [:meta {:property (str "og:" (name prop))
             :content content}])))
