(defproject log-gen "0.2.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [clj-time "0.8.0"]
                 [org.apache.commons/commons-math3 "3.2"]]
  :main ^:skip-aot log-gen.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
