# RELATÓRIO DE AUDITORIA DE LICENÇAS, DEPENDÊNCIAS E ARTEFATOS — PROJETO BR-LAWYER

> **Documento de Auditoria Legal e Técnica:** `docs/research/LICENSE_AND_DEPENDENCY_AUDIT.md`  
> **Auditor:** SUBAGENT 2 — LICENSE & DEPENDENCY AUDITOR  
> **Data:** 31 de Agosto de 2026  
> **Repositório Base:** j-lawyer.org v2.8.0 / BR-LAWYER  
> **Licença Principal:** GNU Affero General Public License Version 3 (AGPLv3)

---

## 1. RESUMO EXECUTIVO (EXECUTIVE SUMMARY)

O presente relatório consolida a auditoria jurídica, técnica e de cadeia de suprimentos (*software supply chain*) realizada sobre o código-fonte, configurações de compilação (Maven reactor), binários embutidos e dependências de terceiros do projeto **j-lawyer.org** (workspace `c:\projetos IA\BR-LAWYER\br-lawyer`), visando fundamentar o desenvolvimento do **BR-LAWYER**.

### Principais Conclusões:
1. **Licença Principal do Projeto:** **GNU Affero General Public License Version 3 (AGPLv3)**. Todos os arquivos-fonte Java contêm o cabeçalho integral da AGPLv3. O BR-LAWYER mantém a conformidade estrita com a AGPLv3 como obra derivada livre e de código aberto.
2. **Bloqueador Proprietário Crítico Identificado:** O diretório `j-lawyer-proprietary/` contém o arquivo `j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar` (9.18 MB), um wrapper compilado fechado e proprietário para a rede da Ordem dos Advogados da Alemanha (BRAK - *besonderes elektronisches Anwaltspostfach*). **Classificação: `BLOCKED_FOR_LICENSE_REVIEW / MUST_BE_REMOVED`**. Deve ser totalmente extirpado e substituído no BR-LAWYER pela arquitetura aberta `JudicialSystemAdapter` (DJEN, DataJud, PJe/PDPJ).
3. **Mapeamento de Dependências e Maven Central:**
   - O projeto migrou historicamente de 223+ JARs descontrolados em pastas `lib/` para um reator Maven estruturado.
   - **169 artefatos (CENTRAL_OK)** possuem hash SHA-1 idêntico aos lançamentos canônicos do Maven Central e foram mapeados em `<dependencyManagement>`.
   - **11 artefatos (SHA_MISMATCH)** apresentam divergência de bytes (recompilados/reempacotados localmente, incluindo versões do Log4j 2.17.1, JSch, JSON-Simple, Flyway, Tika e JasperReports).
   - **43 artefatos (NOT_ON_CENTRAL)** permanecem em repositório de arquivos local (`maven-repo/`), abrangendo bibliotecas proprietárias alemãs, módulos JavaFX legados e stubs do WildFly.
4. **Compatibilidade de Licenças de Terceiros:**
   - A esmagadora maioria das dependências utiliza licenças permissivas (**Apache 2.0, MIT, BSD-2/3-Clause**) ou de copyleft fraco (**LGPL 2.1/3.0, EPL 1.0/2.0, CDDL 1.1**), plenamente compatíveis com AGPLv3 em ambiente Java/JVM.
   - O ecossistema de manipulação de PDFs usa **iText Core 9.0.0** (licenciado sob AGPLv3), o que é juridicamente harmonioso com o núcleo AGPLv3 do BR-LAWYER.
5. **Recursos Estáticos e UI:** Fontes (*Exo 2*, *Inter* - SIL OFL 1.1; *Roboto* - Apache 2.0), temas (*FlatLaf* 3.5.4 - Apache 2.0) e ícones (*Crystal SVG* - LGPL; *FileIcons* - MIT) são 100% abertos e seguros para redistribuição.
6. **Lacunas de Localização para o Brasil:** O corretor ortográfico *JOrtho* possui 9 dicionários europeus em formato `.ortho` (faltando `dictionary_pt_BR.ortho`); a camada de faturamento utiliza o padrão alemão *ZUGFeRD/MustangProject* (que deve dar lugar à NFS-e/NF-e); e a camada bancária utiliza o padrão europeu *SEPA XML* (que deve dar lugar ao CNAB 240/400 e PIX).

