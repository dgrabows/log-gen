# log-gen

Utility for generating web server access logs with some attempt made to resemble realistic traffic. The "realistic" part is a work in progress. Useful for testing (or whatever, really).

## Building

[Leiningen](http://leiningen.org/) is required to build.

## Usage

    $ lein uberjar
    $ java -jar target/uberjar/log-gen-0.2.0-standalone.jar <number of log entries> <log output file>

## Example Output

Fields in log lines are tab delimited.

    #date	time	method	uri	status	bytes	ip	uri-query	referer	user-agent	time-taken
    2014-11-08	08:19:10	GET	/images/logo.png	200	1902	192.168.209.24	-	-	"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"	0.807
    2014-11-08	08:19:12	GET	/scripts/customer.js	200	20	192.168.194.211	-	-	"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"	0.087
    2014-11-08	08:19:16	GET	/css/main.css	200	42873	192.168.220.157	-	-	"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1944.0 Safari/537.36"	0.055
    2014-11-08	08:19:26	GET	/customers/5/orders	200	29419	192.168.244.256	-	-	"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"	0.399
    2014-11-08	08:19:35	GET	/customers	200	6978	192.168.187.249	-	-	"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:31.0) Gecko/20100101 Firefox/31.0"	0.343
    2014-11-08	08:19:38	GET	/customers/18/orders	200	73210	192.168.104.62	-	-	"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:31.0) Gecko/20100101 Firefox/31.0"	0.728
    2014-11-08	08:19:49	GET	/customers/5	200	20	192.168.15.264	-	-	"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"	0.116
    2014-11-08	08:20:00	POST	/customers	200	81512	192.168.129.11	-	-	"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:31.0) Gecko/20100101 Firefox/31.0"	0.263
    2014-11-08	08:20:02	POST	/customers/18	200	14268	192.168.263.68	-	-	"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"	0.283

## License

Copyright © 2014 Dan Grabowski

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
