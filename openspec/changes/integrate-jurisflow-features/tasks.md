## 1. Core Brazilian Legal Architecture & Domain Engines
- [x] 1.1 Implement Brazilian Procedural Deadlines Engine (CPC/2015) in Java (`BrazilianProceduralDeadlineCalculator.java`, `ProceduralDeadlineResult.java`)
- [x] 1.2 Implement DataJud CNJ API Discovery & DJEN publication parser in Java (`DatajudProcessDiscoveryClient.java`, `DjenPublicationParser.java`)
- [x] 1.3 Implement Alphanumeric CNPJ validation and Brazilian Registry Intelligence integration
- [x] 1.4 Implement Brazilian Legal Financials & RPV accounting calculator (`BrazilianLegalFinancialCalculator.java`, `RpvCalculationResult.java`)
- [x] 1.5 Implement in-memory PKCS#12 A1 digital certificate validator with password wiping (`A1CertificateValidator.java`, `A1CertificateInfo.java`)
- [x] 1.6 Implement Brazilian Legal Prompt Catalog and token replacement engine (`BrazilianLegalPromptCatalog.java`, `LegalPromptTemplate.java`)

## 2. Automated Testing & Verification
- [x] 2.1 Unit tests for BrazilianProceduralDeadlineCalculator (CPC Art. 219, 220, 224, holidays and recess)
- [x] 2.2 Unit tests for DatajudProcessDiscoveryClient and DjenPublicationParser (Query DSL, endpoint routing, JSON extraction)
- [x] 2.3 Unit tests for BrazilianLegalFinancialCalculator (RPV split, fee percentages, net client, status REPASSADO)
- [x] 2.4 Unit tests for A1CertificateValidator (In-memory PKCS#12 parsing and security wipe)
- [x] 2.5 Unit tests for BrazilianLegalPromptCatalog (Petitions, Contestações, Appeals, Embargos)
