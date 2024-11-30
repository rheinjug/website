(ns hyde.mediathek
  (:require [clojure.xml :as xml]
            [hiccup.core :as h]
            [clj-time.format :as f]
            [clojure.string :as string]))

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

(defn- truncate-string [s]
  (let [words (string/split s #"\s+")
        truncated (string/join " " (take 25 words))]
    (if (< 25 (count words))
      (str truncated "...")
      truncated)))

(defn- render-entry [{:keys [id duration title author summary categories thumbnail link subtitle published]}]
  [:div.row.entry {:id id
                   :style "margin-bottom: 2rem;"}
   [:div.col-4
    [:a {:href link}
     [:img.thumbnail {:src thumbnail :alt (str "Link zum Vortrag " title)}]]]
   [:div.col-8
    [:a {:href link}
     [:h5.title title]]
    (when subtitle [:h6.subtitle subtitle])
    [:span.published.float-right.small
     (f/unparse (f/formatter "dd.MM.yyyy") published)]
    [:p.author author]
    (when summary
      [:p.summary (truncate-string summary)])
    [:a.btn.btn-light {:href link} "Zum Video"]]])

(defn- render [entries]
  [:div.entries.list-group
   (->> entries
        (sort-by :published)
        reverse
        (map render-entry))])

(defn build-mediathek
  "Retrieve XML feed from mediathek "
  ([output-to output-latest-to]
   (let [url "https://mediathek.hhu.de/rss/7/feed/"
         entries (parse-entries (xml/parse url))]
     (->> (take 3 entries) render h/html (spit output-latest-to))
     (->> entries render h/html (spit output-to))))
  ([]
   (build-mediathek "jekyll/_includes/mediathek_entries.html" "jekyll/_includes/mediathek_entries_latest.html")))
