# E2E Test Infra: br-lawyer pt-BR Localization

## Test Philosophy
- Requirement-driven, opaque-box + structural static audit.
- Methodology: Category-Partition + BVA + Pairwise + Static Code Scanner + Full Maven Multi-Module Reactor Verification.

## Feature Inventory & Test Mapping
| # | Feature | Requirement | Tier 1 (Unit) | Tier 2 (Boundary) | Tier 3 (Pairwise) | Tier 4 (E2E / Static Audit) |
|---|---------|-------------|:-------------:|:-----------------:|:-----------------:|:---------------------------:|
| 1 | Swing Forms & initComponents | ORIGINAL_REQUEST §R1 | ✓ | ✓ | ✓ | ✓ |
| 2 | Dialogs & JOptionPane | ORIGINAL_REQUEST §R1 | ✓ | ✓ | ✓ | ✓ |
| 3 | Resource Bundles Parity | ORIGINAL_REQUEST §R2 | ✓ | ✓ | ✓ | ✓ |
| 4 | Fallback Locale Mechanics | ORIGINAL_REQUEST §R2 | ✓ | ✓ | ✓ | ✓ |
| 5 | Desktop Menus & Titles | ORIGINAL_REQUEST §R3 | ✓ | ✓ | ✓ | ✓ |
| 6 | Splash, Login & Viewers | ORIGINAL_REQUEST §R3 | ✓ | ✓ | ✓ | ✓ |
| 7 | Assistant & Backup Manager | ORIGINAL_REQUEST §R3 | ✓ | ✓ | ✓ | ✓ |
| 8 | Maven Clean Package & Test Suite | ORIGINAL_REQUEST §R4 | ✓ | ✓ | ✓ | ✓ |

## Test Architecture
- **Environment**:
  - JDK: `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot`
  - Maven: `C:\tools\apache-maven-3.9.9\bin\mvn.cmd`
- **Execution Commands**:
  - Unit & Localization: `mvn test -pl j-lawyer-client "-Dtest=PtBrLocalizationTest,M1ChallengerStressTest,BrazilianUiUtilsTest,BrazilianUiUtilsChallengerTest"`
  - Backup Manager: `mvn test -pl j-lawyer-backupmgr`
  - Full Reactor Build: `mvn clean package -pl j-lawyer-client -am`
  - Full Client Test Suite: `mvn test -pl j-lawyer-client`

## Test Suites & Coverage
1. `com.jdimension.jlawyer.client.PtBrLocalizationTest` — Verifies bundle loading and key-set parity between base and pt-BR bundles.
2. `com.jdimension.jlawyer.client.M1ChallengerStressTest` — Stress tests localization helper functions and date/currency formatting under pt-BR.
3. `com.jdimension.jlawyer.client.BrazilianUiUtilsTest` & `BrazilianUiUtilsChallengerTest` — Tests Brazilian UI utilities, CPF/CNPJ/OAB formatting, and currency masks.
4. `de.jdimension.jlawyer.backupmgr.BackupMgrLocalizationTest` & `BackupMgrChallengerTest` — Verifies backup manager bundles and string lookups.
5. `com.jdimension.jlawyer.client.ZeroGermanResidualStaticTest` (To be added in M4) — Comprehensive regex-based scanner verifying 0 German UI literals in `.form` and `.java` production files.

## Acceptance Criteria Thresholds
- Tier 1: 100% pass on all existing 193 client unit tests + 2 backupmgr unit tests.
- Tier 2: 100% pass on boundary and challenger stress tests.
- Tier 3: Pairwise locale fallback verification across all 43 bundle families.
- Tier 4: Clean Maven reactor compilation (`mvn clean package -pl j-lawyer-client -am`) + 0 German residual strings detected across all production Swing forms and Java UI files.
