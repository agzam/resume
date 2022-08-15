(ns resume.server
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.middleware :refer [wrap-base-url]]
            [integrant.core :as ig]
            [resume.content :refer [get-data]]
            [resume.page :as page]
            [ring.adapter.jetty :as jetty]))

(defroutes routes
  (GET "/" [] (page/index (get-data)))
  (route/resources "/" {:root ""})
  (route/not-found "Not Found"))

(def app
  (->
   (handler/site routes)
   (wrap-base-url)))

(defmethod ig/init-key :resume.server/server [_ {:keys [port] :as opts}]
  (println (str "server running on port: " port))
  (jetty/run-jetty app opts))

(defmethod ig/halt-key! :resume.server/server [_ server]
  (println "stopping the server")
  (.stop server))
