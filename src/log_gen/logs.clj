(ns log-gen.logs
  (:import (java.util UUID Date)
           org.apache.commons.math3.distribution.LogNormalDistribution)
  [:require [clojure.data.generators :as gen]
            [clojure.math.numeric-tower :as math]
            [clojure.string :as string]
            [clj-time.core :as time]
            [clj-time.coerce :as time.coerce]
            [clj-time.format :as time.format]
            [clojure.java.io :as io]])

(def example
  {:timestamp  #inst "2014-11-15T13:28:15.000-00:00"
   :method     "GET"
   :uri        "/index.html"
   :status     200
   :bytes      7420
   :ip         "192.168.0.1"
   :query      "?a=1"
   :referer    "http://www.google.com"
   :user-agent "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"
   :time-taken 0.030})

(def ^LogNormalDistribution log-normal-distribution
  (let [ln-mean (+ 1.0 (Math/log 100))
        ln-stddev (* 0.2 ln-mean)]
    (LogNormalDistribution. ln-mean ln-stddev)))

(defn log-normal
  [max-val]
  (long (min (.sample log-normal-distribution) max-val)))

(defn ranged-normal
  "Generates a normally distributed random value with the mean and standard deviation provided.
  Values generated outside the range specified by min and max will be modified to equal min or max."
  [mean stddev min-val max-val]
  (-> (+ mean (* stddev (.nextGaussian gen/*rnd*)))
      Math/rint
      long
      (max min-val)
      (min max-val)))

(defn private-ipv4
  "Generates a class C private IPv4 address (192.168.0.0/16).
  The last two bytes of the address are uniformly distributed."
  []
  (str "192.168." (gen/uniform 0 266) "." (gen/uniform 0 266)))

(defn date-in-range
  "Generate a random date with uniform distribution in the range low (inclusive) to high (exclusive)."
  [low high]
  (time.coerce/from-long (gen/uniform (time.coerce/to-long low) (time.coerce/to-long high))))

(def index-spec
  (let [ln-mean (+ 1.0 (Math/log 50))
        ln-stddev (* 0.15 ln-mean)
        log-norm-dist (LogNormalDistribution. ln-mean ln-stddev)]
    {:method     #(identity "GET")
     :uri        #(identity "/index.html")
     :status     #(gen/weighted {200 1000 500 1})
     :bytes      #(identity 2304)
     :time-taken #(/ (long (min (.sample log-norm-dist) 8000)) 1000.0)}))

(defn spec-access-event
  [spec]
  {:timestamp  (date-in-range (time/minus (time/now) (time/days 7)) (time/now))
   :method     ((:method spec))
   :uri        ((:uri spec))
   :status     ((:status spec))
   :bytes      ((:bytes spec))
   :ip         (private-ipv4)
   :query      nil
   :referer    nil
   :user-agent (gen/one-of "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"
                           "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"
                           "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36"
                           "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1944.0 Safari/537.36"
                           "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"
                           "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:31.0) Gecko/20100101 Firefox/31.0")
   :time-taken ((:time-taken spec))})

(defn access-event
  [f-ip]
  {:timestamp  (date-in-range (time/minus (time/now) (time/days 7)) (time/now))
   :method     (gen/weighted {"GET" 4 "POST" 1})
   :uri        (gen/weighted {"/index.html"          1
                              "/customers"           10
                              "/customers/5"         3
                              "/customers/18"        5
                              "/customers/32"        1
                              "/customers/5/orders"  2
                              "/customers/18/orders" 4
                              "/images/logo.png"     1
                              "/scripts/app.js"      1
                              "/scripts/customer.js" 1
                              "/css/main.css"        1
                              "/css/customer.css"    1})
   :status     (gen/weighted {200 7500
                              500 15
                              401 10
                              404 1})
   :bytes      (ranged-normal 10000 30000 20 1000000)
   :ip         (f-ip)
   :query      nil
   :referer    nil
   :user-agent (gen/one-of "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"
                           "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"
                           "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36"
                           "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1944.0 Safari/537.36"
                           "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"
                           "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:31.0) Gecko/20100101 Firefox/31.0")
   :time-taken (/ (log-normal 30000) 1000.0)})

(defn gen-generator [n]
  (let [ips (vec (gen/reps (/ n 500) private-ipv4))
        ip-count (count ips)
        mean-nth (/ ip-count 2)
        stddev-nth (int (* mean-nth 0.05))
        gen-ip #(nth ips (ranged-normal mean-nth stddev-nth 0 (dec ip-count)))]
    #(access-event gen-ip)))

(defn generate-access-log
  "Generates an access log file containing n reqeuests spread over 7 days. The file is written to path."
  [n path]
  (time
    ;(let [ips (vec (gen/reps (/ n 500) private-ipv4))
    ;      gen-ip #(nth ips (ranged-normal 500 200 0 (dec (count ips))))]
    (let [event-generator (gen-generator n)]
      (with-open [log-file (io/writer path)]
        (.write log-file "#date\ttime\tmethod\turi\tstatus\tbytes\tip\turi-query\treferer\tuser-agent\ttime-taken\n")
        (doseq [event (gen/reps n event-generator)]
          (.write log-file
                  (format "%s\t%s\t%s\t%s\t%d\t%d\t%s\t%s\t%s\t%s\t%.3f\n"
                          (time.format/unparse (:year-month-day time.format/formatters) (:timestamp event))
                          (time.format/unparse (:hour-minute-second time.format/formatters) (:timestamp event))
                          (:method event)
                          (:uri event)
                          (:status event)
                          (:bytes event)
                          (:ip event)
                          (or (:query event) "-")
                          (if (:referer event) (str "\"" (:referer event) "\"") "-")
                          (if (:user-agent event) (str "\"" (:user-agent event) "\"") "-")
                          (:time-taken event))))))))