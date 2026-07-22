# ADR-0001: Architecture — Nauru market-entry compliance actor (`marketentry`)

**Status**: accepted
**Date**: 2026-07-23

## Context

`cloud-itonami-iso3166-nru` was published as a `:blueprint` (docs +
`blueprint.edn` + `deps.edn`, then a country-level `culture.facts`
catalog in a separate Wave 1 batch) but carried ZERO `src/marketentry`
or `src/statute` content -- its `:public-sector/market-entry-
compliance` domain, declared in `blueprint.edn`, was unimplemented.
This ADR closes that gap, following the pattern established by
`cloud-itonami-iso3166-jpn` (origin) and its Pacific-island siblings
`cloud-itonami-iso3166-fsm` (Federated States of Micronesia) and
`cloud-itonami-iso3166-grd` (Grenada) -- both cloned fresh into scratch
space and studied for exact file layout, namespace shapes, test
conventions, and `deps.edn`/README/CONTRIBUTING/GOVERNANCE conventions
before writing anything for Nauru. `cloud-itonami-iso3166-plw` (Palau)
was checked via the GitHub API and confirmed to carry ONLY the same
`src/culture/` stub Nauru itself started with -- no `marketentry`/
`statute` pattern to study there yet.

Nauru is one of the smallest, least-documented-online sovereign states
in the world (population ~12,000). This iteration expected a genuinely
thin catalog and was prepared for that to be the honest outcome; in
practice, Nauru's own government sites turned out to host a
considerably richer, more current business-registration and
procurement framework than expected once the correct department
websites were located.

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
FSM/GRD's harness verbatim (StateGraph node names, governor hard/
escalate contract, phase 0-3 rollout, `Store` protocol with MemStore +
DatomicStore parity) and researching Nauru's own real market-entry
rules from scratch for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test. Demo data includes `eng-6`/`eng-7`, two engagements
  sharing a declared `:split-group`, specifically to exercise the
  cross-entity flagship check below.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`NRU-DFT-000000`, `NRU-SUB-000000`), plus the two flagship
  checks (see below).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

### Reachability notes: `.gov.nr` is genuinely sparse and TLS-misconfigured, but `.gov.nr`-hosted sub-sites and `paclii.org` behave very differently

`https://www.naurugov.nr/` itself has a TLS certificate issued only for
`*.nauru.gov.nr` (a SAN mismatch) and returns a bare `403 Forbidden`
even with the mismatch ignored -- this iteration did NOT attempt to
work around this via anything beyond a plain `--insecure` curl probe to
observe the response, and disclosed the mismatch rather than treating
it as a live site. The CORRECT live domain, `https://www.nauru.gov.nr/`,
resolves cleanly (HTTP 200) and links out to two further live,
substantially content-rich `.gov.nr` sub-sites this iteration fetched
directly and repeatedly: `naurufinance.info` (Ministry of Finance's own
site -- procurement, tax, Treasury) and `justice.gov.nr` (Department of
Justice and Border Control's own separate site -- both business/
corporate registration divisions). By contrast, `paclii.org` (the
Pacific Islands Legal Information Institute, used here as an acceptable
disclosed secondary source per this session's own instructions, the
same role `droit-afrique.com` plays for Francophone Africa elsewhere in
this fleet) returned a genuine Cloudflare "Just a moment" bot-detection
challenge to every direct fetch attempt. This iteration did NOT attempt
to bypass that challenge (a hard safety rule for this session) --
instead it used the Internet Archive Wayback Machine's own captures of
specific PacLII index pages, which were reachable and readable, for the
one fact this repo's catalogs depend on PacLII for (the labor-law
title/year, see below).

### Which mechanism administers procurement -- investigated against the task's own hypothesis, not assumed

The task suggested a small-state government "may not have a dedicated
standalone procurement regulator/agency the way larger states do."
Direct investigation confirmed this for Nauru specifically: no
Procurement Board, Central Procurement Unit, or similar body was found
anywhere in Nauru's own government structure. Instead, the Ministry of
Finance's own Treasury Division administers Government procurement
directly, under Part 3A of the Public Finance (Control and Management)
Act 1997 (own primary text, fetched and read in full,
`naurufinance.info/wp-content/uploads/2020/08/Public-Finance-Control-
and-Management-Act-1997.pdf`) and the Public Finance (Control and
Management) Regulations 2013 (SL No. 1 of 2013, own primary text,
fetched and read in full,
`naurufinance.info/wp-content/uploads/2020/12/Public-Finance-
Regulations-2013-Procurement.pdf`). The Ministry's own live procurement
page (`naurufinance.info/treasury-division/procurement/`, fetched
directly) states this plainly: "The Treasury is responsible for
Government procurement arrangements."

