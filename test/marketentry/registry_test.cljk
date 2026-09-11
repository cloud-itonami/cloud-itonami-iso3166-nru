(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "NRU" 0)
        s (registry/register-submit "eng-1" "NRU" 0)]
    (is (= "NRU-DFT-000000" (get d "draft_number")))
    (is (= "NRU-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "NRU" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest procurement-agent-required-value-threshold
  (testing "reg 6(1): value exceeding $3,000 requires a procurement agent"
    (is (false? (registry/procurement-agent-required? {:contract-value 3000.0 :foreign-or-international-funded? false})) "exactly at threshold does not exceed it")
    (is (true? (registry/procurement-agent-required? {:contract-value 3000.01 :foreign-or-international-funded? false})))
    (is (false? (registry/procurement-agent-required? {:contract-value 2000.0 :foreign-or-international-funded? false})))
    (is (false? (registry/procurement-agent-required? {})) "missing contract-value does not throw and is not required")))

(deftest procurement-agent-required-foreign-or-international-funding
  (testing "reg 6(3): foreign-government/international-institution funding always requires a procurement agent regardless of value"
    (is (true? (registry/procurement-agent-required? {:contract-value 100.0 :foreign-or-international-funded? true})))))

(deftest procurement-agent-requirement-mismatch-is-value-scope-gated
  (testing "an engagement below the threshold and not foreign/international-funded is never flagged, even without an agent"
    (is (false? (registry/procurement-agent-requirement-mismatch?
                 {:contract-value 2000.0 :foreign-or-international-funded? false
                  :conducted-via-procurement-agent? false :procurement-agent nil}))))
  (testing "an engagement above the threshold that did NOT engage a procurement agent -> mismatch"
    (is (true? (registry/procurement-agent-requirement-mismatch?
                {:contract-value 5000.0 :foreign-or-international-funded? false
                 :conducted-via-procurement-agent? false :procurement-agent nil}))))
  (testing "an engagement above the threshold that claims an agent not in the currently authorised set -> mismatch"
    (is (true? (registry/procurement-agent-requirement-mismatch?
                {:contract-value 5000.0 :foreign-or-international-funded? false
                 :conducted-via-procurement-agent? true :procurement-agent :some-unauthorised-agent}))))
  (testing "an engagement above the threshold that DID engage a currently authorised agent -> not flagged"
    (is (false? (registry/procurement-agent-requirement-mismatch?
                 {:contract-value 5000.0 :foreign-or-international-funded? false
                  :conducted-via-procurement-agent? true :procurement-agent :eigigu-procurement})))))

(deftest artificial-division-violation-cross-entity-aggregate
  (testing "a single engagement is never flagged -- nothing to sum against"
    (is (false? (registry/artificial-division-violation?
                 [{:contract-value 2000.0 :foreign-or-international-funded? false
                   :conducted-via-procurement-agent? false}]))))
  (testing "two engagements each below the threshold, whose SUM crosses it, neither via a procurement agent -> violation"
    (is (true? (registry/artificial-division-violation?
                [{:contract-value 2000.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? false}
                 {:contract-value 1500.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? false}]))))
  (testing "two engagements whose sum does NOT cross the threshold -> not a violation"
    (is (false? (registry/artificial-division-violation?
                 [{:contract-value 1000.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? false}
                  {:contract-value 1500.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? false}]))))
  (testing "if any individual engagement in the group already independently required an agent, this is not double-counted as artificial division"
    (is (false? (registry/artificial-division-violation?
                 [{:contract-value 5000.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? false}
                  {:contract-value 1500.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? false}]))))
  (testing "if the group already routed through a procurement agent, no violation"
    (is (false? (registry/artificial-division-violation?
                 [{:contract-value 2000.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? true}
                  {:contract-value 1500.0 :foreign-or-international-funded? false :conducted-via-procurement-agent? false}])))))

;; ---------------------------------------------------------------------------
;; Money is compared at money precision, not at double precision
;; ---------------------------------------------------------------------------

(deftest whole-unit-fees-were-already-correct-and-stay-correct
  (testing "the seeded shape: base + rate x months in whole currency units"
    (is (registry/engagement-fee-matches-claim?
         {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
           :claimed-fee 860000.0}))))

(deftest cent-denominated-fees-are-no-longer-rejected-while-correct
  (testing "`(== (double claimed) (+ (double base) (* (double rate) (double months))))`
            rejected CORRECT totals once an amount carried cents -- 40,989 of
            327,060 combinations (12.5%), against 0 of 327,060 in whole units"
    (let [bad (for [m (range 1 37)
                    bc (range 10000 90000 2100)
                    rc (range 500 6000 210)
                    :let [truth (/ (+ bc (* rc m)) 100.0)]
                    :when (not (registry/engagement-fee-matches-claim?
                                {:base-fee (/ bc 100.0) :monthly-rate (/ rc 100.0)
                                  :monitoring-months m :claimed-fee truth}))]
                [m (/ bc 100.0) (/ rc 100.0) truth])]
      (is (empty? bad) (str "false rejections: " (count bad) " e.g. " (first bad))))))

(deftest a-genuinely-wrong-fee-is-still-caught
  (testing "rounding to money precision must not blunt the check"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.01})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 859999.99})))))

(deftest an-unverifiable-fee-never-matches
  (testing "un-verifiable is not the same as correct, and not a crash"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee "500000" :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.0})))
    (is (nil? (registry/compute-engagement-fee {:base-fee 500000 :monthly-rate 30000})))))
