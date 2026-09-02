# Change: Integrate Juris-Flow (ATRIUM v2.0) Features into BR-LAWYER

## Why
BR-LAWYER requires complete native capabilities for the Brazilian legal ecosystem. Juris-Flow (ATRIUM v2.0) provides a battle-tested, MIT-licensed, local-first legal suite including automated judicial collectors (DataJud, DJEN, PJe), digital certificate A1 sandbox, 2FA TOTP authentication, Brazilian procedural deadline management (CPC/CLT/dias úteis with human supervision), rich AI petition/prompt engine (+350KB models), and a modern 17-view UI V2 web client.

## What Changes
- **Brazilian Judicial Discovery & Collectors**: Native collectors for DJEN, DataJud (CNJ API) and PJe with scheduled automation and triage.
- **A1 Certificate Sandbox & TOTP 2FA**: Local secure cryptographic sandbox for PKCS#12 A1 certificates and TOTP RFC 6238 two-factor authentication without leaking secrets.
- **Procedural Calendar & Deadlines**: Brazilian procedural deadline computation (business days, forensic recesses, suspension calendars) requiring mandatory human confirmation.
- **AI Assistant & Prompt Bank**: Deep legal prompt catalog (+350KB) and Google Gemini AI assistance with minimized context and human supervision.
- **UI V2 & Classic Web Client**: Full-featured responsive modern web UI with 17 canonical views covering Cases (Processos), Publications, Tasks/Kanban, Agenda, Contacts, Leads/CRM, Financials, Documents, Prompts, Judicial Integrations, Audit, Collector Monitoring, Importer, and White-label branding.
- **Encrypted Local-First Storage & Recovery**: AES-256-GCM encryption at rest, atomic writes, snapshot recovery, and encrypted `.atrium-backup` archives.

## Impact
- Affected specs: `brazilian-judicial-discovery`, `djen-datajud-collector`, `a1-certificate-sandbox`, `procedural-calendar-deadlines`, `ai-assistant-prompts`, `ui-v2-web-client`, `encrypted-storage-recovery`
- Affected code: `server.mjs`, `collector/`, `lib/`, `js/`, `css/`, `scripts/`, `tests/`, `index.html`
