(ns resume.server
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.middleware :refer [wrap-base-url]]
            [integrant.core :as ig]
            [resume.page :as page]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as resource]
            [ring.util.response :as resp]
            [resume.data :refer [get-data]]))

(defroutes routes
  (GET "/" [] (page/index (get-data)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (->
   (handler/site routes)
   (wrap-base-url)))

(defmethod ig/init-key :resume.server/server [_ opts]
  (jetty/run-jetty app opts))

(defmethod ig/halt-key! :resume.server/server [_ server]
  (.stop server))
