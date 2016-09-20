(ns world)

(def ^:private the-world (atom {}))

(defn value [] @the-world)

(defn response [] (@the-world :response))

(defn response-body []
  (str ((@the-world :response) :body)))

(defn response-status []
  ((@the-world :response) :status))

(defn reset-world!
  ([] (reset! the-world {}))
  ([newval]
   (reset! the-world newval)))

(defn swap-world!
  ([f x]
   (swap! the-world f x))
  ([f x y]
   (swap! the-world f x y))
  ([f x y & args]
   (swap! the-world f x y args)))
