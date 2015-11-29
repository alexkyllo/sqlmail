# sqlmail

SQLMail is a simple Clojure application for e-mailing scheduled reports using SQL. It might be the simplest "reporting server" in the world. It is just [yesql](https://github.com/krisajenkins/yesql) + [postal](https://github.com/drewr/postal) + [schejulure](https://github.com/AdamClements/schejulure).

## Installation

### Use as an application
Clone the project

`git clone git@github.com:alexkyllo/sqlmail.git`

Add a profiles.clj to define your database connection and e-mail account

Then edit resources/sql/queries.sql and src/sqlmail/core.clj to define and schedule your reports

### Use as a library
Add this to your Leiningen dependencies in project.clj:

[![Clojars Project](http://clojars.org/sqlmail/latest-version.svg)](http://clojars.org/sqlmail)

First put your queries in a .sql file and bind them to functions using yesql.
Optionally, use [environ](https://github.com/weavejester/environ) to keep your database
credentials outside of your source code:

```clojure
(defqueries "sql/queries.sql" {:connection (env :db-spec)})
```

Then, just use the `make-scheduled-report` function within your application to start a scheduled report:

```clojure
(def my-report
  (make-scheduled-report
    user-count ;; name of yesql query function to run
    {} ;; map of parameters to pass to the query
    (env :mail-account) ;; smtp account with keys :host :user :pass
    {:from "foo@example.com" :to "bar@example.com" :subject "a cool report"} ;; e-mail headers
    :html ;; format: either :html or :csv
    {:hour 8 :minute 30 :day :weekdays})) ;; schedule in schejulure DSL
```

This will immediately start a future `java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask`.

To cancel the report:

```clojure
(future-cancel my-report)
```
Note that the scheduler is single-threaded, so if one of your reports takes a long time to run it may block another report from running.

## Usage

Run the jar and it will start a process that schedules all the reports defined in sqlmail.core/-main

    $ java -jar sqlmail-0.1.0-standalone.jar [args]

## Defining queries and reports

```clojure

;; in src/sqlmail/core.clj

;; define a set of queries stored in resources/sql/queries.sql using yesql
(defqueries "sql/queries.sql" {:connection (env :db-spec)})

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
     :subject "Test Scheduled HTML Report"}
     :html
     {})
  ;; another report
  (make-scheduled-report
    my-other-query
    {}
    (env :mail-account)
    {:from "foo@example.com"
     :to "bar@example.com"
     :subject "Test Scheduled CSV Report"}
     :csv
     {}))
```

Then either `lein run` or `lein jar` and run the jar using java to start the report server running.

## License

Copyright © 2015 Alex Kyllo

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
