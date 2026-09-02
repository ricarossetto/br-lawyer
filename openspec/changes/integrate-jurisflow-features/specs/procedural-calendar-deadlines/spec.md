## ADDED Requirements

### Requirement: Brazilian Procedural Deadlines and Judicial Calendar
The system SHALL compute procedural deadlines according to Brazilian civil and labor procedural rules (CPC/CLT - business days, national and local court holidays, forensic recess) and SHALL require explicit human confirmation before locking final fatal deadlines.

#### Scenario: Business Day Deadline Computation with Human Review
- **WHEN** an intimation is linked to create a deadline task
- **THEN** an estimated business-day deadline is suggested and marked as pending human review until confirmed by an attorney
