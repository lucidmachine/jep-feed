(ns jep-feed.util-test
  (:require
   [jep-feed.util :refer :all]
   [clojure.test :refer :all]
   [clojure.core.cache :as cache]))

(deftest safe-subs-test
  (testing "0 arity returns nil"
    (is (=
         nil
         (safe-subs))))
  (testing "1 arity returns the input"
    (is (=
         "foo"
         (safe-subs "foo"))))
  (testing "2 arity"
    (testing "when the 1st arg is a string, returns the substring beginning at start inclusive"
      (is (=
           "oo"
           (safe-subs "foo" 1))))
    (testing "when the 1st arg is not a string, returns nil"
      (is (=
           nil
           (safe-subs [] 1)))))
  (testing "3 arity"
    (testing "when the 1st arg is a string, returns the substring beginning at start inclusive and ending at end"
      (is (=
           "o"
           (safe-subs "foo" 1 2))))
    (testing "when the 1st arg is not a string, returns nil"
      (is (=
           nil
           (safe-subs [] 1 2))))))

(deftest html-str-test
  (testing "replaces select nasty characters with their HTML entities"
    (is (=
         "&lt; &gt; &amp; &quot;"
         (html-str "< > & \"")))))

(deftest atomic-through-cache!-test
  (testing "when the given key is not present, evaluates the value function, inserts the value in the cache, and returns that value"
    (let [test-cache (atom (cache/ttl-cache-factory {} :ttl 9001))]
      (is (=
           "bar"
           (atomic-through-cache! test-cache :foo (constantly "bar"))))))

  (testing "when the given key is present, does not update the cache and returns the cached value"
    (let [test-cache (atom (cache/ttl-cache-factory {:foo "baz"} :ttl 9001))]
      (is (=
           "baz"
           (atomic-through-cache! test-cache :foo (constantly "bar")))))))
