# STATUS DE DESENVOLVIMENTO: ENRIQUECIMENTO DE DADOS CADASTRAIS BRASILEIROS (BR-LAWYER)

**Documento de Acompanhamento de Implementação e Entregas**  
**Branch de Trabalho:** `feat/brazilian-data-enrichment`  
**Data:** 01/09/2026  
**Status Geral:** Em Execução / Implementação de Core & Providers  

---

## 1. CHECKLIST DE ENTREGAS E COMPONENTES

| Componente / Módulo | Status | Responsabilidade | Descrição Técnica |
| :--- | :---: | :--- | :--- |
| **Pesquisa de Fontes Governamentais** | ✅ Concluído | `docs/research/BRAZILIAN_DATA_SOURCES.md` | Mapeamento RFB, SERPRO v3, CNA/OAB, IBGE, BACEN |
| **Matriz de Provedores & Open Source** | ✅ Concluído | `docs/research/BRAZILIAN_REGISTRY_PROVIDER_MATRIX.md` | BrasilAPI, ViaCEP, MinhaReceita, Validações, Fonética PT-BR |
| **Arquitetura de Referência** | ✅ Concluído | `docs/architecture/DATA_ENRICHMENT_ARCHITECTURE.md` | Especificação completa do subsistema, SPI, DTOs e Fluxos |
| **Modelos Canônicos & Validação Java** | 🔄 Em Andamento | `j-lawyer-server-common` | DTOs normalizados, CpfCnpjValidator, Metaphone-PT, Normalizer |
| **SPI de Provedores & Providers Reais** | 🔄 Em Andamento | `j-lawyer-server-common` / `j-lawyer-server-ejb` | BrasilApiProvider, ViaCepProvider, SerproProvider, MockProvider |
| **Motor de Resiliência, Cache e Fallback** | 🔄 Em Andamento | `j-lawyer-server-ejb` | Circuit Breaker, Exponential Backoff, Multi-tier Cache |
| **Deduplicação & Conflict Check** | 🔄 Em Andamento | `j-lawyer-server-ejb` | BrazilianContactDeduplicator, ConflictCheckEnricher |
| **REST API v7** | 🔄 Em Andamento | `j-lawyer-server-io` | Endpoints JAX-RS `/v7/enrichment/...` |
| **EJB Remote Services** | 🔄 Em Andamento | `j-lawyer-server-api` / `j-lawyer-server-ejb` | `BrazilianDataEnrichmentServiceRemote` |
| **Interface Swing Desktop** | 🔄 Em Andamento | `j-lawyer-client` | Dialogs de busca CNPJ, preview diff, QSA import e CEP |
| **Suíte de Testes Unitários e Mock** | 🔄 Em Andamento | `j-lawyer-server-common` / `j-lawyer-server-ejb` | Testes com dados sintéticos sem dependência externa |

---

## 2. PRÓXIMOS PASSOS IMEDIATOS

1. Implementar classes de validação e normalização em `j-lawyer-server-common` (`BrazilianDocumentValidator`, `PortugueseMetaphone`, `LegalEntityNormalizer`).
2. Implementar modelos canônicos e interfaces SPI em `j-lawyer-server-common`.
3. Implementar provedores em `j-lawyer-server-common` e `j-lawyer-server-ejb` (`BrasilApiProvider`, `ViaCepProvider`, `SerproProvider`, `IbgeProvider`, `BacenProvider`, `MockRegistryProvider`).
4. Implementar serviço EJB `BrazilianDataEnrichmentService` com fallback, circuit breaker e cache em `j-lawyer-server-ejb`.
5. Implementar deduplicador e enriquecedor de conflitos em `j-lawyer-server-ejb`.
6. Implementar endpoints REST em `j-lawyer-server-io`.
7. Implementar UI Swing no `j-lawyer-client`.
8. Executar suíte de testes automatizados e validar compilação Maven.
9. Realizar commits semânticos no Git e efetuar push.
