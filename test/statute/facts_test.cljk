(ns statute.facts-test
  (:require [kotoba.lang.text :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest nru-has-spec-basis
  (let [sb (facts/spec-basis "NRU")]
    (is (= 7 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["NRU" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["nru.corporations-act-1972"]
         (mapv :statute/id (facts/by-topic "NRU" :incorporation))))
  (is (= 2 (count (facts/by-topic "NRU" :labor)))
      "Employment and Services Tax Act (labor+tax) and Workers (Contracts of Service) Ordinance 1967 both tagged :labor")
  (is (= ["nru.beneficial-ownership-act-2017"]
         (mapv :statute/id (facts/by-topic "NRU" :beneficial-ownership))))
  (is (empty? (facts/by-topic "NRU" :data-protection))
      "no data-protection statute located this iteration -- honestly absent")
  (is (empty? (facts/by-topic "ATL" :labor))))
