(defproject sqlmail "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [yesql "0.5.1"]
                 ;;[com.h2database/h2 "1.4.188"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [com.draines/postal "1.11.3"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [schejulure "1.0.1"]
                 [hiccup "1.0.5"]
                 [environ "1.0.1"]]
  :main ^:skip-aot sqlmail.core
  :target-path "target/%s"
  :plugins [[cider/cider-nrepl "0.9.1"]
            [lein-environ "1.0.1"]]
  :profiles {:uberjar {:aot :all}})
