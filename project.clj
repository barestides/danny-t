(defproject danny-t "0.1.0-SNAPSHOT"
  :description "Parallel maze solving algorithm."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [meiro "0.1.0-SNAPSHOT"]]
  :main ^:skip-aot danny-t.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
