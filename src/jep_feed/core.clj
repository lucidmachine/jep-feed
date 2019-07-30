(ns jep-feed.core
  (:require
   [clojure.core.cache :as cache]
   [clojure.data.xml :as xml]
   [reaver]
   [jep-feed.util :as util])
  (:gen-class))

;;
;; Load
;;

(def jep-url-prefix "https://openjdk.java.net/jeps/")
(defn jep-url
  "Builds the URL for a JEP with the given ID."
  [id]
  (str jep-url-prefix id))

(defn download-jep-html
  "Downloads the HTML document for a JEP with the given ID."
  [id]
  (slurp (jep-url id)))


;;
;; Parse
;;


(defn jeps
  "Parses JEPs from the JEPs index into a seq of maps.

  Example:
    (nth (jeps (jep-html 0)) 10)
    ;; => {:id \"106\",
           :name \"Add Javadoc to javax.tools\",
           :type \"Feature\",
           :status \"Closed / Delivered\",
           :release \"8\",
           :component-l \"tools\",
           :component-m \"/\",
           :component-r \"javadoc (tool)\"}"
  [jep-index-html]
  (reaver/extract-from (reaver/parse jep-index-html) ".jeps tr"
                       [:id :name :type :status :release :component-l :component-m :component-r]
                       ;; Extractions: element-selector + transformation-fn
                       "td.jep" reaver/text
                       "td a" reaver/text
                       "td:eq(0) span" (comp
                                        #(util/safe-subs % 6)
                                        (reaver/attr :title))
                       "td:eq(1) span" (comp
                                        #(util/safe-subs % 8)
                                        (reaver/attr :title))
                       "td:eq(2) span" (comp
                                        #(util/safe-subs % 9)
                                        (reaver/attr :title))
                       "td:eq(3)" reaver/text
                       "td:eq(4)" reaver/text
                       "td:eq(5)" reaver/text))


;;
;; Transform
;;


(defn jep->rss-sexp
  "Transforms the given JEP map into a sexp for its corresponding RSS item."
  [{:keys [id name type status release component-l component-m component-r]}]
  [:item
   [:guid (jep-url id)]
   [:title (str "JEP " id ": " name)]
   [:link (jep-url id)]
   [:description (util/html-str "JEP " id ": " name
                                "\nStatus: " status
                                "\nRelease: " release
                                "\nComponent: " component-l component-m component-r)]])

(xml/alias-uri 'atomns "http://www.w3.org/2005/Atom")

(defn jeps->rss-sexp
  "Transforms a seq of JEP maps into a sexp for the RSS feed."
  [jeps]
  [:rss {:version "2.0" :xmlns/atom "http://www.w3.org/2005/Atom"}
   [:channel
    [:title "JDK Enhancement Proposals"]
    [:link (jep-url 0)]
    [::atomns/link {:rel "self" :href  (jep-url 0)}]
    [:description "A regularly-updated list of proposals to serve as the long-term Roadmap for JDK Release Projects and related efforts."]
    (map jep->rss-sexp jeps)]])

(defn jeps->rss
  "Transforms a seq of JEP maps into RSS markup."
  [jeps]
  (-> jeps
      jeps->rss-sexp
      xml/sexp-as-element
      xml/emit-str))


;;
;; Exec
;;


(def one-day-ms (* 1000 60 60 24))
(def jep-rss-cache (atom (cache/ttl-cache-factory {} :ttl one-day-ms)))

(defn fetch-jeps-rss!
  "Download the contents of the JEP index, JEP 0; parse all JEPs from the index;
  transform them into an RSS feed; cache the results for 1 day."
  []
  (util/atomic-through-cache!
   jep-rss-cache
   0
   (-> 0
       download-jep-html
       jeps
       jeps->rss
       constantly)))

(defn -main
  "Prints the JEP index as an RSS feed document."
  [& args]
  (println (fetch-jeps-rss!)))

