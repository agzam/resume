;; -*-  eval: (rainbow-mode 1) -*-
(ns resume.styles
  (:require [garden.color :as gc]
            [garden.core :refer [css]]
            [garden.def :refer [defcssfn]]
            [garden.selectors :as gs :refer [&]]
            [garden.stylesheet :refer [at-media]]))

(def colors
  {:accent-dark "#7d6a24"
   :accent-light "#bcab84"
   :text-default "#4f3f3f"
   :accent-bright "#f1d4a6"
   :unimportant "#bfbdb4"})

(def background-tint
  [:body
   {:background "#FFFDF2; /* Old browsers */" }
   {:background "-moz-linear-gradient(left,  #FFFDF2 0%, #FFFDF2 37%, #ffffff 100%); /* FF3.6+ */"}
   {:background "-webkit-gradient(linear, left top, right top, color-stop(0%,#FFFDF2), color-stop(37%,#FFFDF2), color-stop(100%,#ffffff)); /* Chrome,Safari4+ */"}
   {:background "-webkit-linear-gradient(left,  #FFFDF2 0%,#FFFDF2 37%,#ffffff 100%); /* Chrome10+,Safari5.1+ */"}
   {:background "-o-linear-gradient(left,  #FFFDF2 0%,#FFFDF2 37%,#ffffff 100%); /* Opera 11.10+ */"}
   {:background "-ms-linear-gradient(left,  #FFFDF2 0%,#FFFDF2 37%,#ffffff 100%); /* IE10+ */"}
   {:background "linear-gradient(to right,  #FFFDF2 0%,#FFFDF2 37%,#ffffff 100%); /* W3C */"}])

(def media-queries
  [(at-media {:print true}
             [:body {:background "none !important"
                     :font-size "75%"
                     :max-width "99%"}
              ;; hide all links except email
              #_[".links li:not(:first-child) a" {:display :none}]
              #_[:.links [:img {:display :none}]]
              [:.header {:margin-top "0.5rem"}]
              [:.experience
               [:h2 {:font-size "1.3rem"}]
               [:.company [:.name {:font-size "1rem"}]]]
              [:.pdf-link {:display :none}]
              [:.updated-date {:display :none}]])
   (at-media {:screen :only
              :max-width "800px"}
             [:body {:max-width "95%"
                     :width "95%"}
              [:.pdf-link {:display :none}]
              [:.header
               {:margin-top "0.5rem"
                :grid-template-columns "minmax(25%, 2fr)"
                :grid-template-areas "'title' 'links'"}
               [:.location {:display :none}]
               [".links li:not(:first-child) a" {:display :none}]
               [:.links {:justify-self :flex-start
                         :margin-top 0}]]
              [:h2 {:font-size "1.5rem"}]
              [:.experience
               [:.company [:.name {:white-space :pre-wrap
                                   :font-size "1rem"}]]
               [:.summary [:.role :.interval {:font-size "1rem"}]]]])
   (at-media {:screen :only
              :min-width "801px"
              :max-width "1800px"}
             [:body {:max-width "75%"
                     :width "75%"}
              [:.experience [:.company [:.name {:white-space :pre-wrap
                                                :font-size "1.3rem"}]]]])
   (at-media {:screen :only
              :min-width "1800px"}
             [:body {:max-width "50%"
                     :width "50%"}]
             [:.experience
              [:.company
               [:.name {:font-size "1.8rem"}]]])])
(def body
  [:body {:font-family "Maven Pro, Sans-serif"
          :margin-left :auto
          :margin-right :auto
          :color (colors :text-default)
          :line-height "1.5rem"}
   [:.content-container {:position :relative}]])

(def common-tags
  [[:ul {:margin-block-start 0
         :margin-block-end 0
         :padding-inline-start 0
         :list-style :none}]
   [:li :p {:margin-block-start "5px"
            :margin-block-end "5px"}]
   [:a {:text-decoration :none
        :color :inherit
        :transition "0.7s all ease"}
    :&:visited {:text-decoration :none}
    [:&:hover {:color (colors :accent-bright)}]]
   [:h2 {:font-size "1.8rem"
         :color (colors :accent-light)}]
   [:.hidden-label {:font-size "0.1px"
                    :line-height "0.1px"}]])

(def pdf-link
  [:.pdf-link
   {:position :absolute
    :top "1.5rem"
    :right "-5rem"
    :transform-origin "top left"
    :transform "scale(1)"
    :transition ".3s all ease"}
   [:&:hover {:transform "scale(1.5)"}]
   [:img {:width "4rem"
          :filter "drop-shadow(3px 3px 3px #bebebe) saturate(0.3)"}]])

(def header
  [:.header {:display :grid
             :margin-top "3rem"
             :grid-template-columns "minmax(25%, 2fr) minmax(30%, 4fr)"
             :grid-template-areas "'title links'"}
   [:.title {:grid-area "title"}
    [:* {:white-space :nowrap}]]
   [:.links {:grid-area "links"
             :justify-self "flex-end"
             :margin-top "20px"}
    [:li {:white-space :nowrap}]
    [:img {:width "1.1rem"
           :vertical-align :middle
           :margin-right "0.5rem"
           :filter "drop-shadow(3px 3px 3px #bebebe) saturate(0.2)"}]]])

