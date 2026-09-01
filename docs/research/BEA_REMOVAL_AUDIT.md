# RELATÓRIO DE AUDITORIA: REMOÇÃO DO beA E ARTEFATOS PROPRIETÁRIOS ALEMÃES

> **Documento de Referência:** `docs/research/BEA_REMOVAL_AUDIT.md`  
> **Subagente:** SUBAGENT PROPRIETARY / BEA REMOVAL AUDITOR  
> **Data:** 31 de Agosto de 2026  
> **Repositório Base:** j-lawyer.org v2.8.0 / BR-LAWYER  

---

## 1. RESUMO EXECUTIVO

A presente auditoria realizou uma varredura completa em todo o ecossistema do projeto (código-fonte Java, templates de formulários Swing `.form`, REST endpoints, DTOs, interfaces EJB, arquivos POM Maven, scripts de build, banco de dados e frontend Angular) para mapear e isolar todas as dependências e referências ao **beA** (*besonderes elektronisches Anwaltspostfach* - correio eletrônico seguro da Ordem dos Advogados da Alemanha / BRAK) e outros artefatos proprietários alemães.

### Principais Conclusões:
1. **Natureza do Wrapper Proprietário:** O binário `j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar` (9.18 MB, hash SHA-1 `2009c7290797...`) é um artefato fechado/proprietário da BRAK. No estágio atual do repositório, ele **já foi desvinculado dos arquivos `pom.xml` principais**, mas ainda reside referenciado em scripts legados (`lib-gav-map.txt`, `lib-gav-map-central.tsv`, `seed-maven-repo.sh`).
2. **Camada de Comunicação no Servidor (`j-lawyer-server`):** O upstream desacoplou o wrapper embutido migrando para chamadas HTTP REST delegadas ao microservice local **beAstie** (`http://localhost:7080`) através de `BeaService.java` (Stateless EJB), `BeaSessionRegistry.java` (Singleton EJB) e `BeaEndpointV8.java` (`/v8/bea`).
3. **Acoplamento no Cliente Desktop (`j-lawyer-client`):** Há **44 arquivos** dedicados ao beA no pacote `com.jdimension.jlawyer.client.bea` (22 classes Java, 13 formulários `.form`, 1 certificado de teste `.p12` e 8 ícones `.png`). Além disso, existem **27 arquivos Java na UI** com acoplamento direto via `import com.jdimension.jlawyer.client.bea.*` (incluindo `JKanzleiGUI`, `Main`, `AboutDialog`, `AddressPanel`, `ArchiveFilePanel`, etc.).
4. **Dependências em `lib/bea/`:** Os 25 arquivos JARs presentes em `j-lawyer-client/lib/bea/` foram 100% mapeados para coordenadas canônicas do **Maven Central** (`CENTRAL_OK`), e os JARs físicos na pasta são resíduos da era Ant/NetBeans.
5. **Risco de `NoClassDefFoundError` / `ClassNotFoundException`:** Não há chamadas diretas a classes internas do `.jar` proprietário no classpath do cliente ou servidor. Todavia, a remoção inadvertida do pacote `com.jdimension.jlawyer.client.bea` ou dos EJBs sem refatoração prévia causará:
   - `ClassNotFoundException` no boot do cliente desktop ao registrar o módulo via reflexão (`EditorsRegistry` carregando `"com.jdimension.jlawyer.client.bea.BeaInboxPanel"` registrado em `Main.java:997`);
   - Erros de compilação e falhas de injeção EJB no WildFly (`ScheduledTasksService` injetando `BeaSessionRegistry`);
   - `NamingException` em endpoints REST `/v8/bea`.
6. **Substituição Brasileira Limpa:** Desenvolvemos a arquitetura aberta `JudicialSystemAdapter` (composta por `DjenAdapter`, `DataJudAdapter` e `PjeAdapter`/PDPJ), que substitui perfeitamente a semântica do beA pelas intimações oficiais do DJEN (CNJ ComunicaAPI), metadados do DataJud e peticionamento eletrônico ICP-Brasil.

---

