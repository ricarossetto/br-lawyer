## ADDED Requirements

### Requirement: Automated Judicial Collectors
The system SHALL run scheduled and manual background collectors for DJEN, DataJud, and PJe tribunals, triaging publications and storing raw and normalized records.

#### Scenario: DJEN Publication Scraping
- **WHEN** the collector agent executes for configured attorney OABs or law firms
- **THEN** publications are downloaded, deduplicated, and placed in the triage inbox with unread/pending-treatment states
