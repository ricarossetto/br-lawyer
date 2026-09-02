## ADDED Requirements

### Requirement: PKCS#12 A1 Digital Certificate Sandbox
The system SHALL provide an isolated local memory sandbox for loading and signing with Brazilian ICP-Brasil A1 digital certificates (.pfx/.p12) without persisting passwords or private keys in logs or unencrypted stores.

#### Scenario: Certificate Loading and Verification
- **WHEN** user uploads an A1 certificate with valid passphrase
- **THEN** certificate metadata (subject, issuer, validity, CNPJ/CPF) is verified in memory sandbox and sensitive keys remain protected