## 2. INVENTÁRIO COMPLETO DE REFERÊNCIAS AO beA NO CÓDIGO-FONTE

### 2.1 Módulo `j-lawyer-server-api` (Interfaces EJB e DTOs)
- **Interface Remota:**
  - `com.jdimension.jlawyer.services.BeaServiceRemote.java` (Interface EJB `@Remote` contendo 35 métodos cobrindo Login, Logout, Postboxes, Folders, Mensagens, Anexos, Validação de Assinaturas, SAFE Identity Search, e-EB Confirmação/Rejeição).
- **DTOs REST em `com.jdimension.jlawyer.services.bea.rest` (24 classes):**
  1. `BeaAttachment.java`
  2. `BeaAttachmentValidationResult.java`
  3. `BeaCertificateInfoRequest.java`
  4. `BeaEebRequestAttributes.java`
  5. `BeaEebResponseAttributes.java`
  6. `BeaFolder.java`
  7. `BeaIdentity.java`
  8. `BeaIdentitySearchRequest.java`
  9. `BeaListItem.java`
  10. `BeaLoginResult.java`
  11. `BeaMessage.java`
  12. `BeaMessageExport.java`
  13. `BeaMessageFilter.java`
  14. `BeaMessageHeader.java`
  15. `BeaMessageJournalEntry.java`
  16. `BeaMessageValidationResult.java`
  17. `BeaMoveMessageRequest.java`
  18. `BeaPostbox.java`
  19. `BeaProcessCard.java`
  20. `BeaProcessCardEntry.java`
  21. `BeaRecipient.java`
  22. `BeaSaveDraftRequest.java`
  23. `BeaSendMessageRequest.java`
  24. `BeaVerificationResult.java`

### 2.2 Módulo `j-lawyer-server` (Backend EJB & REST)
- **`j-lawyer-server-ejb`:**
  - `com.jdimension.jlawyer.services.BeaService.java`: Implementa `BeaServiceRemote` e `BeaServiceLocal`. Delega chamadas HTTP REST para `http://localhost:7080` (beAstie).
  - `com.jdimension.jlawyer.services.BeaServiceLocal.java`: Interface `@Local` do EJB.
  - `com.jdimension.jlawyer.services.BeaSessionRegistry.java`: Singleton EJB (`@Singleton`, `@ConcurrencyManagement`) gerenciando sessões e tokens JWT/Bearer de usuários no beAstie.
  - `com.jdimension.jlawyer.services.ScheduledTasksService.java`: Contém injeção `@EJB private BeaSessionRegistry sessionRegistry;` e tarefa agendada a cada 15 min `cleanupStaleBeaSessions()`.
  - `com.jdimension.jlawyer.services.ScheduledTasksServiceLocal.java`: Declaração do método `cleanupStaleBeaSessions()`.
- **`j-lawyer-server-io` (REST API):**
  - `org.jlawyer.io.rest.v8.BeaEndpointV8.java`: JAX-RS REST endpoint no path `/v8/bea` (38 endpoints HTTP GET/POST/PUT/DELETE) delegando para `BeaServiceLocal`.
- **`j-lawyer-server-entities` (JPA & Migrations):**
  - `com.jdimension.jlawyer.persistence.AddressBean.java`: Campo `beaSafeId` (coluna `beaSafeId`), getter/setter `getBeaSafeId()` / `setBeaSafeId()`.
  - `com.jdimension.jlawyer.persistence.AppUserBean.java`: Campos `beaCertificate` (coluna `beaCertificate`, `MEDIUMBLOB`) e `beaCertificatePassword` (coluna `beaCertificatePassword`), getters/setters correspondentes.
  - `db.migration.V1_15_0_9__CalculateBeaPasswordHashes.java`: Migração legada Flyway para hash de senhas de certificados beA.
  - SQL Migrations históricas: `V2_6_0_6__BooleanColumns.sql`, `V3_4_0_3__ModifyBeaCertificatePassword.sql`, `V3_5_0_3__RemoveBeaAutoLogin.sql`.
