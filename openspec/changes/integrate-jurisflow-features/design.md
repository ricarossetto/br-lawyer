## Context
BR-LAWYER is an enterprise legal practice management platform adapting and expanding functionality for Brazilian attorneys, courts, and law firms. The addition of the complete Juris-Flow (ATRIUM v2.0) architecture delivers local-first Brazilian legal operations with end-to-end encryption, automated scraping/collection, and modern UI V2 components.

## Goals / Non-Goals
- **Goals**:
  - Deliver native integration with DataJud (CNJ), DJEN, and PJe portals.
  - Implement PKCS#12 A1 certificate sandbox and TOTP 2FA.
  - Provide procedural deadline calculations under Brazilian law (CPC, CLT, etc.) with mandatory human confirmation.
  - Deploy complete 17-view UI V2 alongside fallback UI Classic.
  - Support encrypted local-first persistence (AES-256-GCM) with automated recovery and snapshot backup.
  - Integrate comprehensive AI prompt library and Google Gemini assistance with minimized context.
- **Non-Goals**:
  - Replacing the Java EE server backend; Juris-Flow operates cooperatively as the full-stack web and collector runtime.
  - Unsupervised legal filing or automated official notice acknowledgment (science judicial).

## Decisions
- **Decision 1: Modular Subsystems**: The Node.js fullstack engine operates with `server.mjs`, `collector/`, `lib/`, `js/`, and `css/` in the project root, providing unified scripts and developer tooling.
- **Decision 2: Strict Human-in-the-Loop**: All sensitive legal actions (deadline confirmation, notice science, email bulletins) require explicit human interaction.
- **Decision 3: Zero-Leakage Crypto**: A1 certificate keys, passwords, and TOTP secrets are held strictly in local memory sandboxes and never exposed to logs or API payloads.

## Risks / Trade-offs
- **Risk**: Node.js 24 runtime requirement.
  - **Mitigation**: Launchers check Node.js and Corepack availability automatically.
