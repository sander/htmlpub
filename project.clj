(defproject

  htmlpub
  "0.2.0"

  :description
  "Static website stuff"

  :dependencies
  [[org.clojure/clojure "1.7.0"]
   [org.clojure/data.json "0.2.6"]
   [org.clojure/java.jdbc "0.4.1"]
   [compojure "1.4.0"]
   [ring/ring-headers "0.1.3"]
   [org.xerial/sqlite-jdbc "3.8.10.2"]
   [yesql "0.4.2"]]

  :plugins
  [[lein-ring "0.9.6"]]

  :ring
  {:init htmlpub.handler/init
   :handler htmlpub.handler/app})
