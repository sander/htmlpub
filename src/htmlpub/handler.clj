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
  (:import [java.net URL]))

(def db-spec
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "data/htmlpub.db"})

(defqueries "queries.sql")

(defn- json
  ([content] (json content 200))
  ([content status]
   {:status  status
    :headers {"Content-Type" "application/json"}
    :body    (str (with-out-str (json/pprint content)) \newline)}))

(defn- absolute-url [path req]
  (str (URL. (URL. (request-url req)) path)))

(defn- get-webmentions []
  (json (select-webmentions db-spec)))

(defn- get-webmention [id]
  (let [[wm] (select-webmention db-spec id)]
    (json (or wm "Not found") (if wm 200 404))))

(defn- handle-incoming [source target req]
  (let [id (-> (insert-webmention<! db-spec source target)
               (get (keyword "last_insert_rowid()")))]
    {:status  202
     :headers {"Content-Type" "text/plain"}
     :body    (absolute-url (str id) req)}))

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