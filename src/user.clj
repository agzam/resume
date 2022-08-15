(ns user
  (:require [integrant.repl :as ig-repl :refer [go halt]]
            [integrant.core :as ig]))

(def config {:resume.server/server
             {:port 3000
              :join? false}
             :resume.content/generate {}})

(ig-repl/set-prep!
 (fn []
   (ig/load-namespaces config)
   (ig/prep config)))