---

## 2. AUDITORIA DA LICENÇA PRINCIPAL E COPYRIGHT (AGPLv3 AUDIT)

### 2.1 Licença Raiz
- **Arquivo:** `LICENSE` (35.181 bytes, 662 linhas).
- **Licença:** GNU Affero General Public License, Version 3, 19 November 2007 (Copyright © 2007 Free Software Foundation, Inc.).
- **Obrigações-Chave:**
  - **Copyleft Forte (Seção 5):** Qualquer modificação ou obra baseada no programa deve ser integralmente licenciada sob a AGPLv3 como um todo.
  - **Interação Remota via Rede (Seção 13):** Operadores de servidores de rede (aplicação web/REST API/WildFly) são obrigados a disponibilizar o código-fonte correspondente e completo a todos os usuários que interagem com o software via rede.
  - **Avisos Legais Adequados (Seção 5.d):** Interfaces gráficas interativas devem exibir copyright e ausência de garantia.

### 2.2 Cabeçalhos de Arquivos e Atribuição de Autoria
- Prática adotada no código upstream: Todos os arquivos `.java` no projeto contêm como preâmbulo a cópia verbatim da AGPLv3.
- Arquivos de propriedades de internacionalização da interface (`AboutDialog.properties` e `AboutDialog_en.properties`) declaram:
  > `license.long=j-lawyer.org by Jens Kutschke is licensed under a "GNU AFFERO GENERAL PUBLIC LICENSE Version 3" license, source code is available at https://github.com/jlawyerorg.`
- **Módulos Auditados:**
  - `j-lawyer-client` (Swing desktop client): AGPLv3.
  - `j-lawyer-server` (`ejb`, `war`, `server-io`, `j-lawyer-io`, `ear`): AGPLv3.
  - `j-lawyer-server-entities`, `j-lawyer-server-api`, `j-lawyer-server-common`: AGPLv3.
  - `j-lawyer-cloud`, `j-lawyer-fax`, `j-lawyer-invoicing`, `j-lawyer-backupmgr`, `j-lawyer-io-common`: AGPLv3.
  - `j-lawyer-web` (Angular Web UI em `j-lawyer-web/frontend/package.json`): `"license": "AGPL-3.0-only"`.

---

## 3. AUDITORIA DO CONTEÚDO PROPRIETÁRIO: `j-lawyer-proprietary/` E beA

### 3.1 O Binário `j-lawyer-bea-wrapper.jar`
- **Localização:** `j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar`
- **Tamanho:** 9.183.026 bytes (~9,18 MB).
- **Coordenadas Sintéticas:** `jlawyer.thirdparty:j-lawyer-bea-wrapper:0.0.0`
- **Hash SHA-1:** `2009c729079709ccabe054eb8cfaef0cdde06136`
- **Origem e Conteúdo:** Artefato proprietário/fechado fornecido por terceiros/BRAK para interfacear clientes desktop com o serviço de correio eletrônico seguro dos advogados alemães (*besonderes elektronisches Anwaltspostfach* - beA), incluindo rotinas nativas/binárias de criptografia e leitura de smartcards/PKI alemães.
- **Licenciamento:** Fechado / Sem código-fonte disponível no repositório.

### 3.2 Referências e Acoplamento no Código Upstream
1. **Desktop Client (`j-lawyer-client`):**
   - Declarado como dependência em `j-lawyer-client/pom.xml` (linha 519).
   - Classe `com.jdimension.jlawyer.client.bea.BeaAccess` invoca métodos do wrapper e obtém versão via `BeaAccess.getBeaWrapperVersion()`.
   - Diversos diálogos de UI NetBeans (`BeaInboxPanel.java`, `AddBeaRecipientSearchDialog.java`, `BeaIdentitySearchDialog.java`, etc.).
2. **Server-Side REST Evolution (`j-lawyer-server`):**
   - O upstream começou a desacoplar o wrapper do cliente criando o serviço `BeaService.java` (EJB Stateless) e `BeaEndpointV8.java` (JAX-RS REST v8), delegando operações beA para um daemon externo via HTTP (`http://localhost:7080`, projeto "beAstie").
   - Contudo, a dependência direta do JAR proprietário ainda reside no `pom.xml` do cliente e nos scripts de build (`scripts/seed-maven-repo.sh`, `scripts/lib-gav-map.txt`).

