(ns resume.html
  "Minimal hiccup-to-HTML renderer for babashka."
  (:require [clojure.string :as str]))

(def ^:private void-elements
  #{:area :base :br :col :embed :hr :img :input
    :link :meta :source :track :wbr})

(defn escape-html
  "Escape special characters for safe HTML text content."
  [s]
  (-> (str s)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn- parse-tag
  "Parse :div.class1.class2#id into tag name, classes, and id."
  [tag-kw]
  (let [s (name tag-kw)
        tag-name (or (re-find #"^[^.#]+" s) "div")
        classes (->> (re-seq #"\.([^.#]+)" s) (map second))
        id (second (re-find #"#([^.#]+)" s))]
    {:tag tag-name :classes classes :id id}))

(defn- render-attrs
  "Render an attribute map to an HTML attribute string."
  [attrs]
  (->> attrs
       (keep (fn [[k v]]
               (when (some? v)
                 (if (true? v)
                   (str " " (name k))
                   (str " " (name k) "=\"" (escape-html v) "\"")))))
       (str/join "")))

(declare render)

(defn- render-content
  "Render a sequence of mixed content items to HTML."
  [items]
  (->> items
       (map (fn [item]
              (cond
                (nil? item) ""
                (string? item) (escape-html item)
                (vector? item) (render item)
                (sequential? item) (render-content item)
                :else (escape-html (str item)))))
       (str/join "")))

(defn render
  "Render a hiccup form to an HTML string.
  Handles tags with class/id shorthand, attribute maps,
  nested vectors, sequences, and void elements."
  [form]
  (cond
    (nil? form) ""
    (string? form) (escape-html form)
    (vector? form)
    (if (keyword? (first form))
      (let [[tag-kw & rest] form
            {:keys [tag classes id]} (parse-tag tag-kw)
            [attrs content] (if (map? (first rest))
                              [(first rest) (next rest)]
                              [nil rest])
            all-classes (cond-> (vec classes)
                          (:class attrs) (conj (:class attrs)))
            attrs (cond-> (dissoc (or attrs {}) :class)
                    (seq all-classes) (assoc :class (str/join " " all-classes))
                    id (assoc :id id))]
        (if (void-elements (keyword tag))
          (str "<" tag (render-attrs attrs) ">")
          (str "<" tag (render-attrs attrs) ">"
               (render-content content)
               "</" tag ">")))
      (render-content form))
    (sequential? form) (render-content form)
    :else (escape-html (str form))))

(defn html5
  "Wrap hiccup content in an HTML5 document."
  [& content]
  (str "<!DOCTYPE html>\n<html>"
       (render-content content)
       "</html>\n"))

(defn include-css
  "Return a hiccup link element for a CSS stylesheet."
  [url]
  [:link {:href url :rel "stylesheet" :type "text/css"}])

(defn hiccup->text
  "Convert a hiccup form to plain text with basic formatting."
  [form]
  (cond
    (nil? form) ""
    (string? form) form
    (vector? form)
    (let [[tag & rest] form
          content (if (map? (first rest)) (next rest) rest)]
      (case tag
        :p (str (str/join " " (map hiccup->text content)) "\n")
        :ul (->> content
                 (map #(str "- " (hiccup->text %) "\n"))
                 (str/join ""))
        (str/join " " (map hiccup->text content))))
    (sequential? form)
    (str/join "" (map hiccup->text form))
    :else ""))

(defn hiccup->flat-text
  "Extract all text from hiccup without any formatting.
  Useful for titles, meta descriptions, etc."
  [form]
  (cond
    (nil? form) ""
    (string? form) form
    (sequential? form)
    (let [items (if (and (vector? form) (keyword? (first form)))
                  (let [[_ & rest] form]
                    (if (map? (first rest)) (next rest) rest))
                  form)]
      (->> items
           (map hiccup->flat-text)
           (remove str/blank?)
           (str/join " ")))
    :else ""))
