# Licença e Auditoria de Conformidade Open Source — BR-LAWYER

> **Documento de Governança de Licenças:** `docs/LICENSE_AUDIT.md`  
> **Status:** Aprovado  
> **Licença Primária:** GNU Affero General Public License Version 3 (AGPLv3)

---

## 1. Declaração Geral de Licenciamento

O projeto **BR-LAWYER** é uma obra derivada e evolução do **j-lawyer.org**, integralmente licenciado sob a **GNU Affero General Public License v3 (AGPLv3)**.

### Obrigações e Garantias:
1. **Preservação de Direitos Autorais e Licença:** Todos os avisos de copyright dos autores originais do j-lawyer.org e dos novos colaboradores do BR-LAWYER são mantidos em cada arquivo-fonte.
2. **Disponibilidade do Código-Fonte (Copyleft de Rede - AGPLv3 Art. 13):** O código-fonte integral, incluindo todas as modificações, integrações e arquivos de compilação, é publicamente acessível em [github.com/ricarossetto/br-lawyer](https://github.com/ricarossetto/br-lawyer).
3. **Proibição de Relicenciamento Fechado:** Nenhum componente do núcleo do BR-LAWYER será relicenciado sob termos proprietários ou licenças incompatíveis com o copyleft estrito da AGPLv3.

---

## 2. Auditoria e Isolamento de Componentes

### 2.1 Binário Proprietário beA (Remoção Obrigatória)
- **Artefato:** `j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar`
- **Classificação:** `BLOCKED_FOR_LICENSE_REVIEW / MUST_BE_REMOVED`
- **Decisão:** Extirpar o diretório `j-lawyer-proprietary` e desacoplar o desktop client de qualquer binário fechado da BRAK alemã. Substituir por adaptadores abertos da arquitetura `JudicialSystemAdapter` (DJEN, DataJud, PJe).

### 2.2 Dependências de Terceiros
- Todas as dependências ativas foram auditadas e categorizadas em `docs/THIRD_PARTY.md` e `docs/research/LICENSE_AND_DEPENDENCY_AUDIT.md`.
- As licenças utilizadas (Apache 2.0, MIT, BSD, LGPL, EPL, CDDL, iText AGPLv3) são 100% compatíveis com a distribuição do BR-LAWYER sob AGPLv3.