### 3.3 Diretriz para o BR-LAWYER: Decomissionamento Total
- **Risco Jurídico:** A redistribuição de binários proprietários opacos junto a código AGPLv3 viola as boas práticas de software livre e cria incerteza de conformidade.
- **Risco Funcional:** O beA é 100% inútil para a jurisdição brasileira.
- **Ação:**
  1. **Remover** o diretório `j-lawyer-proprietary/` da árvore de build e do Git.
  2. **Excluir** a dependência `jlawyer.thirdparty:j-lawyer-bea-wrapper` de `j-lawyer-client/pom.xml` e dos scripts `seed-maven-repo.sh`.
  3. **Substituir** a interface `BeaServiceRemote`/`BeaAccess` pelo subsistema aberto brasileiro `JudicialSystemAdapter` (com os adaptadores `DjenAdapter`, `DataJudAdapter`, `PjeAdapter`, `EsajAdapter`, `EprocAdapter`).

---

## 4. INVENTÁRIO DETALHADO DE DEPENDÊNCIAS E MAPEAMENTO GAV

### 4.1 Categorização Geral das Dependências (223 artefatos analisados)

| Categoria | Quantidade | Descrição | Ação para o BR-LAWYER |
|---|:---:|---|---|
| **CENTRAL_OK** | 169 | SHA-1 local idêntico ao Maven Central canônico | Manter coordenadas oficiais do Maven Central |
| **SHA_MISMATCH** | 11 | JARs divergentes/reempacotados localmente | Substituir por releases oficiais canônicos |
| **NOT_ON_CENTRAL** | 43 | Artefatos residuais não encontrados no Central | Isolar, substituir ou manter em repo limpo |
| **TOTAL** | **223** | Universo de dependências legadas | **Transição para 100% Maven Central canônico** |

### 4.2 Detalhamento dos 11 Artefatos SHA_MISMATCH (Divergências Locais)

1. `commons-beanutils:commons-beanutils:1.8.0` — Reempacotado localmente (SHA: `0c651d51...` vs Central `bf488106...`).
2. `org.flywaydb:flyway-core:11.17.2` — Recompilado localmente (SHA: `55701928...` vs Central `f0a87caf...`).
3. `org.flywaydb:flyway-mysql:11.17.2` — Recompilado localmente (SHA: `a4c05cef...` vs Central `56b1c2a6...`).
4. `net.sf.jasperreports:jasperreports:4.5.1` — Reempacotado localmente (SHA: `31432ac2...` vs Central `383828fb...`).
5. `com.jcraft:jsch:0.1.54` — Reempacotado localmente (SHA: `0d7d8aba...` vs Central `da35843...`).
6. `com.github.cliftonlabs:json-simple:2.3.0` — Reempacotado localmente (SHA: `b4c3335f...` vs Central `f371933...`).
7. `org.apache.logging.log4j:log4j-api:2.17.1` — Recompilado localmente (SHA: `104975c9...` vs Central `d771af8...`).
8. `org.apache.logging.log4j:log4j-core:2.17.1` — Recompilado localmente (SHA: `b4f4bca9...` vs Central `779f60f...`).
9. `org.apache.logging.log4j:log4j-slf4j-impl:2.17.1` — Recompilado localmente (SHA: `8bfdf3e2...` vs Central `84692d4...`).
10. `org.apache.tika:tika-app:1.22` — Reempacotado localmente (SHA: `5a245fb3...` vs Central `b0f63b7...`).
11. `net.lingala.zip4j:zip4j:1.3.2` — Reempacotado localmente (SHA: `3e209559...` vs Central `4ba84e98...`).

*Recomendação BR-LAWYER:* Substituir integralmente os JARs SHA_MISMATCH pelas coordenadas oficiais do Maven Central após verificação de regressão de testes unitários.

### 4.3 Detalhamento dos 43 Artefatos NOT_ON_CENTRAL

1. **JavaFX Runtime Jars (7 artefatos):** `javafx.base`, `javafx.controls`, `javafx.fxml`, `javafx.graphics`, `javafx.media`, `javafx.swing`, `javafx.web`.
   - *Status:* Já obsoletos. O build em Java 17 com BellSoft Liberica Full JDK provê esses módulos nativamente em `jrt:/javafx.*`.
