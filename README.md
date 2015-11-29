# sqlmail

SQLMail is a simple Clojure application for e-mailing scheduled reports using SQL. It might be the simplest "reporting server" in the world. It is just yesql + postal + schejulure.

## Installation

Clone the project
git clone git@github.com:alexkyllo/sqlmail.git
Add a profiles.clj to define your database connection and e-mail account
Then edit resources/sql/queries.sql and src/sqlmail/core.clj to define and schedule your reports

## Usage

Run the jar and it will start a process that schedules all the reports defined in sqlmail.core/-main

    $ java -jar sqlmail-0.1.0-standalone.jar [args]

## Defining queries and reports

```clojure

;; in src/sqlmail/core.clj

;; define a set of queries stored in resources/sql/queries.sql using yesql
(defqueries "sql/queries.sql" {:connection (env :db-conn)})

;; define a report to schedule using schejulure
(defn -main
  "Run to start all scheduled reports."
  [& args]
  ;; one report
  (make-scheduled-report
    my-query
    {}
    (env :mail-account)
    {:from "foo@example.com"
     :to "bar@example.com"
     :subject "Test Scheduled Report"}
     :html
     {})
  ;; another report
  (make-scheduled-report
    my-query
    {}
    (env :mail-account)
    {:from "foo@example.com"
     :to "bar@example.com"
     :subject "Test Scheduled Report"}
     :html
     {}))
```


## License

Copyright © 2015 Alex Kyllo

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
