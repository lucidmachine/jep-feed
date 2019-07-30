(defproject jep-feed "1.0.0"
  :description "An application which renders the JDK Enhancement Proposals index as an RSS feed."
  :url "https://github.com/lucidmachine/jep-feed"
  :license {:name "MIT"
            :url "https://mit-license.org/"}
  :main ^:skip-aot jep-feed.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-auto "0.1.3"]
            [lein-shell "0.5.0"]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.cache "0.7.2"]
                 [org.clojure/data.xml "0.2.0-alpha6"]
                 [reaver "0.1.2"]]
  :aliases {"native" ["shell"
                      "native-image"
                      "--report-unsupported-elements-at-runtime"
                      "--initialize-at-build-time"
                      "-jar" "./target/uberjar/${:uberjar-name:-${:name}-${:version}-standalone.jar}"
                      "-H:Name=./target/${:name}"]})
