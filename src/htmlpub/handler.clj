(ns htmlpub.handler
  (:require
    [clojure.data.json :as json]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :as route]
    (ring.middleware
      [absolute-redirects :refer [wrap-absolute-redirects]]
      [default-charset :refer [wrap-default-charset]]
      [proxy-headers :refer [wrap-forwarded-remote-addr]]
      [keyword-params :refer [wrap-keyword-params]]
      [params :refer [wrap-params]])
    [ring.util.request :refer [request-url]]
    [yesql.core :refer [defqueries]])
  (:import [java.net URL MalformedURLException]))

(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     (or (System/getProperty "htmlpub.db") (System/getenv "HTMLPUB_DB") "data/htmlpub.db")})

(defqueries "queries.sql")

(defn- json
  ([content] (json content 200))
  ([content status]
   {:status  status
    :headers {"Content-Type" "application/json"}
    :body    (str (with-out-str (json/pprint content)) \newline)}))

(defn- absolute-url [path req]
  (str (URL. (URL. (request-url req)) path)))

(defn- url? [s]
  (try (URL. s) (catch MalformedURLException _)))

(defn- remove-nil [m]
  (into {} (remove (comp nil? second) m)))

(defn- last-insert-rowid [res]
  (get res (keyword "last_insert_rowid()")))

(defn- get-webmentions []
  (json (map remove-nil (select-webmentions db-spec))))

(defn- get-webmention [id]
  (let [[wm] (select-webmention db-spec id)]
    (json (if wm (remove-nil wm) "Not found") (if wm 200 404))))

(defn- handle-incoming [source target req]
  (let [[status resp]
        (cond
          (not (url? source)) [400 "Source is not a valid URL."]
          (not (url? target)) [400 "Target is not a valid URL."]
          :else [202 (-> (insert-webmention<! db-spec source target) (last-insert-rowid) (str) (absolute-url req))])]
    {:status  status
     :headers {"Content-Type" "text/plain"}
     :body    (str resp "\n")}))

(defroutes app-routes
  (GET "/webmention/" [] (get-webmentions))
  (GET "/webmention/:id{[0-9]+}" [id] (get-webmention id))
  (POST "/webmention/" [source target :as req] (handle-incoming source target req))
  (route/not-found "Not found"))

(def app
  (-> app-routes
      (wrap-absolute-redirects)
      (wrap-default-charset "utf-8")
      (wrap-forwarded-remote-addr)
      (wrap-keyword-params)
      (wrap-params)))

(defn init []
  (create-table! db-spec))
