(ns volcanoes.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io  :as io]))

(def csv-lines
  (with-open [csv (io/reader "/home/user/volcanoes/resources/GVP_Volcano_List_Holocene.csv")]
    (doall
      (csv/read-csv csv)))
  )
(def volcano-records
  (let [csv-lines     (rest csv-lines)
        header-line   (first csv-lines)
        volcano-lines (rest csv-lines)]
    (map (fn [volcano-line]
           (zipmap header-line volcano-line))
         volcano-lines))
  )
