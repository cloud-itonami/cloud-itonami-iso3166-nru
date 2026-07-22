(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Nauru procurement law, whether a declared procurement
  operation actually requires an authorised procurement agent under the
  Public Finance (Control and Management) Regulations 2013's own reg
  6(1)/(3), whether a group of declared engagements looks like an
  artificial division of one larger procurement operation (Act 1997
  s.15E(4)), whether a claimed engagement fee actually equals base +
  months x rate, whether a Nauru Revenue Office Tax Identification
  Number has been verified for a filing that requires it, or when a
  draft stops being a draft and becomes a real-world
  naurufinance.info-administered procurement submission, so this MUST
  be a separate system able to *reject* a proposal and fall back to
  HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Seven checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Procurement-agent
       requirement mismatch         -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       contract value and funding
                                       source require an authorised
                                       procurement agent under the
                                       Regulations' own reg 6(1)/(3),
                                       and HARD-hold if required but
                                       not confirmed engaged (or the
                                       declared agent is not one of
                                       the currently authorised
                                       three). FLAGSHIP part 1 --
                                       a THRESHOLD-plus-funding-source
                                       boolean gate, a check SHAPE
                                       genuinely different from every
                                       prior sibling's numeric-lookup
                                       or date-window recompute.
    4. Artificial division
       violation                     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute, across
                                       every OTHER engagement sharing
                                       the SAME declared
                                       `:split-group`, whether the
                                       group's own combined declared
                                       contract value would have
                                       crossed reg 6(1)'s threshold
                                       while no individual engagement
                                       in the group would have on its
                                       own -- the Act's own s.15E(4)
                                       anti-structuring rule. FLAGSHIP
                                       part 2 -- the first CROSS-ENTITY
                                       AGGREGATE check in this family
                                       (every prior sibling evaluates
                                       one engagement's own declared
                                       facts in isolation).
    5. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    6. TIN verification unverified  -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-tin-verification?
                                       true`, INDEPENDENTLY check
                                       `:tin-verified?`. CONDITIONAL on
                                       the engagement's own ground
                                       truth. Grounded in the Nauru
                                       Revenue Office's own Tax
                                       Identification Number
                                       requirement (Employment and
                                       Services Tax Act; Revenue
                                       Administration Act -- see
                                       `marketentry.facts`).
    7. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(事業名登録/事業免許/法人登録/受益所有者開示/TIN登録確認等)が充足していない状態での提案"}]))))

(defn- procurement-agent-requirement-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared contract value/funding source require an
  authorised procurement agent under the Regulations' own reg 6(1)/(3)
  -- flagship part 1."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/procurement-agent-requirement-mismatch? e)
        [{:rule :procurement-agent-requirement-mismatch
          :detail (str subject " は取引価値(" (:contract-value e)
                      ")または外国政府/国際機関資金拠出(foreign-or-international-funded?="
                      (:foreign-or-international-funded? e)
                      ")によりPublic Finance (Control and Management) Regulations 2013 "
                      "reg 6(1)/(3)の認可済みprocurement agent(Brisbane Procurement/"
                      "Eigigu Procurement/Nauru Post Office)経由が要求されるが、"
                      "conducted-via-procurement-agent?=" (:conducted-via-procurement-agent? e)
                      " procurement-agent=" (:procurement-agent e) " で未確認")}]))))

(defn- artificial-division-violations
  "For `:filing/submit`, INDEPENDENTLY recompute -- across every OTHER
  engagement sharing the SAME declared `:split-group` -- whether the
  group's own combined declared contract value would have crossed reg
  6(1)'s $3,000 threshold while no individual engagement in the group
  would have on its own -- Act 1997 s.15E(4)'s own anti-structuring
  rule. Flagship part 2, the first CROSS-ENTITY check in this family."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (:split-group e)
        (let [group (filter #(= (:split-group %) (:split-group e)) (store/all-engagements st))]
          (when (registry/artificial-division-violation? group)
            [{:rule :artificial-division-violation
              :detail (str subject " は split-group=" (:split-group e)
                          " を共有する他のengagementと合わせて "
                          "Public Finance (Control and Management) Act 1997 s.15E(4)の"
                          "禁じるprocurement operationの人為的分割(artificial division)"
                          "に該当する可能性がある(個別には reg 6(1)の$3,000閾値未満だが"
                          "合算では超過し、いずれもprocurement agentを経由していない)")}]))))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- tin-verification-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-tin-verification? true`, INDEPENDENTLY check
  `:tin-verified?` -- CONDITIONAL on the engagement's own ground
  truth. Grounded in the Nauru Revenue Office's own Tax Identification
  Number requirement (Employment and Services Tax Act; Revenue
  Administration Act)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-tin-verification? e))
                 (not (true? (:tin-verified? e))))
        [{:rule :tin-verification-unverified
          :detail (str subject " はNauru Revenue OfficeのTax Identification Number(TIN)登録"
                      "(Employment and Services Tax Act; Revenue Administration Act)の"
                      "確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (procurement-agent-requirement-mismatch-violations request st)
                           (artificial-division-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (tin-verification-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