- **`j-lawyer-server-common`:**
  - `com.jdimension.jlawyer.server.services.settings.ServerSettingsKeys.java`: Constantes `SERVERCONF_BEAMODE`, `SERVERCONF_BEAENDPOINT`, `SERVERCONF_BEAENABLEDVERSIONS`.
  - `com.jdimension.jlawyer.server.services.settings.UserSettingsKeys.java`: Constantes `CONF_BEA_LASTUSEDTEMPLATE`, `CONF_BEA_LASTUSEDMAILBOX`, `CONF_BEA_POSTBOXORDER`.
  - `com.jdimension.jlawyer.server.utils.ServerFileUtils.java`: Tratamento da extensão de arquivo `.bea`.

### 2.3 Módulo `j-lawyer-client` (Cliente Desktop Swing)
- **Pacote dedicado `com.jdimension.jlawyer.client.bea` (44 arquivos):**
  - **Classes Java (22):** `AddBeaRecipientSearchDialog`, `BeaAccess` (Singleton cliente), `BeaAttachmentMetadata`, `BeaCheckTimerTask`, `BeaEebDisplayDialog`, `BeaFolderTreeCellRenderer`, `BeaIdentitySearchDialog`, `BeaIdentitySearchRowIdentifier`, `BeaIdentitySearchThread`, `BeaInboxPanel` (Painel principal da UI), `BeaJournalEventTypes`, `BeaMessageContentUI`, `BeaMessageTableCellRenderer`, `BeaSignaturesVerificationDialog`, `BeaUtils`, `EebRejectDialog`, `IdentityPanel`, `LoadBeaFolderAction`, `SaveBeaMessageAction`, `SelectLegalAuthorityDialog`, `SendBeaMessageAction`, `SendBeaMessageFrame`, `SortedBeaFolderNode`, `ViewBeaDialog`.
  - **Formulários NetBeans `.form` (13):** `AddBeaRecipientSearchDialog.form`, `BeaEebDisplayDialog.form`, `BeaIdentitySearchDialog.form`, `BeaInboxPanel.form`, `BeaMessageContentUI.form`, `BeaSignaturesVerificationDialog.form`, `EebRejectDialog.form`, `IdentityPanel.form`, `SelectLegalAuthorityDialog.form`, `SendBeaMessageFrame.form`, `ViewBeaDialog.form`, etc.
  - **Binário de Teste / Certificado (1):** `Zert01.p12` (Certificado PKCS#12 de teste beA).
  - **Recursos Gráficos `.png` (8):** `send-failed.png`, `send-invalid.png`, `send-success.png`, `send-unknown.png`, `signature-fail.png`, `signature-partial.png`, `signature-success.png`, `signature-unknown.png`.
- **Classes Swing com Acoplamento Externo ao Pacote `bea`:**
  - `AboutDialog.java`: Linha 700 invoca `BeaAccess.getBeaWrapperVersion()`.
  - `JKanzleiGUI.java` / `.form`: Menu `mnuBeaSettings` ("beA (Anwaltspostfach)"), tooltip `lblBeaStatus`, checagem de saída `BeaAccess.getInstance().isOutboxEmpty(...)`.
  - `Main.java`: Linhas 996-1010 registram módulo `mod.comm.bea` com editor class `"com.jdimension.jlawyer.client.bea.BeaInboxPanel"` e status event `Event.TYPE_BEASTATUS`.
  - `DesktopPanel.java`: Inicialização de `BeaCheckTimerTask`, subscrição ao `Event.TYPE_BEASTATUS`.
  - `AddressPanel.java` / `.form`: Painel de SAFE-ID beA e botão para enviar mensagem beA.
  - `InvolvedPartyEntryPanel.java`: Painel de envolvidos no processo com lookup SAFE-ID beA.
  - `ArchiveFilePanel.java`: Botão e ações para enviar mensagem beA a partir da pasta do processo.
  - `BeaPanel.java` / `.form` (em `editors/documents/viewer/`): Visualizador de arquivos `.bea`.
  - `XjustizPanel.java` & `XjustizLauncher.java`: Visualizador de XML do tribunal alemão e e-EB.
  - `BEAInternalLauncher.java`: Launcher de abertura de arquivos `.bea`.
  - `ImportCourtsFromBeaDialog.java` & `ImportCourtsFromBeaThread.java`: Importação da base de tribunais alemães via beA.
  - `UserAdministrationDialog.java`: Diálogo de configuração do certificado `.p12` e senha do beA do usuário.
  - `CreateNewAddressPanel.java`: Campo SAFE-ID beA ao cadastrar endereço.
  - `BugReportDialog.java`: Diagnóstico de versão do wrapper beA no relatório de erro.
  - `ClientSettings.java`: Constantes de configuração `CONF_BEA_*` e `CONF_BEASEND_*`.
  - `JLawyerServiceLocator.java`: Método `lookupBeaServiceRemote()`.
  - `Event.java` & `BeaStatusEvent.java`: `Event.TYPE_BEASTATUS = 80`.

### 2.4 Módulo `j-lawyer-web` (Web Frontend Angular)
- **Diretório `j-lawyer-web/frontend/src/app/bea/`:**
  - `bea.component.ts`, `bea.service.ts`, `bea.models.ts`, `bea-inbox.component.ts`, `bea-compose.component.ts`, `bea-viewer.component.ts`, `bea-bulk-save.component.ts`.
- **Roteamento `app.routes.ts`:** Rota `bea: () => import('./bea/bea.component')`.
- **i18n (`de.json` / `en.json`):** Chaves `bea.*`, `beaSettings`, `beaPresent`, `beaAbsent`, `beaFile`, `beaPassword`, `beaUpload`, `beaRemove`, `beaCompose.*`.

---

## 3. AUDITORIA DE SCRIPTS, POMs E DEPENDÊNCIAS DE TEMPO DE EXECUÇÃO

### 3.1 Referências em Arquivos de Configuração do Maven e Scripts
1. **`pom.xml` (Raiz e Submódulos):**
   - No `pom.xml` raiz e em `j-lawyer-client/pom.xml`, o artefato `jlawyer.thirdparty:j-lawyer-bea-wrapper` **já foi desativado/removido** do bloco de dependências ativas.
   - Em `j-lawyer-cloud/pom.xml` (linha 15): Comentário residual `<!-- jackson conflicts with the version that is part of bea-wrapper, therefore fix its version -->`.
   - Em `j-lawyer-client/pom.xml` (linha 184): Dependência de `jlawyer.thirdparty:config_schulung:0.0.0` (JAR de configuração de ambiente de treinamento da BRAK/Alemanha).
2. **Scripts em `scripts/`:**
   - `scripts/lib-gav-map.txt`: Linhas 10 a 34 mapeiam os 25 JARs de `j-lawyer-client/lib/bea/` e a linha 64 mapeia `/j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar => jlawyer.thirdparty:j-lawyer-bea-wrapper:0.0.0`.
   - `scripts/lib-gav-map-central.tsv`: Linha 195 lista `jlawyer.thirdparty:j-lawyer-bea-wrapper:0.0.0` como `NOT_ON_CENTRAL` (hash `2009c729079709ccabe054eb8cfaef0cdde06136`). Linha 188 lista `config_schulung:0.0.0`.
   - `scripts/seed-maven-repo.sh`: Comentários nas linhas 5 e 8 citam o wrapper beA e varre pastas `lib/` e `libs/`.
3. **Scripts de Release (`release-nightly.sh`, `release-nightly-noclean.sh`):**
   - Comentários na linha 12 citam `beA config_schulung.jar`.

### 3.2 Bibliotecas em `j-lawyer-client/lib/bea/` e Stubs
- O diretório `j-lawyer-client/lib/bea/` contém **25 JARs** e 1 arquivo `nblibraries.properties`.
- **Status de Verificação:** Todos os 25 JARs (`guava`, `pdfbox`, `log4j2`, `jaxb-runtime`, `txw2`, `stax-ex`, `checker-qual`, `istack-commons-runtime`, etc.) são bibliotecas padrão de código aberto cujos hashes coincidem rigorosamente com o Maven Central (`CENTRAL_OK`).
- **Diagnóstico:** Esses arquivos físicos são obsoletos e redundantes, pois o build Maven atual já baixa tais dependências diretamente do Maven Central através do `<dependencyManagement>`. Podem ser excluídos com segurança junto com a pasta `lib/bea/`.

---

## 4. IMPACTO DA REMOÇÃO DO WRAPPER E ANÁLISE DE EXCEÇÕES EM RUNTIME

### 4.1 O Wrapper Físico `j-lawyer-bea-wrapper.jar`
- O arquivo `j-lawyer-proprietary/libs/j-lawyer-bea-wrapper.jar` (9.18 MB) contém classes de uma biblioteca fechada desenvolvida pela Westernacher/BRAK.
- **Impacto no Build:** A exclusão física do diretório `j-lawyer-proprietary/` **NÃO quebra a compilação atual via Maven (`mvn clean install`)**, pois o reactor Maven não possui mais nenhuma dependência ativa apontando para `jlawyer.thirdparty:j-lawyer-bea-wrapper`.
- **Impacto no `seed-maven-repo.sh`:** O script deixará de gerar a coordenada fantasma em `maven-repo/`, o que é o comportamento desejado.

### 4.2 Verificação de Riscos de `ClassNotFoundException` e `NoClassDefFoundError`

| Ponto de Invocação | Código Afetado | Comportamento se o beA/Wrapper for Removido sem Refatoração | Ação Preventiva Necessária |
|---|---|---|---|
| **Diálogo Sobre (`AboutDialog`)** | `BeaAccess.getBeaWrapperVersion()` | Seguro. `BeaAccess` possui bloco `try/catch(Exception)` retornando `"unknown"`. | Remover linha do `AboutDialog.java` e substituir por versões dos adaptadores brasileiros. |
| **Boot do Cliente Desktop** | `Main.java:997` & `EditorsRegistry.java:719` | **CRÍTICO: `ClassNotFoundException`** em tempo de execução ao tentar instanciar `"com.jdimension.jlawyer.client.bea.BeaInboxPanel"` via reflexão. | Atualizar `Main.java` registrando a nova classe `JudicialInboxPanel`. |
| **Timer de Background do Cliente** | `DesktopPanel.java:859` (`BeaCheckTimerTask`) | **Erro de Compilação / Runtime** se a classe `BeaCheckTimerTask` for excluída sem desacoplar o timer do `DesktopPanel`. | Substituir `BeaCheckTimerTask` por `JudicialCheckTimerTask` (polling DJEN). |
| **Abertura de Casos e Contatos** | `AddressPanel`, `InvolvedPartyEntryPanel`, `ArchiveFilePanel` | **Erros de Compilação** devido a imports estáticos de classes do pacote `client.bea`. | Substituir campos beA pelos campos brasileiros (OAB, CPF/CNPJ, Peticionamento). |
| **Agendador de Tarefas do Servidor** | `ScheduledTasksService.java:695` | **FALHA NO DEPLOY DO WILDFLY**: `@EJB BeaSessionRegistry` não encontrará o EJB se for excluído sem atualizar a classe. | Refatorar `ScheduledTasksService` substituindo a limpeza de sessões beA pelo pooling de comunicações DJEN. |
| **Endpoints REST Servidor** | `BeaEndpointV8.java` (`/v8/bea`) | **`NamingException` / HTTP 500** caso o JNDI do `BeaServiceLocal` seja removido sem atualizar o endpoint. | Descontinuar `/v8/bea` e criar `/v8/judicial` para o frontend web. |

---

## 5. PLANO DE DESACOPLAMENTO E TRANSIÇÃO: `BeaService` → `JudicialSystemAdapter`

```
                                ARQUITETURA BR-LAWYER: JUDICIAL INTEGRATION
                                
   ┌───────────────────────────┐                     ┌───────────────────────────┐
   │    j-lawyer-client        │                     │      j-lawyer-web         │
   │  (Swing Desktop Client)   │                     │   (Angular Web UI)        │
   └─────────────┬─────────────┘                     └─────────────┬─────────────┘
                 │                                                 │
                 │ Remote EJB Lookup                               │ REST JSON (/v8/judicial)
                 ▼                                                 ▼
   ┌─────────────────────────────────────────────────────────────────────────────┐
   │                            j-lawyer-server                                  │
   │  ┌───────────────────────────────────────────────────────────────────────┐  │
   │  │              JudicialIntegrationService (Stateless EJB)               │  │
   │  └──────────────────────────────────┬────────────────────────────────────┘  │
   │                                     │ Invoca                                │
   │                                     ▼                                       │
   │  ┌───────────────────────────────────────────────────────────────────────┐  │
   │  │             <<interface>> JudicialSystemAdapter (SPI)                 │  │
   │  │   + fetchCommunications(LawyerCredentials, Criteria): List<JudComm>   │  │
   │  │   + queryProcessMetadata(ProcessNumber): JudicialProcessDTO           │  │
   │  │   + sendPetition(PetitionRequest): ProtocolReceiptDTO                 │  │
   │  │   + confirmReceipt(NoticeId, LawyerCredentials): NoticeReceiptDTO     │  │
   │  └───────┬──────────────────────────┬──────────────────────────┬─────────┘  │
   └──────────┼──────────────────────────┼──────────────────────────┼────────────┘
              │                          │                          │
              ▼                          ▼                          ▼
   ┌──────────────────────┐   ┌──────────────────────┐   ┌──────────────────────┐
   │     DjenAdapter      │   │    DataJudAdapter    │   │      PjeAdapter      │
   │ (CNJ ComunicaAPI)    │   │ (CNJ DataJud API)    │   │ (PDPJ-Br / MNI WS)   │
   │ - Intimações diárias │   │ - Metadados TPU/CNJ  │   │ - Peticionamento A1  │
   │ - Publicações OAB    │   │ - Consulta pública   │   │ - Protocolo e Prazos │
   └──────────────────────┘   └──────────────────────┘   └──────────────────────┘
```

### 5.1 Especificação da Nova Interface `JudicialSystemAdapter`
Criar no módulo `j-lawyer-server-api` o pacote `com.jdimension.jlawyer.services.judicial`:
- **Interface SPI `JudicialSystemAdapter`:**
  - `List<JudicialCommunicationDTO> fetchCommunications(JudicialCredentials credentials, CommunicationFilter filter) throws JudicialException;`
  - `JudicialProcessDTO getProcessDetails(String processNumber) throws JudicialException;`
  - `PetitionReceiptDTO sendPetition(PetitionSubmission petition) throws JudicialException;`
  - `NoticeReceiptDTO acknowledgeCommunication(String communicationId, JudicialCredentials credentials) throws JudicialException;`
  - `boolean validateCertificate(byte[] certificateBytes, String password) throws JudicialException;`
- **Adaptadores Especializados:**
  1. **`DjenAdapter`:** Ingestão de publicações judiciais via **ComunicaAPI** do CNJ (`https://comunicaapi.pje.jus.br/api/v1/comunicacao`), realizando busca por número de OAB e UF.
  2. **`DataJudAdapter`:** Consulta de dados do processo via **API Pública DataJud** do CNJ (`https://api-publica.datajud.cnj.jus.br/`), trazendo movimentações e classes padronizadas da TPU.
  3. **`PjeAdapter` / `PdpjAdapter`:** Conector MNI 2.2.2 / PDPJ-Br para tribunais sob PJe, e-SAJ, eproc e Projudi para envio de petições assinadas com certificado ICP-Brasil (PKCS#12 / A1).

### 5.2 Roteiro de Execução Técnica (Fases de Implementação)

#### Fase 1: Limpeza e Eliminação Física de Resíduos Alemães
1. Excluir pasta `j-lawyer-client/lib/bea/` (25 JARs legados) e `j-lawyer-client/lib/config_schulung.jar`.
2. Remover dependência `config_schulung` de `j-lawyer-client/pom.xml`.
3. Limpar referências ao `j-lawyer-bea-wrapper.jar` em `scripts/lib-gav-map.txt`, `scripts/lib-gav-map-central.tsv` e `scripts/seed-maven-repo.sh`.
4. Atualizar comentários residuais em `j-lawyer-cloud/pom.xml` e `.github/dependabot.yml`.

#### Fase 2: Refatoração do Banco de Dados e Migrations Flyway
1. Criar migration `V3_6_0_0__MigrateBeaToJudicialIntegration.sql`:
   - `ALTER TABLE security_users CHANGE beaCertificate digitalCertificate MEDIUMBLOB;`
   - `ALTER TABLE security_users CHANGE beaCertificatePassword digitalCertificatePassword VARCHAR(150) BINARY;`
   - `ALTER TABLE AddressBean CHANGE beaSafeId oabNumber VARCHAR(50);`
   - `ALTER TABLE AddressBean ADD oabUf VARCHAR(2);`
   - `DELETE FROM ServerSettingsBean WHERE settingKey LIKE 'jlawyer.server.bea.%';`
   - `INSERT INTO ServerSettingsBean (settingKey, settingValue) VALUES ('brlawyer.judicial.djen.enabled', 'true');`
   - `INSERT INTO ServerSettingsBean (settingKey, settingValue) VALUES ('brlawyer.judicial.datajud.enabled', 'true');`
   - `UPDATE server_options SET value = 'Enviar via PJe / Protocolo' WHERE optionGroup = 'document.tags' AND value = 'versenden via beA';`

#### Fase 3: Backend e Server API
1. Criar DTOs e interfaces em `j-lawyer-server-api: com.jdimension.jlawyer.services.judicial.*`.
2. Implementar `JudicialIntegrationService` (EJB) e adaptadores `DjenAdapter`, `DataJudAdapter`, `PjeAdapter` em `j-lawyer-server-ejb`.
3. Atualizar `ScheduledTasksService.java` para executar `pollDjenCommunications()` (agendamento diário às 06:00 e 13:00) em substituição a `cleanupStaleBeaSessions()`.
4. Criar endpoint JAX-RS `JudicialEndpointV8.java` (`/v8/judicial`) em `j-lawyer-server-io` e depreciar `BeaEndpointV8.java`.

#### Fase 4: Refatoração do Cliente Desktop Swing (`j-lawyer-client`)
1. Excluir o pacote `com.jdimension.jlawyer.client.bea` e criar `com.jdimension.jlawyer.client.judicial`.
2. Criar `JudicialInboxPanel.java` (Painel de Intimações do DJEN com filtros por data, tribunal, processo e status de leitura).
3. Atualizar `Main.java` substituindo o registro do módulo beA por:
   ```java
   ModuleMetadata judicial = new ModuleMetadata("Comunicações Judiciais (DJEN)");
   judicial.setEditorClass("com.jdimension.jlawyer.client.judicial.JudicialInboxPanel");
   judicial.setFullName("Intimações e Diário Oficial");
   judicial.setEditorName("DJEN / PJe");
   judicial.setModuleName("Processos");
   ```
4. Atualizar `JKanzleiGUI.java`:
   - Substituir `mnuBeaSettings` por `mnuJudicialSettings` ("Integração Judiciária (DJEN / DataJud / PJe)").
   - Substituir `lblBeaStatus` por `lblJudicialStatus` ("Novas intimações no DJEN").
5. Atualizar `AddressPanel.java` e `InvolvedPartyEntryPanel.java`:
   - Substituir campos de SAFE-ID por campos OAB (Número e UF) e CPF/CNPJ com consulta e validação.
6. Atualizar `AboutDialog.java` removendo `BeaAccess.getBeaWrapperVersion()`.

#### Fase 5: Refatoração do Frontend Web (`j-lawyer-web`)
1. Substituir `src/app/bea/` por `src/app/judicial/` (`judicial-inbox.component.ts`, `judicial.service.ts`).
2. Atualizar rota em `app.routes.ts` de `bea` para `judicial`.
3. Ajustar formulários de usuário para upload de certificado digital ICP-Brasil A1 (`.pfx` / `.p12`) e chave de API do CNJ.
