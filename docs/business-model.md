# Business model — NRU

Independent public-sector market-entry compliance for Nauru.

- Treasury Division, Department of Finance -- public procurement under
  Part 3A of the Public Finance (Control and Management) Act 1997; no
  dedicated standalone procurement board/agency was found (this
  iteration specifically checked). Public Finance (Control and
  Management) Regulations 2013, reg 6(1)/(3): a procurement operation
  valued over $3,000, or funded at any value by a foreign government or
  international/regional institution, must be conducted by an
  authorised procurement agent (currently Brisbane Procurement, Eigigu
  Procurement, or Nauru Post Office, per the Ministry of Finance's own
  live procurement page).
- Three-instrument business registration, all housed in the Department
  of Justice and Border Control: Corporations Act 1972 (Registrar of
  Corporations), Business Names Registration Act 2018 (Registrar of
  Business Names), Business Licences Act 2017 (Registrar of Business
  Licences); Beneficial Ownership Act 2017 disclosure (no bright-line
  ownership percentage in the primary text read).
- Procurement-agent gate (see `src/marketentry/facts.cljc`) --
  independently recomputes whether a declared procurement operation's
  own value/funding source require an authorised agent under reg
  6(1)/(3), AND whether a group of engagements sharing a declared
  `:split-group` looks like an artificial division of one larger
  operation to dodge the threshold (Act 1997 s.15E(4)) -- the first
  cross-entity aggregate check in this family.
- Tax: NOT a zero-tax offshore haven today -- Business Tax Act
  (effective 1 July 2016), Employment and Services Tax Act (TIN
  registration with the Nauru Revenue Office), Revenue Administration
  Act; OECD Global Forum 'largely compliant' rating (June 2019).

## Trust Controls

Any actual portal registration or filing submission requires
Market-Entry Compliance Governor clearance and always escalates to
human sign-off. A false or fabricated regulatory-requirement claim is a
HARD hold.
