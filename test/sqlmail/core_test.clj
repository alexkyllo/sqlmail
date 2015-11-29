(ns sqlmail.core-test
  (:require [clojure.test :refer :all]
            [sqlmail.core :refer :all]))

(deftest test-map-to-html
  (testing "can convert vector of maps to html table"
    (is (=
         (map-to-html [{:one 1 :two 2 :three 3} {:one "a" :two "b" :three "c"}])
         (str "<table><tr><th>one</th><th>two</th><th>three</th></tr>"
              "<tr><td>1</td><td>2</td><td>3</td></tr>"
              "<tr><td>a</td><td>b</td><td>c</td></tr></table>")))))

(deftest test-vec-to-html
  (testing "can convert a vector of vectors to html table"
    (is (=
         (vec-to-html [[:one :two :three] [1 2 3] ["a" "b" "c"]])
         (str "<table><tr><th>one</th><th>two</th><th>three</th></tr>"
              "<tr><td>1</td><td>2</td><td>3</td></tr>"
              "<tr><td>a</td><td>b</td><td>c</td></tr></table>")))))

(deftest test-stringify-ids
  (testing "can stringify symbols in header row returned from query"
    (is (=
         (stringify-ids [[:one :two :three] [1 2 3] ["a" "b" "c"]])
         [["one" "two" "three"] [1 2 3] ["a" "b" "c"]]))))

(deftest test-the-mailer-mails
  (testing "can mail mail"
    (is (=
         (mail-report user-count {} {} {:from "foo@example.com" :to "bar@example.com" :subject "test"} :html)
         {:code 0, :error :SUCCESS, :message "message sent"}))))

(deftest test-the-scheduler-schedules
  (is (=
       (type (make-scheduled-report (constantly nil) {} {} {} :html {}))
       java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask)))
