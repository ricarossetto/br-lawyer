# BR-LAWYER — Upstream Compatibility & Merge Friction Minimization Guidelines

> **Documento de Governança Técnica:** `docs/UPSTREAM_COMPATIBILITY_GUIDELINES.md`  
> **Versão:** 1.0.0  
> **Status:** APROVADO  
> **Baseline Upstream:** `d2a2e579a5276a433406c88f0f3cd6056c659643` (j-lawyer.org v3.6-dev / 2.8.0)  
> **Escopo:** Diretrizes mandatórias para desenvolvimento no BR-LAWYER garantindo merges contínuos e sem fricção com o upstream.

---

## 1. Auditoria do Diff da Fase 1 (`feat/foundation-i18n`)

### 1.1 Inventário de Arquivos do Diff

| Categoria | Arquivo / Recurso | Tipo de Operação | Risco de Conflito |
| :--- | :--- | :--- | :--- |
| **Domínio / Validação** | `j-lawyer-server-common/.../cnj/CnjNumber.java` | Criação (Novo) | Nulo |
| **Domínio / Validação** | `j-lawyer-server-common/.../cnj/CnjNumberValidator.java` | Criação (Novo) | Nulo |
| **Domínio / Validação** | `j-lawyer-server-common/.../cnj/CpfCnpjValidator.java` | Criação (Novo) | Nulo |
| **Testes Unitários** | `j-lawyer-server-common/.../cnj/CnjNumberValidatorTest.java` | Criação (Novo) | Nulo |
| **Testes Unitários** | `j-lawyer-server-common/.../cnj/CpfCnpjValidatorTest.java` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../AboutDialog_pt_BR.properties` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../AdminConsoleFrame_pt_BR.properties` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../JKanzleiGUI_pt_BR.properties` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../LoginDialog_pt_BR.properties` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../Main_pt_BR.properties` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../Modules_pt_BR.properties` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../SplashThread_pt_BR.properties` | Criação (Novo) | Nulo |
| **Localização (i18n)** | `j-lawyer-client/.../StartupSplashFrame_pt_BR.properties` | Criação (Novo) | Nulo |
| **Governança & Docs** | `docs/UPSTREAM.md`, `docs/UPSTREAM_ATTRIBUTION.md`, etc. | Criação (Novos) | Nulo |
| **Arquivos Core Upstream** | `pom.xml`, `Main.java`, `SplashThread.java`, `JKanzleiGUI.java` | **Inalterados** | Nulo |

### 1.2 Análise de Impacto em Arquivos Compartilhados
- **`pom.xml` (Parent & Módulos):** Preservada a versão `2.8.0`, estrutura de módulos e repositórios. Nenhuma alteração destrutiva ou reordenação de tags.
- **`SplashThread.java` & `Main.java`:** As chamadas originais utilizam `ResourceBundle.getBundle("...")`. A introdução dos arquivos `_pt_BR.properties` permite a localização automática via resolução de locale padrão da JVM sem tocar em uma única linha de código Java original.
- **Arquitetura de Diretórios Upstream:** Preservada integralmente.

---

## 2. Princípios Fundamentais de Não-Atrito com o Upstream

Para que o BR-LAWYER continue recebendo patches de segurança e melhorias contínuas do j-lawyer.org por anos sem divergência de código incontrolável, todas as contribuições devem obedecer aos 6 Princípios Cardeais:

### Princípio 1: Aditividade Estrita (Purely Additive Pattern)
- Funcionalidades específicas do Brasil (CNJ, CPF/CNPJ, OAB, DJEN, DataJud, PJe, NFS-e, PIX) devem ser criadas em novos arquivos e classes.
- O código novo reside em pacotes dedicados (`com.jdimension.jlawyer.domain.legal.*`, `com.jdimension.jlawyer.integration.brazil.*`).

### Princípio 2: Proibição de Refatoração Cosmética em Código Upstream (No Cosmetic Cleanups)
- **É expressamente proibido:**
  - Reformatar código original upstream (reindentação, ordenação automática de imports, remoção de linhas em branco).
  - Renomear variáveis locais, métodos privados ou parâmetros em classes upstream apenas por estilo.
  - Converter arquivos originais para novos estilos ou regras de linting que gerem diffs desnecessários.
- Qualquer diff em arquivo compartilhado existente deve conter estritamente a modificação funcional necessária.

### Princípio 3: Estabilidade de Interfaces e Assinaturas (Contract Preservation)
- Nenhuma assinatura pública ou protegida de método herdado do j-lawyer.org pode ser alterada ou removida.
- Para estender comportamento:
  - Adicionar sobrecargas de métodos (*overloads*), mantendo o método original intacto delegando para a nova implementação.
  - Utilizar herança ou composição (padrão *Decorator* ou *Adapter*).

### Princípio 4: Desacoplamento de Localização (ResourceBundles Only)
- Toda internacionalização em Português do Brasil deve ser feita em arquivos `<NomeDoComponente>_pt_BR.properties`.
- Nunca modificar diretamente arquivos `<NomeDoComponente>.properties` (alemão/default) ou `<NomeDoComponente>_en.properties` (inglês), exceto para adicionar chaves novas ausentes no upstream.
- Nunca embutir strings literais de mensagens diretamente no código-fonte Java.

### Princípio 5: Padrão Adapter & Feature Flags para Componentes Exclusivos
- Componentes exclusivos da Alemanha (como o sistema `beA` ou tabelas `RVG`):
  - Não devem ser deletados da base de código, pois isso quebraria os merges de melhorias do upstream nesses módulos.
  - Devem ser desativados via configurações, anotações `@Deprecated`, Feature Flags ou profiles de compilação Maven.
  - Implementações brasileiras implementam interfaces comuns (`JudicialSystemAdapter`, `BillingStrategy`).

### Princípio 6: Modificações de Entidades JPA e Banco de Dados (Non-breaking JPA)
- Ao adicionar campos brasileiros em entidades existentes (`ArchiveFileBean`, `AddressBean`):
  - Utilizar colunas opcionais (`nullable = true`) para que scripts de migração do upstream não quebrem esquemas existentes.
  - Novas tabelas específicas do BR-LAWYER devem adotar o prefixo `br_` (ex: `br_cnj_movimentacao`, `br_djen_publicacao`).

---

## 3. Matriz de Localização e Namespaces Técnicos

| Camada | Namespace / Pacote Upstream | Namespace BR-LAWYER (Aditivo) |
| :--- | :--- | :--- |
| **Domínio Legal / NPU** | `com.jdimension.jlawyer.server` | `com.jdimension.jlawyer.domain.legal.cnj` |
| **Cadastros / Documentos BR** | `com.jdimension.jlawyer.entities` | `com.jdimension.jlawyer.domain.legal.documents` |
| **Integração Judiciária** | `com.jdimension.jlawyer.server.bea` | `com.jdimension.jlawyer.integration.brazil.djen` / `datajud` |
| **Financeiro & Fiscal** | `com.jdimension.jlawyer.invoicing` | `com.jdimension.jlawyer.invoicing.brazil` |
| **UI Components Desktop** | `com.jdimension.jlawyer.client` | `com.jdimension.jlawyer.client.brazil` |

---

## 4. Procedimento Operacional Padrão (SOP) para Sincronização Upstream

### Passo 1: Atualização dos Remotes
```bash
git fetch upstream master
git fetch origin
```

### Passo 2: Criação de Branch Efêmera de Sincronização
```bash
git checkout -b sync/upstream-$(date +%Y%m%d) upstream/master
git merge br-lawyer-dev --no-commit
```

### Passo 3: Resolução de Conflitos Guiada
1. Arquivos `_pt_BR.properties`: Se o upstream adicionou novas chaves nos arquivos `.properties` alemães/ingleses, mesclar automaticamente e replicar as novas chaves para o `_pt_BR.properties`.
2. Classes Java compartilhadas: Se houver conflito em chamadas de inicialização, manter a chamada de inicialização do BR-LAWYER sem remover as correções do upstream.
3. Pom.xml: Aceitar novas dependências do upstream mantendo as versões das dependências consolidadas.

### Passo 4: Validação de Regressão e Compilação
```bash
mvn clean test
```

### Passo 5: Integração na Branch Principal
```bash
git checkout br-lawyer-dev
git merge sync/upstream-$(date +%Y%m%d) --no-ff -m "chore(sync): sincronização com upstream j-lawyer.org ($(date +%Y%m%d))"
git branch -d sync/upstream-$(date +%Y%m%d)
```

---

## 5. Checklist de Verificação de Não-Atrito Pré-Commit

Antes de abrir PR ou mesclar código em `br-lawyer-dev`, o desenvolvedor deve certificar:

- [ ] O diff não contém reformatatação arbitrária de código em arquivos legados do upstream.
- [ ] Novas classes residem em pacotes isolados ou sub-pacotes dedicados.
- [ ] Nenhuma assinatura de método público legado foi alterada ou excluída.
- [ ] Todas as novas strings de UI estão registradas em arquivos `*_pt_BR.properties`.
- [ ] Novas colunas em entidades JPA legadas são anuláveis (`nullable = true`) ou possuem valores default seguros.
- [ ] A compilação completa do reactor Maven (`mvn clean test`) passa com 100% de sucesso.