(def ul-defaults
  {:text-align :justify
   :list-style :disc
   :padding-inline-start "1rem"})

(def summary
  [:.summary [:ul ul-defaults]])

(def experience
  [:.experience
   [:.company {:margin-top "1.5rem"}
    [:.name {;; :font-size "1.6rem"
             :white-space :nowrap
             :margin-bottom "5px"
             :color (colors :accent-dark)
             :z-index 1}
     [:&:hover {:color (colors :accent-bright)}]]
    [:>div {:display :grid
            :grid-template-columns "minmax(25%, 2fr) minmax(30%, 4fr)"
            :grid-gap "10px"
            :grid-template-areas "'summary details'"
            :grid-template-rows :max-content}
     [:.summary {:grid-area "summary"
                 :display :flex
                 :flex-direction :column
                 :position :relative}
      [:.name :.company-sector
       :.company-location
       :.role :.interval
       :.keywords {:margin-right "5px"}]
      [:.company-sector
       :.company-location {:font-size :x-small
                           :line-height "15px"
                           :margin-left "2px"
                           :color (colors :unimportant)}]
      [:.role {}]
      [:.interval {}]]
     [:.details {:grid-area "details"
                 :text-align :justify
                 :position :relative}
      [:ul (merge ul-defaults {:list-style :circle})]]
     [:.keywords {:margin-top :auto
                  :margin-bottom 0
                  :padding-top "20px"
                  :font-size :small
                  :color (colors :unimportant)}
      [:h5 {:margin-block-start 0
            :margin-block-end 0}]]]]])

(def projects
  [:.projects
   [:.project {:margin-top "1.5rem"}
    [:.name {:font-size "1.2rem"
             :white-space :nowrap
             :margin-bottom "5px"
             :color (colors :accent-dark)
             :z-index 1}
     [:&:hover {:color (colors :accent-bright)}]]
    [:>div {:display :grid
            :grid-template-columns "minmax(25%, 2fr) minmax(30%, 4fr)"
            :grid-gap "10px"
            :grid-template-areas "'summary details'"
            :grid-template-rows :max-content}
     [:.summary {:grid-area "summary"
                 :display :flex
                 :flex-direction :column
                 :position :relative}
      [:.name :.company-sector]]
     [:.description
      {:grid-area "details"
       :text-align :justify
       :position :relative}
      [:ul (merge ul-defaults {:list-style :circle})]]]]
   [:li.note
    {:margin-top "1.5rem"}
    [:p {:display :table
         :margin-right :auto
         :margin-left :auto
         :font-size "0.9rem"
         :text-align :center
         :color (colors :unimportant)}]]])

(defcssfn linear-gradient)

(def thread-decors
  (let [h-grad (linear-gradient
                "to right"
                [(gc/opacify "#ffffff" 1) "0%"]
                [(gc/hex->rgb (colors :accent-bright)) "100%"])
        v-grad (linear-gradient
                "to bottom"
                [(gc/hex->rgb (colors :accent-bright)) "0%"]
                [(gc/opacify "#ffffff" 1) "100%"])]
    [[:.thread-decor-h
      {:background-image h-grad
       :height "2px"
       :position :absolute
       :width "20%"
       :right "5px"
       :top "13px"}]
     [:.thread-decor-v
      {:width "2px"
       :background-color (colors :accent-bright)
       :position :absolute
       :height "calc(100% + 25px)"
       :top "13px"
       :left "-15px"}]
     [".company:nth-last-of-type(2)"
      ".project:nth-last-of-type(2)"
      [:.thread-decor-v {:background-image v-grad
                         :height "calc(100% + 15px)"}]]

     (let [rad 10
           px #(str % "px")]
       [(& :.thread-decor-h (gs/after))
        {:content "' '"
         :width (px rad)
         :height (px rad)
         :background-color (colors :accent-bright)
         :position :absolute
         :right (px (- (quot rad 2)))
         :top (px (- (quot rad 2)))
         :border-radius (px (* rad 2))}])]))

(def prior-exp-not-provided-remark
  [:.prior-exp-not-provided {:margin-top "1.5rem"}
   [:p {:display :table
        :margin-right :auto
        :margin-left :auto
        :font-size "0.9rem"
        :text-align :center
        :color (colors :unimportant)}]])

(def education
  [:.institution {:font-size :smaller}])

(def updated-date
  [:.updated-date
   {:float "right"
    :margin-top "2rem"
    :margin-bottom "2rem"
    :font-size "6px"
    :color (colors :unimportant)}])

(defn generate
  ([path]
   (css
    {:output-to path}
    [background-tint
     body
     common-tags
     pdf-link
     summary
     header
     experience
     projects
     thread-decors
     prior-exp-not-provided-remark
     education
     updated-date
     media-queries]))
  ([]
   (generate "docs/styles.css")))