2. **Proprietários / Específicos da Alemanha (7 artefatos):**
   - `j-lawyer-bea-wrapper:0.0.0` (Wrapper beA fechado).
   - `config_schulung:0.0.0` (Configuração de treinamento alemã).
   - `java-sepa-xml:1.0.1` (Gerador de XML de transferências bancárias SEPA europeias).
   - `j-lawyer-cloud:0.0.0` & `j-lawyer-invoicing:0.0.0` (Artefatos internos do reator).
   - `javaee-doc-api:0.0.0` (Stub de javadoc de Java EE).
   - `lu.tudor.santec.i18n:0.0.0` (Biblioteca legada de internacionalização do centro de pesquisa Tudor).
3. **Bibliotecas Open Source Não Publicadas no Central Canônico (14 artefatos):**
   - `bizcal:0.0.6`, `bizcalDemoApplication`, `forms:1.0.6` (Componentes de calendário Swing legados).
   - `ical4j:1.0-beta3` (Versão beta de ical4j).
   - `jai_core:1.1.3`, `jai_codec:0.0.0`, `jai_imageio:0.0.0` (Java Advanced Imaging original da Sun Microsystems).
   - `jcifs:1.3.18` (SMB1 client legado do Samba).
   - `jortho:0.0.0` (Corretor ortográfico Swing original do SourceForge).
   - `libintl:0.0.0` (Gettext Java).
   - `swing-layout:1.0.4` (Layout Swing legado do NetBeans).
   - `eclipselink:2.3.0`, `eclipselink-jpa-modelgen:2.3.0` (Versões antigas do repositório da Fundação Eclipse).
4. **Stubs e Reempacotamentos do WildFly / Java EE (15 artefatos):**
   - `jboss-client:0.0.0`, `wildfly-cli:client`, `concurrent:0.0.0`, `dsn:0.0.0`, `flatlaf:3.5.4-no-natives`, `javax.annotation*`, `jaxb-api-osgi`, `jaxws-api`, `jsr181-api`, `pdftest:9.0.0`, `pop3:0.0.0`, `sam:0.0.0` (EventBus), `swingx:0.0.0`, `webservices-api-osgi`.

---

## 5. MATRIZ DE LICENCIAMENTO DE TERCEIROS E COMPATIBILIDADE AGPLv3

| Família de Licença | Bibliotecas Principais no Projeto | Tipo de Copyleft | Compatibilidade com AGPLv3 | Notas Jurídicas |
|---|---|:---:|:---:|---|
| **Apache License 2.0** | Apache Commons (*lang3, io, collections4, compress, net, math3, vfs2, imaging*), Lucene 9.12.0, PDFBox 2.0.24, POI 4.1.2/5.2.3, Tika 1.22, XMLBeans 3.1.0, XML-RPC 3.1.3, ODF Toolkit (*odfdom, simple-odf*), Log4j2 2.17.2, Mustang Project 2.16.3, FlatLaf 3.5.4, XChart 3.8.2, Libphonenumber 8.12.18, Caldav4j 0.9.2, Jackrabbit WebDAV 2.13.5, Joda-Time 2.1, Jollyday 0.5.10 | Permissivo | **100% Compatível** | Permite combinação, sublicenciamento no todo sob AGPLv3, preservando avisos de copyright e NOTICEs. |
| **MIT License** | SLF4J 1.7.30/1.7.36, JSch 0.1.54, FileIcons, CommonMark 0.23.0 | Permissivo | **100% Compatível** | Licença ultra-permissiva. Exige apenas retenção de copyright. |
| **BSD 2-Clause / 3-Clause** | ez-vcard 0.11.2 (BSD-3), JavaSysMon (BSD-2), Hamcrest 1.3 (BSD-3) | Permissivo | **100% Compatível** | Compatível com copyleft forte em obras agregadas e derivadas. |
| **LGPL v2.1 / v3.0** | JasperReports 4.5.1, Hibernate Core 5.3.28.Final, JCalendar 1.3.2, OpenPDF 1.3.22, SwingX, JOrtho, Crystal SVG Icons | Copyleft Fraco | **100% Compatível** | Vinculação dinâmica (JARs no classpath da JVM). Código-fonte das bibliotecas LGPL não é modificado; AGPLv3 rege a aplicação consumidora. |
| **EPL 1.0/2.0 & CDDL 1.1** | EclipseLink 2.7.7, Jersey 2.5.1, GlassFish HK2, Jakarta Persistence API 2.2.3, Jakarta Mail 1.6.8, Jakarta Activation 1.2.2, JAX-RS specs | Copyleft Fraco / Arquivo | **100% Compatível** | Arquivos sob EPL/CDDL mantêm suas licenças em nível de módulo; interoperam livremente com código AGPLv3. |
| **GNU AGPLv3** | **iText Core 9.0.0 Suite** (*barcodes, cleanup, commons, font-asian, forms, hyph, io, kernel, layout, pdfa, pdfua, sign, styled-xml-parser, svg*) | Copyleft Forte | **Perfeita Harmonia** | O iText 9.0.0 é licenciado sob AGPLv3. Como o BR-LAWYER é 100% AGPLv3, a combinação é totalmente legal e livre de conflitos de licença. |
| **GPLv3 / AGPLv3** | Nextcloud API Client (`org.aarboard.nextcloud:nextcloud-api:13.1.0`) | Copyleft Forte | **100% Compatível** | GPLv3 permite combinação com AGPLv3 sob os termos da Seção 13 da AGPLv3. |
| **SIL Open Font License 1.1** | Exo 2 Font Family (OTF/TTF), Inter Font (`flatlaf-fonts-inter:3.19`) | Licença de Fonte Aberta | **100% Compatível** | Permite incorporação livre em softwares livres e proprietários (com cláusula de não venda isolada da fonte). |