**Currency caveat honestly preserved, not smoothed over**: the
consolidated 1997 Act PDF this iteration read states on its own face
"As in force from 25 January 2013" -- meaning it predates the Public
Finance (Control and Management) (Amendment) Act 2019 (No. 9 of 2019,
own primary text, fetched and read separately as its own PDF), which
replaced the Act's own ss.15B/15C definition of "procurement agent"
(from a single competitively-tendered agent to an either/or of an
independent agent or a Cabinet-appointed Government instrumentality/
SOE/statutory corporation). This iteration reports BOTH texts, the way
FSM's own catalog reports both DOFA's 2001-codified statute figures and
DOFA's higher current live-page figures, rather than silently picking
one.

### Flagship HARD checks: `procurement-agent-requirement-mismatch` (threshold-plus-funding-source) AND `artificial-division-violation` (the first cross-entity aggregate check in this family)

The Regulations' own reg 6(1) (own primary text, read directly): "Procurement
operations of a public authority with a value exceeding $3,000 must be
conducted by the procurement agent on behalf of the authority." reg
6(3) separately requires procurement funded by a foreign government or
international/regional institution to ALSO go through the agent
regardless of value. The Ministry's own live procurement page names the
three currently authorised agents: Brisbane Procurement (Nauru
Consulate in Brisbane), Eigigu Procurement, and Nauru Post Office.
`marketentry.registry/procurement-agent-required?` independently
recomputes this threshold-plus-funding-source test from an engagement's
own declared `:contract-value`/`:foreign-or-international-funded?`, and
`procurement-agent-requirement-mismatch?` HARD-holds when required but
not confirmed engaged (or the declared agent is not one of the three
currently authorised).

The Act's own s.15E(4) (own primary text, read directly) adds a second,
genuinely distinct mechanism: "A public authority must not artificially
divide a procurement operation in order to avoid a requirement of the
Regulations." `marketentry.registry/artificial-division-violation?`
independently recomputes this across a GROUP of engagements sharing a
declared `:split-group` id -- it sums the group's own declared contract
values and flags a violation when that SUM would have crossed the reg
6(1) threshold while no individual engagement in the group would have
on its own, and none used a procurement agent. This is a genuinely
different check SHAPE than every prior iso3166 sibling in this family:
FSM's citizen-bidder preference is a numeric lookup-table recompute
gated by a four-criterion eligibility AND-test; Grenada's director-
conviction-disqualifying check is a backward-looking date lookback
window. Both of those, like every other sibling check this iteration is
aware of, evaluate ONE engagement's own declared facts in isolation.
`artificial-division-violation?` is the first check in this family that
evaluates MULTIPLE engagements together -- the same real-world
"structuring" pattern (splitting one large purchase into several small
purchase orders to dodge a threshold) that anti-money-laundering and
public-procurement audit practice worldwide treats as a red flag, here
grounded directly in Nauru's own s.15E(4) text rather than assumed by
analogy to any general AML pattern.

### The three-instrument business-registration question

The task asked every iteration to investigate, rather than assume,
Nauru's own business-registration structure. This iteration found a
genuinely three-instrument shape, all housed in the Department of
Justice and Border Control (`justice.gov.nr`, the Department's own
separate website -- Nauru's own government department list,
`nauru.gov.nr/government.aspx`, fetched directly, has no Ministry of
Commerce/Industry at all): the Corporations Act 1972 (Registrar of
Corporations appointed by the President, s.6), the Business Names
Registration Act 2018 (Registrar = the Secretary for Justice ex
officio, s.4(2), own text: "The Secretary for Justice shall be the
Registrar"), and the Business Licences Act 2017 (Registrar = "the
Secretary", s.4(1)). The Corporations Act's own s.15(4A) (inserted by
Act 38 of 2018, own text read directly) cross-references all three: a
person incorporating (or renewing) a corporation that intends to trade
under a business name must ALSO comply with the Business Licences Act
2017, Beneficial Ownership Act 2017 and Business Names Registration Act
2018. The Corporations Act's own s.15(4)/(6) further establishes a
genuine date-shaped mechanism this catalog documents but does not build
a THIRD governor check around (the two flagship checks above already
add threshold-plus-funding-source and cross-entity-aggregate shapes for
this vertical): a Certificate of Incorporation is valid 12 months from
incorporation, renewable annually on lodgement of the annual return,
and a director/officer who permits the corporation to trade on an
EXPIRED certificate is personally liable for debts incurred during that
period.

