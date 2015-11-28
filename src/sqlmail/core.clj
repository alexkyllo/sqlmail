(ns sqlmail.core
  (:gen-class)
  (:require [yesql.core :refer [defqueries]]
            [postal.core :refer [send-message]]
            [schejulure.core :refer [schedule]]
            [hiccup.core :refer [html]]
            [environ.core :refer [env]]
            [clojure.data.csv :refer [write-csv]]
            [clojure.java.io :refer [writer]]
            [tempfile.core :refer [tempfile with-tempfile]]))

(defqueries "sql/queries.sql" {:connection (env :db-conn)})

(defn map-to-html [data]
  (html [:table
         [:tr (for [[k v] (first data)]
                [:th k])]
         (for [row data]
           [:tr
            (for [[k v] row]
              [:td v])])]))

(defn vec-to-html [data]
  (html [:table
         [:tr
          (for [h (first data)]
            [:th h])]
         (for [row (rest data)]
           [:tr
            (for [cell row]
              [:td cell])])]))

(defn stringify-ids [v]
  (vec (cons (mapv name (first v)) (rest v))))

(defn mail-html-report [account from to subject report]
  (send-message account {:from from
                         :to to
                         :subject subject
                         :body [{:type "text/html"
                                 :content (vec-to-html
                                           (report {} {:as-arrays? true}))}]}))

(defn mail-csv-report [account from to subject report]
  (with-tempfile [tf
                  (tempfile
                   (with-out-str
                     (write-csv *out* (stringify-ids (report {} {:as-arrays? true})))))]
    (send-message account {:from from
                           :to to
                           :subject subject
                           :body [{:type :attachment
                                   :content tf
                                   :file-name "report.csv"}]})))

(defn -main
  "main"
  [report & args]
  (mail-html-report (to-html (report))))