---

## 6. AUDITORIA DE RECURSOS ESTÁTICOS, FONTES, ÍCONES E MODELOS

### 6.1 Fontes Tipográficas
- **Exo 2:** Localizada em `j-lawyer-client/src/main/resources/fonts/exo2/` (18 arquivos `.otf` cobrindo variações Thin a Black + itálicos, além de `exo2-bold.ttf`). Criada por Natanael Gama sob **SIL Open Font License 1.1**.
- **Inter:** Fornecida pelo artefato `com.formdev:flatlaf-fonts-inter:3.19` sob **SIL Open Font License 1.1**.
- **Roboto:** Fornecida pelo artefato `com.formdev:flatlaf-fonts-roboto:2.137` sob **Apache License 2.0**.

### 6.2 Ícones e Elementos Visuais
- **Crystal SVG:** Coleção clássica de ícones criada por Everaldo Coelho, licenciada sob **LGPL**. Localizada nas pastas de recursos `icons/`, `icons16/`, `icons32/`, `icons128/`, `avatar16/`, `avatar32/`, `modules16/`, `modules32/`.
- **FileIcons:** Criada por braver sob **MIT License**.
- **FlatLaf Vector Icons:** Ícones modernos em SVG renderizados dinamicamente pelo FlatLaf sob **Apache 2.0**.

### 6.3 Modelos de Relatórios e Templates
- **Relatórios JasperReports (`.jrxml`):** Em `j-lawyer-client/src/main/resources/reports/`:
  - `archivefile.jrxml`, `archivefile_address_detail.jrxml`, `archivefile_cost_detail.jrxml`, `archivefile_review_detail.jrxml`, `archivefile_review_event_detail.jrxml`, `reviews.jrxml`, `reviews_detail.jrxml`.
- **Templates de Exportação HTML e Email:** Em `j-lawyer-server/j-lawyer-server-ejb/src/main/resources/templates/`:
  - `caseexport/` (HTML5 responsivo, CSS moderno e scripts JS `app.js`, `history.js`).
  - `email/template.html` (Template padrão de email).
  - `smart/smarttemplate.groovy` (Motor Groovy de templates inteligentes).

### 6.4 Dicionários Ortográficos (JOrtho)
- Em `j-lawyer-client/src/main/resources/dictionaries/`:
  - `dictionary_de.ortho` (Alemão), `dictionary_en.ortho` (Inglês), `dictionary_es.ortho` (Espanhol), `dictionary_fr.ortho` (Francês), `dictionary_it.ortho` (Italiano), `dictionary_nl.ortho` (Holandês), `dictionary_pl.ortho` (Polonês), `dictionary_ru.ortho` (Russo), `dictionary_ar.ortho` (Árabe).