### Beneficial ownership: verified to have NO bright-line percentage, unlike many FATF-influenced regimes

The Beneficial Ownership Act 2017's own s.5(1) (own primary text, read
directly, substituted by Act 36 of 2018 s.7) defines "beneficial owner"
as a natural person with "ultimate control, directly or indirectly" or
who "ultimately owns, directly or indirectly" the legal entity -- no
numeric ownership-percentage threshold is stated anywhere in the
primary text this iteration read, unlike many FATF-influenced
beneficial-ownership regimes elsewhere that use a 25% bright line. This
iteration deliberately does NOT build a governor check assuming a
percentage threshold that the primary text does not actually state.

### Tax: verified CURRENT, not the outdated "tax haven" reputation

The Nauru Revenue Office's own page (`naurufinance.info/nauru-revenue-
office/`, fetched directly) states Nauru received OECD Global Forum
ratification of a "largely compliant" rating with OECD Standards in
June 2019, and continues work toward EU Base Erosion and Profit
Shifting Inclusive Framework minimum standards. The NRO's own
administered-legislation page (fetched directly) names a current
multi-instrument regime -- Revenue Administration Act, Employment and
Services Tax Act (2014, employer/payer TIN registration required,
Nauruan-citizen exemption below $9,240/month), Business Tax Act
(effective 1 July 2016: Small Business Tax 2.5%, Business Profits Tax
20%/25% by residency/ownership/turnover category, Non-Resident Tax
20%), Telecommunications Service Tax Act 2009, and the Gaming Act 2011.
This iteration did NOT independently fetch/read the RAA's, ESTA's, or
BTA's own full primary text (only the NRO's own current summary of
them) -- an honest gap disclosed in `statute.facts`'s own namespace
docstring, not resolved by guessing section numbers.

### Labor law: a genuinely disclosed, title-only gap

PacLII itself blocked every direct fetch attempt with a Cloudflare bot-
detection challenge this iteration did not attempt to bypass. The
Internet Archive Wayback Machine's own capture of PacLII's official
"Nauru Sessional Legislation beginning with W" index page (fetched
directly, `web.archive.org/web/20241214201302/http://www.paclii.org/
nr/legis/num_act/toc-W.html`) confirmed the TITLES "Workers (Contracts
of Service) Ordinance 1967" and "Workers (Contracts of Service)
Ordinance (No. 2) 1967" exist in Nauru's own sessional legislation
database -- but no Wayback capture of either document's own specific
page could be found this session, and this iteration did not guess at,
or attribute any section number or substantive provision to, either
Ordinance's own text. `statute.facts`'s entry for this Ordinance is
title/year-only, exactly as thin as what could actually be confirmed --
a smaller, honester catalog entry than fabricating detail would
produce.

## Consequences

- `src/` now genuinely exists with real, tested, curl/pdftotext-cited
  content for this blueprint's declared domain (`:public-sector/
  market-entry-compliance`) -- moves this repo's
  `manifest/itonami-fleet-audit.edn` `:prod-ready?` signal from `:stub`
  to `:active`.
- The existing `culture.facts` catalog (Wave 1, unrelated batch) is
  untouched.
- The Act's own general de-minimis/exemption pathways (s.15E(5)/(6):
  disaster/war/social-unrest exemption, Secretary for Finance approval)
  and the Corporations Act's own 12-month certificate-expiry personal-
  liability mechanism (s.15(4)/(6)) are genuine, verified, NOT-
  implemented extension points for a future iteration.
- The labor-law title-only gap, and the RAA/ESTA/BTA's own un-read
  primary text, are honest, disclosed gaps -- this ADR names exactly
  what was and was not independently confirmed, rather than presenting
  a uniformly confident catalog.
- Sibling country blueprints can continue forking JPN/FSM/GRD/NRU and
  swapping in their own genuinely-researched `marketentry.facts` /
  `statute.facts` content and whichever flagship check their own law
  actually supports -- this ADR is itself further evidence that even a
  very small, thinly-documented state can still yield a genuinely
  distinct check SHAPE (here, a cross-entity aggregate) when its own
  primary text is read carefully rather than assumed by analogy.
