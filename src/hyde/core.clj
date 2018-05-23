(ns hyde.core
  (:require [clojure.data.json :as json]
            [slugger.core :as slugger])
  (:gen-class))







(defn extract-render-data [{beschreibung :description
                            titel :name
                            {ort :name} :venue
                            datum :time
                            eintrag :updated
                            link :link
                            id :id
                            status :status
                            time :local_time}]
  {:ort ort
   :id id
   :link link
   :eintrag (java.util.Date. eintrag)
   :datum (java.util.Date. datum)
   :titel titel
   :status (keyword status)
   :beschreibung beschreibung
   :zeit time})

(defn render-next-event [{:keys [beschreibung eintrag ort link titel datum status]}]

  (format "
layout: featured
date: %1$tY-%1$tm-%1$td 14:55:05 +0100
title: \"%2$s\"
postlink: %4$s
meetuplink: %3$s
"
          datum
          titel
          link
          (format "%1$tY-%1$tm-%1$td-%2$s.markdown" datum (slugger/->slug titel))))


(defn render [{:keys [beschreibung eintrag ort link titel datum status zeit]}]

  (format "---
layout: post
date: %1$tY-%1$tm-%1$td 14:55:05 +0100
title: \"%2$s\"
meetuplink: \"%3$s\"
categories: rheinjug event
status: \"%7$s\"
ort: \"%4$s\"
zeit: \"%6$s\"
---
%5$s
"
          datum
          titel
          link
          ort
          beschreibung
          zeit
          (name status)))

(def data (json/read-str
           (slurp "https://api.meetup.com/rheinjug/events?status=upcoming,past")
           :key-fn keyword))

(def next-event (-> (slurp "https://api.meetup.com/rheinjug/events?page=1")
                    (json/read-str ,,, :key-fn keyword)
                    first
                    extract-render-data
                    render-next-event))


(defn -main
  [& args]
  (doseq [{datum :datum titel :titel :as event} (map extract-render-data data)]
    (spit (format "_posts/%1$tY-%1$tm-%1$td-%2$s.markdown" datum (slugger/->slug titel)) (render event)))
  (spit "_data/featured.yml" next-event)
  :done)


(comment

  (-main)

  (take 3 data)

  (slugger/->slug " i suck at this")

  (extract-render-data(first data))
  )
