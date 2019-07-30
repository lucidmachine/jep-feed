(ns jep-feed.util
  (:require
   [clojure.core.cache :as cache]
   [clojure.string :as string]))

(defn safe-subs
  "subs, but it won't throw a NullPointerException if the input isn't a string."
  ([] nil)
  ([maybe-str] maybe-str)
  ([maybe-str start]
   (if (string? maybe-str)
     (subs maybe-str start)))
  ([maybe-str start end]
   (if (string? maybe-str)
     (subs maybe-str start end))))

(def html-str
  (comp
   #(string/escape % {\< "&lt;"
                      \> "&gt;"
                      \& "&amp;"
                      \" "&quot;"})
   str))

(defn atomic-through-cache!
  "If the given atom-wrapped cache already contains a value for the given key,
  returns the value cached at that key. Otherwise, evaluates the value function,
  updates the cache with the generated key value pair, and returns that value
  from the cache.

  Arguments:
  * `atomic-cache` - A cache object wrapped in an atom.
  * `k` - The cache key. The value cached at this key will be returned if one
    exists. Otherwise, the value of the value function `v-fn` will be placed at
    this key.
  * `v-fn` - A function which, when evaluated, returns the value to be placed in the
    cache. Note that this will not be evaluated if the key `k` already has a
    value in the cache."
  [atomic-cache k v-fn]
  (get
   (swap! atomic-cache cache/through-cache k v-fn)
   k))
