(defproject hyde "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-time "0.15.1"]
                 [hiccup "1.0.5"]
                 [slugger "1.0.1"]]
  :main ^:skip-aot hyde.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
