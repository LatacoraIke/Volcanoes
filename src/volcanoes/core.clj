(ns volcanoes.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io  :as io]))

(def csv-lines
  (with-open [csv (io/reader "/home/user/volcanoes/resources/GVP_Volcano_List_Holocene.csv")]
    (doall
      (csv/read-csv csv))))

(defn transform-header [header]
  (if (= "Elevation (m)" header)
    :elevation-meters
    (-> header
        clojure.string/lower-case
        (clojure.string/replace #" " "-")
        keyword)))

(defn transform-header-row [header-line]
  (map transform-header header-line))

(def volcano-records
  (let [csv-lines     (rest csv-lines)
        header-line   (transform-header-row (first csv-lines))
        volcano-lines (rest csv-lines)]
    (map (fn [volcano-line]
           (zipmap header-line volcano-line))
         volcano-lines)))

(defn parse-eruption-date [date]
  (if (= "Unknown" date)
    nil
    (let [[_ y e] (re-matches #"(\d+) (.+)" date)]
      (cond
        (= e "BCE")
        (- (Integer/parseInt y))
        (= e "CE")
        (Integer/parseInt y)
        :else
        (throw (ex-info "Could not parse year." {:year date}))))))

(defn parse-numbers [volcano]
  (-> volcano
      (update :elevation-meters #(Integer/parseInt %))
      (update :longitude #(Double/parseDouble %))
      (update :latitude #(Double/parseDouble %))))

(def volcanoes-parsed
  (map parse-numbers volcano-records))

(def types (set (map :primary-volcano-type volcano-records)))

;;run-legnth encoding
(defn run-legnth-encoding [m]
  (map (juxt count first) (partition-by identity m)))

;;drop every nth element
(defn drop-every [n m]
  (mapcat butlast (partition-all n m)))

(comment

  (let [volcano (nth volcanoes-parsed 100)]
    (clojure.pprint/pprint volcano))

  (let [volcano (first (filter #(= "211040" (:volcano-number %)) volcanoes-parsed))]
    (clojure.pprint/pprint volcano))

  )
