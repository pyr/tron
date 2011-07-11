;; TRON: Task Run ON
;;
;; Simple library to schedule jobs on a Java ScheduledThreadPoolExecutor.
;; Jobs can be scheduled for recurrent execution or for single-time runs.
;; Jobs can have nicknames to be later decomissioned.
;;
(ns tron
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit)))

(defonce
  ^{:private  true
    :doc      "Number of threads for the scheduler."}
  poolsize    (atom 1))

(defonce
  ^{:private  true
    :doc      "Thread pool for the scheduler."}
  pool        (atom nil))

(defonce
  ^{:private  true
    :doc      "Running tasks on the scheduler."}
  tasks       (atom {}))

(defonce
  ^{:private  true
    :doc      "Shortcut for milliseconds."}
  usecs       TimeUnit/MILLISECONDS)

(defn- get-pool
  "Initiate a thread-pool for periodic tasks."
  []
  (swap! pool #(or % (ScheduledThreadPoolExecutor. @poolsize))))

(defn periodically
  "Schedule a function for periodic execution.
  There are four different calling modes and three
  arities to this function:

  * `nickname f init delay`: provide a nickname to the task
    for later removal. the task will be executed after an
    initial delay and will recur every delay milliseconds.
  * `nickname f delay`: execute the same steps, but launch
    the first occurence right away.
  * `f init delay`: do not provide a nickname for this tasks
  * `f delay`: do not provide a nickname and start the first
    occurence right away."
  ([nickname f init delay]
   (let [task   (.scheduleWithFixedDelay (get-pool) f init delay usecs)]
     (swap! tasks assoc nickname task)))
  ([arg1 arg2 arg3]
   (if (= (class arg1) clojure.lang.Keyword)
     (let [nickname arg1, f arg2, delay arg3]
       (periodically nickname f 0 delay))
     (let [f arg1, init arg2, delay arg3]
       (.scheduleWithFixedDelay (get-pool) f init delay usecs))))
  ([f delay]
   (periodically f 0 delay)))

(defn once
  "Schedule a function for one-time execution. Optionnaly
  provide a nickname for removal."
  ([nickname f delay]
   (let [cb   (fn [] (f) (swap! tasks dissoc nickname))
         task (.schedule (get-pool) cb (long delay) usecs)]
     (swap! tasks assoc nickname task)))
  ([f delay]
   (.schedule (get-pool) f (long delay) usecs)))

(defn shutdown
  "Terminate all scheduled tasks."
  []
  (swap! pool (fn [p] (if p (.shutdown p)) nil)))

(defn cancel
  "Remove a task from the scheduled thread pool."
  [nickname]
  (when-let [task  (@tasks nickname)]
    (swap! tasks dissoc nickname)
    (doto (get-pool)
      (.remove task))))

(defn init
  "Set the number of periodic tasks which can be executed simultaneously."
  ([number]
   (shutdown)
   (swap! poolsize (fn [_] number)))
  ([]
   (init 1)))
