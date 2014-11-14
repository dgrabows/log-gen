(ns log-gen.logs
  (:import (java.net InetAddress)
           (java.util UUID Date))
  [:require [clojure.data.generators :as gen]
            [clojure.math.numeric-tower :as math]
            [clojure.string :as string]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce]
            [clojure.java.io :as io]])

(def example
  ;date time cs-method cs-uri-stem sc-status bytes c-ip cs-uri-query cs(referer) cs(user-agent) time-taken
  {:timestamp #inst "2014-11-15T13:28:15.000-00:00"
   :method "GET"
   :uri "/index.html"
   :status 200
   :bytes 7420
   :ip "192.168.0.1"
   :query "?a=1"
   :referer "http://www.google.com"
   :user-agent "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"
   :time-taken 0.030})

(defn ranged-normal
  "Generates a normally distributed random value with the mean and standard deviation provided.
  Values generated outside the range specified by min and max will be modified to equal min or max."
  [mean stddev min-val max-val]
  (-> (+ mean (* stddev (.nextGaussian gen/*rnd*)))
      Math/rint
      long
      (max min-val)
      (min max-val)))

;(defn gen-private-ipv4
;  "Generates a class C private IPv4 address (192.168.0.0/16).
;  The last two bytes of the address are uniformly distributed."
;  []
;  (InetAddress/getByAddress (byte-array (map byte [192 168 (gen/byte) (gen/byte)]))))

(defn private-ipv4
  "Generates a class C private IPv4 address (192.168.0.0/16).
  The last two bytes of the address are uniformly distributed."
  []
  (str "192.168." (gen/uniform 0 266) "." (gen/uniform 0 266)))


(defn access-event
  []
  {:timestamp (gen/date #inst "2014-11-15T13:28:15.000-00:00")
   :method (gen/weighted {"GET" 4 "POST" 1})
   :uri "/index.html"
   :status (gen/weighted {200 7500
                          302 250
                          500 15
                          401 10
                          404 1})
   :bytes (ranged-normal 10000 30000 0 1000000)
   :ip (private-ipv4)
   :query "?a=1"
   :referer nil
   :user-agent (gen/one-of "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"
                           "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"
                           "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36"
                           "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1944.0 Safari/537.36"
                           "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"
                           "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:31.0) Gecko/20100101 Firefox/31.0")
   :time-taken 0.030})