(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `procurement-agent-required?` / `procurement-agent-requirement-
  mismatch?` / `artificial-division-violation?` are the SAME discipline
  applied to a genuinely Nauru-specific mechanism: the Public Finance
  (Control and Management) Regulations 2013 (SL No. 1 of 2013), reg
  6(1)/(3) -- a procurement operation of a public authority valued over
  $3,000, OR funded (at any value) by a foreign government or
  international/regional institution, must be conducted by an
  authorised procurement agent (currently Brisbane Procurement, Eigigu
  Procurement, or Nauru Post Office, per the Ministry of Finance's own
  live procurement page) -- and the Public Finance (Control and
  Management) Act 1997's own s.15E(4): 'A public authority must not
  artificially divide a procurement operation in order to avoid a
  requirement of the Regulations.'

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors: the Federated States of Micronesia's
  citizen-bidder preference is a numeric lookup-table recompute gated
  by a four-criterion eligibility AND-test; Grenada's director-
  conviction-disqualifying check is a backward-looking DATE lookback
  window. Nauru's own mechanism has TWO independently distinct parts:
  (1) `procurement-agent-required?`/`procurement-agent-requirement-
  mismatch?` is a per-engagement THRESHOLD-plus-funding-source boolean
  gate (a single operation's own declared value/funding facts); (2)
  `artificial-division-violation?` is a CROSS-ENTITY AGGREGATE
  recompute -- it does not evaluate one engagement's own declared
  facts in isolation, but SUMS the declared contract values of a
  GROUP of engagements sharing a declared `:split-group` id, and
  independently determines whether that group's own combined value
  would have crossed the reg 6(1) threshold while no individual
  engagement in the group, on its own declared value, would have --
  the same real-world 'structuring' pattern (splitting one large
  purchase into several small purchase orders to dodge a threshold)
  that anti-money-laundering and public-procurement audit practice
  worldwide treats as a red flag, here grounded directly in the Act's
  own s.15E(4) text rather than assumed by analogy. No prior sibling in
  this family evaluates more than one engagement at a time for a single
  check; this is the first CROSS-ENTITY check shape in the fleet.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement system. It builds the RECORD an
  operator would keep, not the act of submitting a filing itself (that
  is `marketentry.operation`'s `:filing/submit`, always human-gated --
  see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def procurement-agent-thresholds
  "Public Finance (Control and Management) Regulations 2013 (SL No. 1 of
  2013), reg 6(1)/(3), own primary text."
  {:value-threshold 3000
   :authorised-agents #{:brisbane-procurement :eigigu-procurement :nauru-post-office}})

(defn procurement-agent-required?
  "The ground-truth reg 6(1)/(3) determination for `engagement`,
  independently recomputed from its own declared `:contract-value` and
  `:foreign-or-international-funded?`: required if the value exceeds
  $3,000 (reg 6(1)), OR if the operation is funded by a foreign
  government or international/regional institution regardless of value
  (reg 6(3)). A missing/nil `:contract-value` is treated as not
  exceeding the threshold (does not throw)."
  [{:keys [contract-value foreign-or-international-funded?]}]
  (boolean
   (or (and (some? contract-value)
            (> (double contract-value) (:value-threshold procurement-agent-thresholds)))
       (true? foreign-or-international-funded?))))

(defn procurement-agent-requirement-mismatch?
  "Does `engagement` require a procurement agent under
  `procurement-agent-required?`, but is NOT independently confirmed to
  have actually engaged one -- either `:conducted-via-procurement-
  agent?` is not true, or the declared `:procurement-agent` is not one
  of the currently authorised agents (Brisbane Procurement, Eigigu
  Procurement, Nauru Post Office)? An engagement for which a
  procurement agent is NOT required is never flagged by this check
  (the same entity/engagement-scope-gating discipline every sibling
  actor's own threshold/eligibility check uses)."
  [{:keys [conducted-via-procurement-agent? procurement-agent] :as engagement}]
  (boolean
   (and (procurement-agent-required? engagement)
        (or (not (true? conducted-via-procurement-agent?))
            (not (contains? (:authorised-agents procurement-agent-thresholds) procurement-agent))))))

(defn artificial-division-violation?
  "The Public Finance (Control and Management) Act 1997's own s.15E(4)
  anti-structuring rule ('A public authority must not artificially
  divide a procurement operation in order to avoid a requirement of the
  Regulations'), independently recomputed across `engagements` -- a
  GROUP of engagement maps this iteration treats as sharing a single
  declared `:split-group` id (the caller is responsible for grouping;
  this function does not itself look up a store). Flags a violation
  when the group's OWN declared contract values, SUMMED, exceed the
  reg 6(1) $3,000 threshold, while EVERY individual engagement's own
  declared value, on its own, would NOT have required a procurement
  agent, AND none of them independently engaged one. A single-
  engagement group (nothing to sum against) is never flagged -- this
  check exists specifically to catch a MULTI-engagement structuring
  pattern, not to duplicate `procurement-agent-requirement-mismatch?`'s
  own single-engagement threshold test."
  [engagements]
  (boolean
   (and (> (count engagements) 1)
        (let [total (reduce + (map #(double (or (:contract-value %) 0.0)) engagements))]
          (> total (:value-threshold procurement-agent-thresholds)))
        (every? #(not (procurement-agent-required? %)) engagements)
        (every? #(not (true? (:conducted-via-procurement-agent? %))) engagements))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a filing
  (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
