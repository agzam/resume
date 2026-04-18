(ns resume.server
  "Dev server - serves docs/ with live reload on data changes."
  (:require [resume.core :as core]
            [org.httpkit.server :as http]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private content-types
  {"html" "text/html; charset=utf-8"
   "css"  "text/css; charset=utf-8"
   "js"   "application/javascript"
   "svg"  "image/svg+xml"
   "png"  "image/png"
   "jpg"  "image/jpeg"
   "jpeg" "image/jpeg"
   "pdf"  "application/pdf"
   "txt"  "text/plain; charset=utf-8"})

(defn- file-ext [path]
  (let [dot (.lastIndexOf path ".")]
    (when (< 0 dot)
      (subs path (inc dot)))))

(defonce ^:private version (atom 0))
(defonce ^:private watching? (atom false))

(def ^:private reload-script
  "<script>(function(){var v;setInterval(function(){fetch('/__version').then(function(r){return r.text()}).then(function(n){if(v&&n!==v)location.reload();v=n}).catch(function(){})},1000)})()</script>")

(defn- inject-reload-script
  "Insert live-reload polling script before closing body tag."
  [html]
  (str/replace html "</body>" (str reload-script "</body>")))

(defn- handler
  "Ring handler serving static files from docs/ with live reload."
  [req]
  (let [uri (:uri req)]
    (cond
      (= "/__version" uri)
      {:status 200
       :headers {"Content-Type" "text/plain"
                 "Cache-Control" "no-cache"}
       :body (str @version)}

      :else
      (let [path (if (= "/" uri) "/index.html" uri)
            file (java.io.File. (str "docs" path))]
        (if (.isFile file)
          (let [ext (file-ext path)
                ct (get content-types ext "application/octet-stream")]
            (if (= "html" ext)
              {:status 200
               :headers {"Content-Type" ct}
               :body (inject-reload-script (slurp file))}
              {:status 200
               :headers {"Content-Type" ct}
               :body (java.io.FileInputStream. file)}))
          {:status 404
           :headers {"Content-Type" "text/plain"}
           :body "not found"})))))

(defonce ^:private server (atom nil))

(defn- data-files-mtimes
  "Snapshot modification times of all .edn files in data/."
  []
  (let [dir (io/file "data")]
    (when (.isDirectory dir)
      (into {}
        (for [f (.listFiles dir)
              :when (and (.isFile f) (str/ends-with? (.getName f) ".edn"))]
          [(.getAbsolutePath f) (.lastModified f)])))))

(defn- start-watcher!
  "Poll data/*.edn for changes, regenerate and bump version."
  []
  (reset! watching? true)
  (future
    (loop [prev (data-files-mtimes)]
      (Thread/sleep 1000)
      (when @watching?
        (let [curr (data-files-mtimes)]
          (when (not= curr prev)
            (println "data changed, regenerating...")
            (try
              (core/generate)
              (swap! version inc)
              (catch Exception e
                (println "regeneration error:" (.getMessage e)))))
          (recur curr))))))

(defn- stop-watcher! []
  (reset! watching? false))

(defn start!
  "Start the dev server and file watcher.
  Regenerates resume files before serving."
  ([] (start! 3000))
  ([port]
   (core/generate)
   (swap! version inc)
   (start-watcher!)
   (reset! server (http/run-server handler {:port port}))
   (println (str "serving at http://localhost:" port))))

(defn stop!
  "Stop the dev server and file watcher."
  []
  (stop-watcher!)
  (when-let [s @server]
    (s)
    (reset! server nil)
    (println "server stopped.")))

(defn restart!
  "Regenerate and restart the server."
  []
  (stop!)
  (start!))