- **Lacuna Detectada para o BR-LAWYER:** Falta o `dictionary_pt_BR.ortho` para suporte nativo à revisão ortográfica em Português do Brasil no editor do cliente desktop.

---

## 7. IDENTIFICAÇÃO DE RISCOS E BLOQUEIOS (`BLOCKED_FOR_LICENSE_REVIEW`)

```
[CRÍTICO / BLOQUEADO] 🔴
  └── j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar
      - Motivo: Binário fechado e proprietário da Ordem dos Advogados da Alemanha (BRAK).
      - Risco: Violação de princípios open-source, opacidade, irrelevância para o Brasil.
      - Ação: REMOÇÃO TOTAL / SUBSTITUIÇÃO POR ADAPTERS PJe, DJEN, DataJud.

[ALTO RISCO / ESPECÍFICO DA EUROPA] 🟠
  ├── java-sepa-xml:1.0.1
  │   - Motivo: Gerador bancário proprietário SEPA (Single Euro Payments Area).
  │   - Ação: Remover e substituir por geradores abertos de CNAB 240/400 e PIX.
  └── config_schulung:0.0.0
      - Motivo: Configuração local de treinamento alemão.
      - Ação: Remover do classpath de distribuição.

[MÉDIO RISCO / DÉBITO TÉCNICO DE SUPPLY CHAIN] 🟡
  ├── jai_core:1.1.3 & jai_codec / jai_imageio (Sun Proprietary Binary License)
  │   - Ação: Migrar integralmente para com.github.jai-imageio:jai-imageio-core (Open Source).
  ├── jcifs:1.3.18 (SMB1 legacy)
  │   - Ação: Atualizar para jcifs-ng ou smbj (SMB2/SMB3 seguro).
  └── 11 Jars SHA_MISMATCH (Log4j 2.17.1, JSch, JSON-Simple, Flyway, JasperReports, Tika)
      - Ação: Migrar para os artefatos oficiais canônicos do Maven Central.
```

---

## 8. RECOMENDAÇÕES ESTRATÉGICAS DE ENGENHARIA PARA O BR-LAWYER

1. **Exclusão de Binários Proprietários do Repositório:**
   - Excluir a pasta `j-lawyer-proprietary/` e seus scripts associados.
   - Remover as referências do `pom.xml` raiz e de `j-lawyer-client/pom.xml`.
2. **Implementação da Arquitetura `JudicialSystemAdapter`:**
   - Substituir o acoplamento do `BeaService`/`BeaAccess` por interfaces de integração judiciária brasileira:
     - `DjenAdapter` (Consumo via REST do Diário de Justiça Eletrônico Nacional / ComunicaAPI do CNJ).
     - `DataJudAdapter` (Consulta pública processual e metadados processuais CNJ).
     - `PjeAdapter` / `EsajAdapter` / `EprocAdapter` (Peticionamento e consulta nos tribunais estaduais e federais).
3. **Nacionalização do Módulo Financeiro e Bancário:**
   - Remover `java-sepa-xml` e substituir pelo gerador de remessa/retorno bancário brasileiro **CNAB 240 / 400** e gerador de QR Code / chave **PIX** (Payload EMVCo).
   - Substituir o padrão alemão ZUGFeRD (`j-lawyer-invoicing`) por emissor de **NFS-e Nacional** (Padrão ABRASF / Receita Federal) e **DANFE/NF-e**.
4. **Adição dos Recursos de Localização Brasileira:**
   - Gerar e incluir o arquivo `dictionary_pt_BR.ortho` no diretório `dictionaries/` do cliente.
   - Fornecer bundles completos `*_pt_BR.properties` para todos os formulários e diálogos do cliente e mensagens do servidor.
   - Atualizar templates de relatórios JasperReports para moeda Real (R$) e formatação de data/hora no padrão brasileiro (`dd/MM/yyyy`).
5. **Limpeza da Cadeia de Suprimentos Maven:**
   - Eliminar os 43 artefatos `jlawyer.thirdparty` residuais, garantindo que todas as dependências venham diretamente do Maven Central com hashes auditáveis e assinaturas PGP.
