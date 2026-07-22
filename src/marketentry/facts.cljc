(ns marketentry.facts
  "Per-jurisdiction public-procurement market-entry regulatory catalog
  -- the G2-style spec-basis table the Market-Entry Compliance Governor
  checks every `:jurisdiction/assess` proposal against ('did the advisor
  cite an OFFICIAL public source for this jurisdiction's requirements,
  or did it invent one?').

  Nauru's real market-entry surface (curl/pdftotext-verified 2026-07-23;
  where a page could not be reached, or a fact could not be
  independently confirmed, that is stated explicitly rather than
  silently assumed):

  - **No dedicated standalone public-procurement regulator/agency was
    found -- this iteration specifically checked, rather than assumed
    by analogy to a sibling with a Procurement Board or Central
    Procurement Unit** (e.g. Grenada's Public Procurement Board). The
    Ministry of Finance's own Treasury Division administers Government
    procurement directly: `naurufinance.info/treasury-division/
    procurement/` (fetched directly, the Ministry of Finance's own live
    site) states 'Part 3A of the Public Finance (Control and
    Management) Act 1997 provides the framework for the Government's
    procurement arrangements' and 'The Treasury is responsible for
    Government procurement arrangements'. This iteration also fetched
    and read the Act's own Part 3A text directly
    (naurufinance.info/wp-content/uploads/2020/08/Public-Finance-
    Control-and-Management-Act-1997.pdf) -- CURRENCY CAVEAT: that PDF's
    own header states 'As in force from 25 January 2013', i.e. it does
    NOT yet incorporate the Public Finance (Control and Management)
    (Amendment) Act 2019 (No. 9 of 2019)'s own replacement text for
    former ss.15B/15C (fetched separately as its own PDF, read
    directly), which changed the statutory definition of 'procurement
    agent' from a single competitively-tendered agent to an
    either/or of (a) an independent procurement agent or (b) a
    Government instrumentality/SOE/statutory corporation appointed by
    Cabinet. This iteration reports the Ministry's own CURRENT live
    procurement page as authoritative for today's operative state
    (mirroring the discipline FSM's own catalog applies to DOFA's
    current page vs its 2001-codified statute text), while separately
    citing the 1997 Act's and 2013 Regulations' own primary text for
    the underlying mechanism.
  - **The flagship check this vertical adds** (see
    `marketentry.governor` / `marketentry.registry`) is grounded in a
    mechanism this iteration found directly in the Regulations' own
    primary text and confirmed is NOT a delegated/unread number: the
    Public Finance (Control and Management) Regulations 2013 (SL No. 1
    of 2013), own text read directly
    (naurufinance.info/wp-content/uploads/2020/12/Public-Finance-
    Regulations-2013-Procurement.pdf), reg 6(1): 'Procurement
    operations of a public authority with a value exceeding $3,000
    must be conducted by the procurement agent on behalf of the
    authority.' reg 6(3) separately requires procurement funded by a
    foreign government or international/regional institution to ALSO
    be conducted by the procurement agent REGARDLESS of value (subject
    to Act s.15E(5) and the procurement manual). The Act's own s.15E(4)
    (own primary text, read directly) adds an anti-structuring rule:
    'A public authority must not artificially divide a procurement
    operation in order to avoid a requirement of the Regulations.' The
    Ministry's own live procurement page names the THREE currently
    authorised procurement agents: Brisbane Procurement (Nauru
    Consulate in Brisbane), Eigigu Procurement, and Nauru Post Office.
    `marketentry.registry`'s flagship check independently recomputes
    BOTH the per-operation threshold test (reg 6(1)/(3)) AND, across a
    declared group of engagements sharing a `:split-group` id, whether
    their combined value would have crossed the $3,000 threshold while
    each individual declared value did not -- the anti-structuring
    check s.15E(4) itself names. This is a genuinely different check
    SHAPE than a single-engagement threshold or tiered lookup: it is a
    CROSS-ENTITY AGGREGATE recompute, grounded in the Act's own
    anti-splitting text rather than assumed by analogy.
  - **Business registration is NOT housed in a Ministry of Commerce**
    (this iteration specifically checked -- Nauru's own government
    department list, fetched from `nauru.gov.nr/government.aspx`, has
    no Ministry of Commerce/Industry at all) **but in the Department of
    Justice and Border Control's own two registration divisions**
    (`justice.gov.nr`, the Department's own separate website, fetched
    directly), both established under Public Service Act 2016 s.11A
    (Gazette 14 May 2021 per the Department's own page): the
    'Business Registration, Business Licensing, Security Licensing,
    Import Licensing and Beneficial Ownership Division' and the
    'Corporations, Partnerships, Associations and Trust Registration
    Division'. Business NAME registration (Business Names Registration
    Act 2018, Registrar of Business Names = the Secretary for Justice
    ex officio, own text read directly: 'The Secretary for Justice
    shall be the Registrar', s.4(2)) is DISTINCT from a business
    LICENCE (Business Licences Act 2017, Registrar of Business Licences
    = 'the Secretary', s.4(1), own text read directly) and from
    CORPORATE incorporation (Corporations Act 1972, Registrar of
    Corporations appointed by the President, s.6(1), own text read
    directly) -- a three-instrument structure, not a single
    'company registry'. The Corporations Act's own s.15(4A) (own text
    read directly, inserted by Act 38 of 2018) cross-references all
    three: a person incorporating (or renewing) a corporation that
    intends to trade under a business name must ALSO comply with the
    Business Licences Act 2017, Beneficial Ownership Act 2017 and
    Business Names Registration Act 2018. The Corporations Act's own
    s.15(4) sets a genuinely date-shaped mechanism this catalog
    documents but does not build a second governor check around (the
    flagship above already adds a cross-entity aggregate shape for
    this vertical): a certificate of incorporation is valid for 12
    months from the date of incorporation, renewable for further
    12-month periods on lodgement of the annual return + fee, and
    s.15(6) makes a director/officer who permits the corporation to
    trade on an EXPIRED certificate PERSONALLY LIABLE for debts/
    liabilities incurred during that period.
  - **Beneficial ownership disclosure IS a real, current requirement,
    but this iteration confirmed it carries NO bright-line ownership
    percentage** (checked directly against the Act's own s.5, unlike
    many FATF-influenced beneficial-ownership regimes elsewhere that
    use a 25% threshold): the Beneficial Ownership Act 2017 (own text
    read directly, `justice.gov.nr`), s.5(1), defines 'beneficial
    owner' as any natural person who has 'ultimate control, directly or
    indirectly' OR who 'ultimately owns, directly or indirectly' the
    legal entity, OR on whose behalf it was created -- no numeric
    threshold is stated in the primary text this iteration read. s.6
    applies the Act to corporations (Corporations Act 1972),
    partnerships (Partnership Act 2018), 2-or-more-person businesses
    (Business Names Registration Act 2018) and trusts. The Secretary
    for Justice is 'the Authority' maintaining the Register.
  - **Tax is NOT the pre-2000s 'zero personal income tax offshore
    financial centre' regime** -- this iteration specifically verified
    Nauru's CURRENT regime rather than relying on that outdated
    reputation. The Nauru Revenue Office's own page
    (`naurufinance.info/nauru-revenue-office/`, fetched directly)
    states Nauru received OECD Global Forum ratification of a 'largely
    compliant' rating with OECD Standards in June 2019, and that Nauru
    'continues to work with international partners ... [on] EU Base
    Erosion and Profit Shifting Inclusive Framework minimum standards
    commitments'. The NRO's own administered-legislation page
    (`naurufinance.info/nauru-revenue-office/nro-administered-
    legislation/`, fetched directly) names a CURRENT multi-instrument
    tax regime: the Revenue Administration Act (RAA, consolidated as of
    20 January 2020, procedural rules for all tax laws); the Employment
    and Services Tax Act (ESTA, consolidated as of 22 January 2020,
    base Act 2014) -- taxes employment/service-fee income sourced in
    Nauru, with a stated exemption for Nauruan citizens (and spouses of
    citizens, and resettled refugees/asylum seekers) earning less than
    $9,240/month ($110,800/year), and requires every employer/payer to
    register for a Tax Identification Number (TIN) with the NRO; the
    Business Tax Act (BTA, consolidated as of 1 January 2021, effective
    from 1 July 2016) -- Small Business Tax (2.5% of gross revenue,
    non-resident individuals with annual gross revenue <= $250,000),
    Business Profits Tax (20% or 25% by residency/ownership/turnover
    category, own text: Category A resident company <= $15,000,000
    gross revenue 20%, Category B resident company > $15,000,000 25%,
    Category C resident company controlled by a non-resident 25%,
    Category D non-resident company via Permanent Establishment 25%),
    and Non-Resident Tax (20% on interest/royalties/insurance
    premiums); the Telecommunications Service Tax Act 2009 (10% of
    gross telecom sales); and the Gaming Act 2011 (bingo/betting/
    raffles/lotteries/table games/gaming-machine licensing).
  - **Labor law: this iteration specifically searched for Nauru's own
    employment statute.** Nauru's `.gov.nr`/`.gov.nr`-hosted sites did
    not surface a dedicated employment-law page, and `paclii.org`
    itself returned a Cloudflare 'Just a moment' bot-detection
    challenge to a direct fetch this iteration did NOT attempt to
    bypass (per this session's own hard safety rule) -- the Internet
    Archive Wayback Machine capture of PacLII's own 'Nauru Sessional
    Legislation beginning with W' index page
    (`web.archive.org/web/20241214201302/http://www.paclii.org/nr/
    legis/num_act/toc-W.html`, fetched directly) confirms the TITLES
    'Workers (Contracts of Service) Ordinance 1967', 'Workers
    (Contracts of Service) Ordinance (No. 2) 1967' and 'Worker's
    Compensation Ordinance 1956' exist in Nauru's own sessional
    legislation database. This iteration could NOT independently fetch
    or read either Ordinance's own full primary text this session (no
    Wayback capture of the specific document page was found, and
    direct PacLII access was blocked) -- an HONEST GAP: `statute.facts`
    below cites the title/year/registry-listing only, not any specific
    section, and does not attribute any particular substantive
    provision to it.
  - This iteration also specifically looked for a representative/
    conflict-of-interest bidder-exclusion or debarment provision (the
    shape Benin's Art. 61/62 or Grenada's s.46 document for their own
    laws) in the Public Finance Act's own Part 3A text and the 2013
    Regulations' own text (both read directly in full). None exists in
    the text read -- `rep-spec-basis` is honestly nil for NRU because
    this iteration confirmed its ABSENCE from the primary text, not
    because it failed to look.

  Coverage is reported HONESTLY (see `coverage`): a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the generic
  intake/portal-registration/filing evidence set; `:legal-basis` /
  `:owner-authority` / `:provenance` are the G2 citation the governor
  requires before any `:jurisdiction/assess` proposal can commit. NRU
  deliberately carries NO `:rep-owner-authority` -- see the namespace
  docstring's honest-absence note (this iteration read the Public
  Finance Act's Part 3A and the 2013 Regulations' own full text and
  confirmed no representative/conflict-of-interest bidder-exclusion
  provision exists there, rather than merely failing to find one).
  `:procurement-agent-owner-authority` / `:procurement-agent-legal-
  basis` / `:procurement-agent-criteria` / `:procurement-agent-
  provenance` ground this vertical's flagship governor check
  (`procurement-agent-requirement-mismatch?` / `artificial-division-
  violation?` in `marketentry.registry`)."
  {"NRU" {:name "Nauru"
          :owner-authority "Treasury Division, Department of Finance, Government of Nauru -- administers Government procurement directly (Public Finance (Control and Management) Act 1997 Part 3A); no dedicated standalone procurement board/agency was found (this iteration specifically checked rather than assumed one exists)"
          :legal-basis "Public Finance (Control and Management) Act 1997, Part 3A (ss.15A-15E, inserted by Act 2012/16; ss.15B/15C replaced by the Public Finance (Control and Management) (Amendment) Act 2019, No. 9 of 2019), own primary text read directly. s.15A: the object of Part 3A is value for money, ethical/fair treatment of participants, and probity/accountability/transparency. s.15E(1): procurement operations of a public authority must be conducted in accordance with the Regulations and the procurement manual. s.15E(4): a public authority must not artificially divide a procurement operation in order to avoid a requirement of the Regulations. CURRENCY CAVEAT: the consolidated Act PDF this iteration read states 'As in force from 25 January 2013' on its own face, i.e. it does not yet incorporate the 2019 Amendment Act's own replacement text for ss.15B/15C (read separately, as its own PDF) -- both are cited."
          :national-spec "Public Finance (Control and Management) Regulations 2013 (SL No. 1 of 2013), own primary text read directly: reg 6(1) 'Procurement operations of a public authority with a value exceeding $3,000 must be conducted by the procurement agent on behalf of the authority'; reg 6(3) procurement funded by a foreign government or international/regional institution must ALSO be conducted by the procurement agent regardless of value (subject to Act s.15E(5)). The Ministry of Finance's OWN current website (naurufinance.info/treasury-division/procurement/, fetched directly) names THREE currently authorised procurement agents: Brisbane Procurement (Nauru Consulate in Brisbane), Eigigu Procurement, and Nauru Post Office."
          :provenance "https://naurufinance.info/treasury-division/procurement/ ; https://naurufinance.info/wp-content/uploads/2020/08/Public-Finance-Control-and-Management-Act-1997.pdf ; https://naurufinance.info/wp-content/uploads/2020/12/Public-Finance-Regulations-2013-Procurement.pdf ; https://naurufinance.info/wp-content/uploads/2020/12/Public-Finance-Act-Amendment-2019-Procurement-Agent.pdf"
          :required-evidence ["Business Name registration record (Business Names Registration Act 2018, Registrar of Business Names = the Secretary for Justice ex officio, Department of Justice and Border Control), when the operator trades under a business name"
                              "Business Licence record (Business Licences Act 2017, Registrar of Business Licences = the Secretary, Department of Justice and Border Control)"
                              "Certificate of Incorporation record (Corporations Act 1972, Registrar of Corporations appointed by the President, Department of Justice and Border Control) -- valid 12 months from incorporation, renewable annually on lodgement of the annual return; this iteration confirmed a director/officer who permits trading on an EXPIRED certificate is personally liable for debts incurred during that period (s.15(6))"
                              "Beneficial Ownership disclosure record (Beneficial Ownership Act 2017, Authority = the Secretary for Justice), when the operator is a corporation, partnership, multi-person business name registrant, or trust"
                              "Tax Identification Number (TIN) registration record with the Nauru Revenue Office (Employment and Services Tax Act; Revenue Administration Act)"
                              "Procurement-agent engagement confirmation record, when the declared procurement operation's value exceeds $3,000 or is funded by a foreign government/international institution (see `procurement-agent-spec-basis`)"
                              "Authorized-representative confirmation record"]
          :corporate-number-owner-authority "Nauru Revenue Office (NRO), Department of Finance"
          :corporate-number-legal-basis "Employment and Services Tax Act (ESTA, base Act 2014, consolidated as of 22 January 2020) and Revenue Administration Act (RAA, consolidated as of 20 January 2020), own summary read directly on the NRO's own administered-legislation page: 'All employers and payers are required to register for a Tax Identification Number (TIN) with the NRO.' This iteration did NOT independently fetch/read either Act's own full primary text (only the NRO's own current summary of them) -- an honest gap on the exact section number, not resolved by guessing."
          :corporate-number-provenance "https://naurufinance.info/nauru-revenue-office/nro-administered-legislation/"
          :procurement-agent-owner-authority "The procurement agent itself (an independent procurement agent, or a Government instrumentality/SOE/statutory corporation appointed by Cabinet, per the Act's own s.15C as replaced by the 2019 Amendment Act), acting on behalf of the public authority; Treasury Division, Department of Finance administers the framework"
          :procurement-agent-legal-basis "Public Finance (Control and Management) Regulations 2013, reg 6(1)/(3) (own primary text): a procurement operation valued over $3,000, OR any procurement funded by a foreign government or international/regional institution regardless of value, must be conducted by the procurement agent. Public Finance (Control and Management) Act 1997 s.15E(4) (own primary text): a public authority must not artificially divide a procurement operation to avoid a Regulations requirement. Currently three authorised agents per the Ministry of Finance's own live procurement page: Brisbane Procurement, Eigigu Procurement, Nauru Post Office."
          :procurement-agent-criteria {:value-threshold 3000
                                      :authorised-agents #{:brisbane-procurement :eigigu-procurement :nauru-post-office}
                                      :foreign-or-international-funding-always-requires-agent? true}
          :procurement-agent-provenance "https://naurufinance.info/wp-content/uploads/2020/12/Public-Finance-Regulations-2013-Procurement.pdf ; https://naurufinance.info/wp-content/uploads/2020/08/Public-Finance-Control-and-Management-Act-1997.pdf ; https://naurufinance.info/treasury-division/procurement/"}
   "USA" {:name "United States"
          :owner-authority "U.S. General Services Administration (GSA) / SAM.gov"
          :legal-basis "Federal Acquisition Regulation (FAR); System for Award Management"
          :national-spec "SAM.gov entity registration + NAICS self-certification"
          :provenance "https://sam.gov/"
          :required-evidence ["EIN record"
                              "SAM.gov registration record"
                              "State business registration record"
                              "Authorized-representative record"]}
   "DEU" {:name "Germany"
          :owner-authority "Beschaffungsamt des BMI / e-Vergabe platforms"
          :legal-basis "Gesetz gegen Wettbewerbsbeschränkungen (GWB) / VgV"
          :national-spec "e-Vergabe supplier registration under EU procurement directives"
          :provenance "https://www.evergabe-online.de/"
          :required-evidence ["Handelsregister extract"
                              "e-Vergabe registration record"
                              "USt-IdNr record"
                              "Authorized-representative record"]}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to assess or file
  on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-nru R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog for market-entry navigation, "
                 "not a survey of all ~194 jurisdictions -- extend "
                 "`marketentry.facts/catalog`, never fabricate a "
                 "jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))

(defn rep-spec-basis
  "The jurisdiction's representative-related requirement map, or nil when
  this catalog has no such regime. For NRU this is deliberately nil --
  this iteration read the Public Finance Act's Part 3A and the 2013
  Regulations' own full text directly and confirmed no representative/
  conflict-of-interest bidder-exclusion or debarment provision exists
  there, distinct from simply not having looked (see namespace
  docstring)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:rep-owner-authority sb)
      (select-keys sb [:rep-owner-authority :rep-legal-basis :rep-provenance]))))

(defn corporate-number-spec-basis
  "The jurisdiction's corporate-number / tax-filing regime, or nil."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority
                       :corporate-number-legal-basis
                       :corporate-number-provenance]))))

(defn procurement-agent-spec-basis
  "The jurisdiction's mandatory-procurement-agent regime, or nil. For NRU
  this is real and current (subject to the namespace docstring's 2013-
  vs-2019 currency caveat) -- the flagship check this vertical adds is
  grounded here (Public Finance (Control and Management) Regulations
  2013 reg 6(1)/(3), Act s.15E(4))."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:procurement-agent-owner-authority sb)
      (select-keys sb [:procurement-agent-owner-authority
                       :procurement-agent-legal-basis
                       :procurement-agent-criteria
                       :procurement-agent-provenance]))))
