# cloud-itonami-iso3166-nru

Open ISO 3166 Blueprint for **NRU**: Nauru -- **`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a Compliance Advisor +
independent **Market-Entry Compliance Governor** to navigate public-
procurement registration, local business/tax registration, and
regulatory-compliance rules in Nauru, so the operator can win and
service a government contract without hiring a full in-house
compliance department.

## Official surface (curl/pdftotext-verified 2026-07-23)

- Procurement: no dedicated standalone procurement board/agency was
  found (this iteration specifically checked, rather than assumed one
  exists the way a larger state might have) -- the Treasury Division,
  Department of Finance administers Government procurement directly
  under Part 3A of the Public Finance (Control and Management) Act
  1997 (`naurufinance.info/treasury-division/procurement/`, the
  Ministry of Finance's own live site, fetched directly). The Public
  Finance (Control and Management) Regulations 2013 (SL No. 1 of 2013),
  own primary text read directly, reg 6(1): any procurement operation
  valued over $3,000 must be conducted by an authorised procurement
  agent (currently Brisbane Procurement, Eigigu Procurement, or Nauru
  Post Office); reg 6(3): procurement funded by a foreign government or
  international/regional institution must ALSO go through the agent
  regardless of value. The Act's own s.15E(4) separately prohibits
  artificially dividing a procurement operation to avoid a Regulations
  requirement.
- Business registration: a THREE-INSTRUMENT structure, all housed in
  the Department of Justice and Border Control (`justice.gov.nr`, the
  Department's own separate website), not a Ministry of Commerce (none
  exists in Nauru's own government department list). Corporate
  incorporation: Corporations Act 1972, Registrar of Corporations
  appointed by the President (s.6). Business name registration:
  Business Names Registration Act 2018, Registrar = the Secretary for
  Justice ex officio (s.4(2)). Business licensing: Business Licences
  Act 2017, Registrar = the Secretary (s.4(1)). Beneficial ownership
  disclosure: Beneficial Ownership Act 2017 (s.5: 'ultimate control' or
  'ultimate ownership', no bright-line percentage threshold in the
  primary text read).
- Tax: NOT the pre-2000s reputation of a zero-personal-income-tax
  offshore financial centre -- the Nauru Revenue Office's own page
  confirms OECD Global Forum ratified a 'largely compliant' rating with
  OECD Standards in June 2019. Current instruments (per the NRO's own
  administered-legislation summary): Revenue Administration Act,
  Employment and Services Tax Act (2014, employer/payer TIN
  registration required), Business Tax Act (effective 1 July 2016:
  Small Business Tax 2.5%, Business Profits Tax 20%/25% by category,
  Non-Resident Tax 20%), Telecommunications Service Tax Act 2009,
  Gaming Act 2011.

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD checks | `procurement-agent-requirement-mismatch` (Public Finance (Control and Management) Regulations 2013 reg 6(1)/(3): a procurement operation valued over $3,000, or funded at any value by a foreign government/international institution, must be conducted by an authorised procurement agent -- independently recomputed from the engagement's own declared contract value/funding source, see `docs/adr/0001-architecture.md`) AND `artificial-division-violation` (Public Finance (Control and Management) Act 1997 s.15E(4): a public authority must not artificially divide a procurement operation to avoid a Regulations requirement -- independently recomputed by SUMMING the declared contract values of every engagement sharing a `:split-group` id, the first CROSS-ENTITY aggregate check in this family) |
| Compliance catalog | `src/statute/facts.cljc` -- Corporations Act 1972, Business Names Registration Act 2018, Business Licences Act 2017, Beneficial Ownership Act 2017, Business Tax Act, Employment and Services Tax Act, Workers (Contracts of Service) Ordinance 1967 (title/year only -- honest gap, see ADR) |
| Tests | `clojure -M:dev:test` |
| Demo | `clojure -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

## No robotics premise -- digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) -- the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set -- it always requires human sign-off.

## What this is NOT

- **Not the government of Nauru.** This blueprint is an independent
  operator the government contracts with or that bids into its
  procurement -- never the government itself, and never an official
  channel.
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Nauru-licensed counsel
  or a registered agent where the law requires licensed
  representation.

## Capability layer

Required capabilities (`blueprint.edn`):

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Nauru:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
