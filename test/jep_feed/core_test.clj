(ns jep-feed.core-test
  (:require [clojure.test :refer :all]
            [jep-feed.core :refer :all]))

;;
;; Load
;;

(deftest jep-url-test
  (testing "URL construction"
    (is (=
         "https://openjdk.java.net/jeps/1234"
         (jep-url 1234)))))

;;
;; Parse
;;

(def two-jeps '({:id "1",
                 :name "JDK Enhancement-Proposal & Roadmap Process"
                 :type "Process"
                 :status "Active"
                 :release nil
                 :component-l ""
                 :component-m ""
                 :component-r ""}
                {:id "105"
                 :name "DocTree API"
                 :type "Feature"
                 :status "Closed / Delivered"
                 :release "8"
                 :component-l "tools"
                 :component-m "/"
                 :component-r "javac"}))

(deftest jeps-test
  (testing "HTML index parsing"
    (let [two-jeps-html "<html><body><table class=\"jeps\"><tbody>
                           <tr> <td><span xmlns=\"\" title=\"Type: Process\">P</span></td> <td><span xmlns=\"\" title=\"Status: Active\">Act</span></td> <td></td> <td class=\"cm\"></td> <td class=\"cm\"></td> <td class=\"cm\"></td> <td class=\"jep\">1</td> <td><a href=\"1\">JDK Enhancement-Proposal &amp; Roadmap Process</a></td> </tr>
                           <tr> <td><span xmlns=\"\" title=\"Type: Feature\">F</span></td> <td><span xmlns=\"\" title=\"Status: Closed / Delivered\">Clo</span></td> <td><span xmlns=\"\" title=\"Release: 8\">8</span></td> <td xmlns=\"\" class=\"cl\">tools</td> <td xmlns=\"\" class=\"cm\">/</td> <td xmlns=\"\" class=\"cr\">javac</td> <td class=\"jep\">105</td> <td><a href=\"105\">DocTree API</a></td> </tr>
                         </tbody></table></body></html>"]
      (is (=
           two-jeps
           (jeps two-jeps-html))))))


;;
;; Transform
;;


(deftest jep->rss-sexp-test
  (testing "Single JEP RSS templating"
    (let [jep-105 (last two-jeps)]
      (is (=
           [:item
            [:guid "https://openjdk.java.net/jeps/105"]
            [:title "JEP 105: DocTree API"]
            [:link "https://openjdk.java.net/jeps/105"]
            [:description "JEP 105: DocTree API\nStatus: Closed / Delivered\nRelease: 8\nComponent: tools/javac"]]
           (jep->rss-sexp jep-105))))))

(deftest jeps->rss-sexp-test
  (testing "RSS full feed templating"

    (testing "channel metadata"
      (is (=
           [:rss
            {:version "2.0", :xmlns/atom "http://www.w3.org/2005/Atom"}
            [:channel
             [:title "JDK Enhancement Proposals"]
             [:link "https://openjdk.java.net/jeps/0"]
             [:xmlns.http%3A%2F%2Fwww.w3.org%2F2005%2FAtom/link {:rel "self", :href "https://openjdk.java.net/jeps/0"}]
             [:description "A regularly-updated list of proposals to serve as the long-term Roadmap for JDK Release Projects and related efforts."]
             ()]]
           (jeps->rss-sexp '()))))

    (testing "multiple items"
      (is (=
           [:rss
            {:version "2.0", :xmlns/atom "http://www.w3.org/2005/Atom"}
            [:channel
             [:title "JDK Enhancement Proposals"]
             [:link "https://openjdk.java.net/jeps/0"]
             [:xmlns.http%3A%2F%2Fwww.w3.org%2F2005%2FAtom/link {:rel "self", :href "https://openjdk.java.net/jeps/0"}]
             [:description "A regularly-updated list of proposals to serve as the long-term Roadmap for JDK Release Projects and related efforts."]
             '([:item
                [:guid "https://openjdk.java.net/jeps/1"]
                [:title "JEP 1: JDK Enhancement-Proposal & Roadmap Process"]
                [:link "https://openjdk.java.net/jeps/1"]
                [:description "JEP 1: JDK Enhancement-Proposal &amp; Roadmap Process\nStatus: Active\nRelease: \nComponent: "]]
               [:item
                [:guid "https://openjdk.java.net/jeps/105"]
                [:title "JEP 105: DocTree API"]
                [:link "https://openjdk.java.net/jeps/105"]
                [:description "JEP 105: DocTree API\nStatus: Closed / Delivered\nRelease: 8\nComponent: tools/javac"]])]]
           (jeps->rss-sexp two-jeps))))))
