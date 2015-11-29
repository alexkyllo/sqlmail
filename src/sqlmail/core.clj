(ns sqlmail.core
  (:gen-class)
  (:require [yesql.core :refer [defqueries]]
            [postal.core :refer [send-message]]
            [hiccup.core :refer [html]]
            [environ.core :refer [env]]
            [clojure.data.csv :refer [write-csv]]
            [clojure.java.io :refer [writer]]
            [schejulure.core :refer [schedule]]
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

(defn mail-html-report
  "Email the results of the query in HTML format"
  [account from to subject report]
  (send-message account {:from from
                         :to to
                         :subject subject
                         :body [{:type "text/html"
                                 :content (vec-to-html
                                           (report {} {:as-arrays? true}))}]}))

(defn mail-csv-report
  "Email the results of the query as an attachment in CSV format"
  [account from to subject report]
  (with-tempfile
    [tf (tempfile
         (with-out-str
           (write-csv *out* (stringify-ids (report {} {:as-arrays? true})))))]
    (send-message account {:from from
                           :to to
                           :subject subject
                           :body [{:type :attachment
                                   :content tf
                                   :file-name "report.csv"}]})))

(defn mail-report
  "Email the results of the query in either HTML or CSV format"
  [query-name query-params mail-acct headers format]
  (case format
    :html
    (send-message
     mail-acct
     (conj
      headers
      {:body [{:type "text/html"
               :content (vec-to-html
                         (query-name
                          query-params
                          {:as-arrays? true}))}]}))
    :csv
    (with-tempfile
      [tf (tempfile
           (with-out-str
             (write-csv
              *out*
              (stringify-ids
               (query-name query-params {:as-arrays? true})))))]
      (send-message
       mail-acct
       (conj
        headers
        {:body [{:type :attachment
                 :content tf
                 :file-name "report.csv"}
                {:type "text/plain"
                 :content "Please see attached report in CSV format."}]})))))

(defn make-scheduled-report
  "query-name   - the name of a yesql-generated query to run
  query-params - a map of parameters to the query
  mail-acct    - a map of smtp account parameters with keys :host :user :pass :ssl for postal
                 (see https://github.com/andrewdavey/postal)
  headers      - the e-mail headers :to :from :subject
  format       - :html (table) or :csv (attachment)
  sched        - a map of times to run the query, in schejulure's DSL
                 (see https://github.com/AdamClements/schejulure)"
  [query-name query-params mail-acct headers format sched]
  (schedule sched #(mail-report query-name query-params mail-acct headers format)))

(defn -main
  "Run to start all scheduled reports."
  [& args]
  ;; schedule reports here using schejulure, e.g.:
  ;; (schedule {:day [:mon :wed] :hour 8 :minute 30} (mail-csv-report ...))
  (make-scheduled-report
   user-count
   {}
   (env :mail-account)
   {:from "alex.kyllo@gmail.com"
    :to "alex.kyllo@gmail.com"
    :subject "Test Scheduled Report"}
   :html
   {}))
