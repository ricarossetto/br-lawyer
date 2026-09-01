# BR-LAWYER — Upstream Baseline & Synchronization Strategy

> **Projeto Upstream:** [jlawyerorg/j-lawyer-org](https://github.com/jlawyerorg/j-lawyer-org)  
> **Licença Upstream:** AGPLv3 (GNU Affero General Public License v3)  
> **Repositório BR-LAWYER:** [ricarossetto/br-lawyer](https://github.com/ricarossetto/br-lawyer)  
> **Data de Estabelecimento do Baseline:** 31 de Agosto de 2026

---

## 1. Registro Exato do Baseline Upstream

- **Commit SHA Exato:** `d2a2e579a5276a433406c88f0f3cd6056c659643`
- **Data do Commit Upstream:** 28 de Agosto de 2026 (18:48:27 +0200)
- **Mensagem do Commit:** `fix(desktop): aktiven Wiedervorlagen-Tab beim Aktualisieren beibehalten (refs #3580)`
- **Autor / Committer:** `j-dimension <info@j-lawyer.org>`
- **Versão Upstream Declarada no pom.xml:** `2.8.0` (Branch de desenvolvimento / release target 3.6-dev)
- **Tag Imutável do Baseline:** `v3.6-upstream-baseline-20260831`

---

## 2. Configuração Git Remotes

A topologia de branches e remotes do BR-LAWYER deve seguir rigorosamente:

```bash
# Remotes configurados:
origin    https://github.com/ricarossetto/br-lawyer.git (fetch & push)
upstream  https://github.com/jlawyerorg/j-lawyer-org.git (fetch & push)

# Branch de desenvolvimento BR-LAWYER:
br-lawyer-dev

# Tag de referência imutável:
git tag -a v3.6-upstream-baseline-20260831 d2a2e579a5276a433406c88f0f3cd6056c659643 -m "Upstream j-lawyer baseline commit for BR-LAWYER"
```

---

## 3. Estratégia de Merge e Sincronização Periódica

Para preservar a capacidade de receber melhorias e correções de segurança do j-lawyer.org:

1. **Princípio do Fork Evolutivo:**
   - O histórico do upstream jamais é reescrito (proibido `rebase` destrutivo ou `force push` no histórico comum).
   - O BR-LAWYER mantém os namespaces técnicos legados (`com.jdimension.jlawyer.*`, `org.jlawyer.*`) durante as fases iniciais para evitar conflitos massivos de merge.
   - Novas capacidades brasileiras residem em pacotes e tabelas específicas (`com.jdimension.jlawyer.domain.legal.cnj.*`, `com.jdimension.jlawyer.integration.brazil.*`, tabelas `br_*`).

2. **Fluxo de Sincronização Periódica:**
   ```bash
   # 1. Obter atualizações do upstream
   git fetch upstream master

   # 2. Criar branch de sincronização temporária
   git checkout -b sync/upstream-$(date +%Y%m%d)

   # 3. Merge do upstream master
   git merge upstream/master

   # 4. Resolução de conflitos guiada pela Matriz de Paridade
   # 5. Execução da suíte de testes de regressão
   mvn clean test

   # 6. Merge no branch br-lawyer-dev após validação
   ```

3. **Gerenciamento de Módulos:**
   - **Módulos Mantidos (Core Inalterado/Estendido):** `j-lawyer-server-common`, `j-lawyer-server-entities`, `j-lawyer-server-api`, `j-lawyer-server-ejb`, `j-lawyer-server-io`, `j-lawyer-client`, `j-lawyer-cloud`, `j-lawyer-backupmgr`, `j-lawyer-io-common`.
   - **Módulos com Substituição/Adaptação Especializada:**
     - `j-lawyer-invoicing`: Adaptado para Nota Fiscal de Serviço Eletrônica (NFS-e), recibos e modelos de cobrança brasileiros.
     - `j-lawyer-fax`: Mantido como legado/opcional (Sipgate); módulo de comunicações estendido para integração com WhatsApp API / Provedores de SMS/Email.
   - **Módulos Proprietários / Isolados:**
     - `j-lawyer-proprietary`: Contém o wrapper fechado do beA alemão (`j-lawyer-bea-wrapper.jar`). Desativado por padrão no BR-LAWYER e substituído por adapters brasileiros abertos (`DjenAdapter`, `DataJudAdapter`, `PjeAdapter`).

---

## 4. Patches Específicos do BR-LAWYER

| Patch ID | Área | Descrição |
| :--- | :--- | :--- |
| `PATCH-BR-01` | `i18n` | Adição de bundles pt-BR e definição de `pt_BR` como Locale padrão do sistema. |
| `PATCH-BR-02` | `domain-cnj` | Validador e formatador de Numeração Processual Única CNJ (Res. 65/2008). |
| `PATCH-BR-03` | `domain-entities` | Suporte a CPF, CNPJ, Inscrição OAB/UF e partes processuais nos beans de entidade. |
| `PATCH-BR-04` | `judicial-integrations`| Adaptadores de ingestão do DJEN (ComunicaAPI) e enriquecimento DataJud (Elasticsearch). |
| `PATCH-BR-05` | `deadlines-safety` | Motor temporal com calendários judiciais e bloqueio de criação automática de prazos sem confirmação humana. |
| `PATCH-BR-06` | `ux-atrium` | Daily Command Center, Inspector Lateral e busca omni `Ctrl+K`. |
