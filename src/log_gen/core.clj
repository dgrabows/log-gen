(ns log-gen.core
  (:gen-class)
  (:require [log-gen.logs :as logs]))

(defn -main
  "Generate access log file based on provided arguments."
  [& args]
  (if-not (and (= 2 (count args))
               (integer? (read-string (first args))))
    (printf "Expected arguments: <event count> <output file path>\n")
    (let [[n-str path] args
          n (read-string n-str)]
      (logs/generate-access-log n path))))
