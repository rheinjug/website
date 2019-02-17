(ns hyde.core
  (:gen-class)
  (:require [hyde.meetups :as meetups]
            [hyde.mediathek :as mediathek]))

(defn -main [& args]
  (meetups/build-meetup-entries)
  (mediathek/build-mediathek))

(-main)
