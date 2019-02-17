(ns hyde.mediathek
  (:require [clojure.xml :as xml]
            [hiccup.core :as h]
            [clj-time.format :as f]))

(defn- single-group-by
  "Like group-by but only returns a single value per key"
  [f coll]
  (reduce (fn [m e] (assoc m (f e) e)) {} coll))

(defn- get-field [entry k]
  (first (:content (get entry k))))

(defn- normalize-entry [entry]
  (let [field (partial get-field entry)]
    {:id         (last (.split (field :guid) "/"))
     :title      (field :title)
     :subtitle   (field :itunes:subtitle)
     :thumbnail  (-> entry :media:thumbnail :attrs :url)
     :duration   (field :itunes:duration)
     :author     (field :author)
     :link       (field :link)
     :published  (f/parse (f/formatters :rfc822) (field :pubDate))
     :categories (-> entry :category :content)
     :summary    (field :itunes:summary)}))

(defn- parse-entries [data]
  (-> data :content first :content
    (->> (filter #(= :item (:tag %)))
      (map :content)
      (map (partial single-group-by :tag))
      (map normalize-entry))))

(defn- render-entry [{:keys [id duration title author summary categories thumbnail link subtitle published]}]
  [:li.entry {:id id}
   [:a {:href link}
    [:img.thumbnail {:src thumbnail}]]
   [:h4.title title]
   (when subtitle [:h5.subtitle subtitle])
   [:p.author author]
   [:p.published (f/unparse (f/formatter "dd.MM.yyyy") published)]
   (when summary [:p.summary summary])])

(defn- render [entries]
  [:ol.entries.list-group (->> entries
                            (sort-by :published) reverse
                            (map render-entry))])

(defn build-mediathek
  "Retrieve XML feed from mediathek "
  ([output-to]
   (let [url "https://mediathek.hhu.de/rss/7/feed/"]
     (->> url xml/parse parse-entries render h/html (spit output-to))))
  ([]
   (build-mediathek "_includes/mediathek_entries.html")))
