## ADDED Requirements

### Requirement: Encrypted Local-First Storage and Recovery
The system SHALL persist application state and judicial runtime using AES-256-GCM encryption with atomic writes, optimistic revisioning, automatic quarantine recovery, and encrypted `.atrium-backup` export/import.

#### Scenario: Corrupted State Auto-Recovery
- **WHEN** encrypted state file fails checksum or integrity verification
- **THEN** system preserves quarantine backup, enters recovery mode, and prompts administrator with restoration options
