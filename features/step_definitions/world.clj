(ns world)

(def ^:private the-world (atom {:result nil}))

(defn value [] @the-world)

(defn reset-world!
  ([] (reset! the-world {:result nil}))
  ([newval]
   (reset! the-world newval)))

(defn swap-world!
  ([f x]
   (swap! the-world f x))
  ([f x y]
   (swap! the-world f x y))
  ([f x y & args]
   (swap! the-world f x y args)))