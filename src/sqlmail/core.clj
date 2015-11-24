(ns sqlmail.core
  (:gen-class)
  (:require [yesql.core :refer [defqueries]]
            [postal.core :refer [send-message]]
            [schejulure.core :refer [schedule]]
            [hiccup.core :refer [html]]
            [environ.core :refer [env]]))

(defqueries "sql/queries.sql" {:connection (env :db-conn)})

(defn to-html [results]
  (html [:table
         [:tr (for [[k v] (first results)]
                [:th k])]
         (for [row results]
           [:tr
            (for [[k v] row]
              [:td v])])]))

(defn mail-html-report [from to subject report spec]
  (send-message spec {:from from
                      :to to
                      :subject subject
                      :body [{:type "text/html" :content (to-html (report))}]}))

(defn -main
  "main"
  [& args]
  (println "Hello, World!"))
