TRON: Task Run ON

TRON lets you schedule tasks for execution using a Java
ScheduledThreadPoolExecutor under the hood. The name is
to make the parallel with UNIX's CRON a bit more evident.

## Usage

TRON exposes very few functions, here is a small example:

    (ns sandbox
      (:require [tr.on :as tron]))

    (defn- periodic [] (println "periodic"))
    (defn- ponctual [] (println "ponctual"))

    ;; Run the fonction 10 seconds from now
    (tron/once ponctual 10000) 

    ;; Run the periodic function every second
    ;; after the last call
    (tron/periodically :foo periodic 1000)

    ;; Run the periodic function every 10 second
    (tron/fixed-periodically :foo periodic 10000)

    ;; Cancel the periodic run 5 seconds from now
    (tron/once #(tron/cancel :foo) 5000)
    
    ;; Execute the followin expressions periodically
    (tron/do-periodically 5000
      (println "hello"))

## Installation

The easiest way to use TRON in your own projects is via Leiningen.
Add the following dependency to your project.clj file:

    [spootnik/tron   "0.5.4"]

If you would rather use maven, you need to specify the clojars
repository dependency and import the tron artifact

    <repository>
        <id>clojars.org</id>
        <url>http://clojars.org/repo</url>
    </repository>
     
    <dependency>
        <groupId>spootnik</groupId>
        <artifactId>tron</artifactId>
        <version>0.5.4</version>
    </dependency>

## License

Copyright (C) 2011 Pierre-Yves Ritschard

Distributed under the MIT License

Permission to use, copy, modify, and distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
