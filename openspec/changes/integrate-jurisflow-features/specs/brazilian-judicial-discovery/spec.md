## ADDED Requirements

### Requirement: Brazilian Judicial Discovery
The system SHALL discover case details from Brazilian judicial databases (DJEN and DataJud) using CNJ standard numbering without producing official acknowledgment or legal science.

#### Scenario: Read-only Case Discovery via CNJ Number
- **WHEN** user queries a valid 20-digit CNJ process number
- **THEN** system fetches parties, movements, court metadata, and subject without acknowledging legal notices
