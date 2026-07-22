(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest nru-has-spec-basis
  (let [sb (facts/spec-basis "NRU")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "NRU")))
    (is (some? (facts/procurement-agent-spec-basis "NRU")))))

(deftest nru-rep-spec-basis-is-honestly-absent
  (testing "the Public Finance Act's Part 3A and 2013 Regulations' own full text were read directly and contain no representative/conflict-of-interest bidder-exclusion provision"
    (is (nil? (facts/rep-spec-basis "NRU")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "NRU")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "NRU" all)))
    (is (not (facts/required-evidence-satisfied? "NRU" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["NRU" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest procurement-agent-spec-basis-criteria
  (let [pa (facts/procurement-agent-spec-basis "NRU")]
    (is (= 3000 (get-in pa [:procurement-agent-criteria :value-threshold])))
    (is (contains? (get-in pa [:procurement-agent-criteria :authorised-agents]) :brisbane-procurement))
    (is (contains? (get-in pa [:procurement-agent-criteria :authorised-agents]) :eigigu-procurement))
    (is (contains? (get-in pa [:procurement-agent-criteria :authorised-agents]) :nauru-post-office))
    (is (true? (get-in pa [:procurement-agent-criteria :foreign-or-international-funding-always-requires-agent?])))))
