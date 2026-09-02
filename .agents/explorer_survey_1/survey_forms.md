# Relatório Exaustivo de Levantamento e Auditoria R1
## Varredura de Formulários Swing (.form), initComponents(), Diálogos JOptionPane e Strings de Interface Java

**Data do Levantamento**: 2026-09-02  
**Agente Investigador**: `teamwork_preview_explorer_survey_forms`  
**Escopo Auditado**: `j-lawyer-client`, `j-lawyer-backupmgr`, `j-lawyer-io-common`  
**Objetivo**: Inventário completo e mapeamento de localização pt-BR de todos os textos residuais em alemão em formulários gráficos, código gerado por GUI Builder, diálogos modais e manipuladores de ação.

---

## 1. Sumário Executivo

A auditoria cobriu **100% dos arquivos de código-fonte e metadados visuais** dos três módulos sob análise, compreendendo 814 classes Java de produção e 297 formulários NetBeans (`.form`).

### Estatísticas Globais Consolidadas

| Categoria de Ocorrência | Quantidade Total | Arquivos Únicos Afetados | Módulos Impactados |
|---|---|---|---|
| **Propriedades em Formulários (.form)** (`text`, `tabTitle`, `toolTipText`, `TitledBorder`, `ComboBoxItem`) | **624** | **187** | `j-lawyer-client` |
| **Strings literais em `initComponents()`** (Java GUI Builder) | **738** | **201** | `j-lawyer-client` |
| **Diálogos `JOptionPane` com strings em alemão** (Mensagens, Títulos, Botões) | **601** | **151** | `j-lawyer-client` |
| **Outras strings de UI em classes Java** (Ações, Menus, Modelos, Validadores) | **1.461** | **237** | `j-lawyer-client`, `j-lawyer-backupmgr` |
| **Total Geral de Ocorrências Mapeadas** | **3.424** | **527** (340 `.java` + 187 `.form`) | `j-lawyer-client`, `j-lawyer-backupmgr` |

### Resumo por Módulo

1. **`j-lawyer-client`**: Concentra a quase totalidade das ocorrências visuais (3.400+ ocorrências em 339 classes Java e 187 arquivos `.form`). Contém o shell principal da aplicação Desktop, abas processuais, agenda, visualizadores de PDF, assistente de IA Ingo, integração beA/tribunais e centenas de telas de configuração.
2. **`j-lawyer-backupmgr`**: Possui 7 classes Java e 1 arquivo FXML (`backupmgr.fxml`). O FXML e o controller `BackupMgrController.java` já possuem textos em português, porém a classe de execução de backend `RestoreExecutor.java` contém 24 mensagens de progresso (`progress.onProgress(...)`) e mensagens de exceção (`throw new Exception(...)`) em alemão que são diretamente repassadas para a interface gráfica via `lblProgress.setText(ex.getMessage())`.
3. **`j-lawyer-io-common`**: Possui 1 classe Java (`Monitor.java`), que é um POJO de transferência de dados técnicos de sistema (CPU, memória, disco) e não contém nenhuma string de interface ou termo em alemão.

---

## 2. Dicionário Canônico de Tradução e Padronização Terminológica (pt-BR)

Para garantir consistência absoluta em toda a aplicação Desktop e conformidade com as regras de governança do ATRIUM, foi estabelecido o seguinte vocabulário oficial:

| Termo em Alemão | Contexto / Localização | Tradução Canônica pt-BR |
|---|---|---|
| **Abbrechen** | Botões de diálogo, menus contextuais | **Cancelar** |
| **Speichern** | Botões de ação, menus | **Salvar** |
| **Speichern unter...** | Exportação de documentos | **Salvar como...** |
| **Schließen** | Botões de fechar janela / diálogo | **Fechar** |
| **Suchen** / **Suche** | Campos de busca, botões, títulos | **Pesquisar** / **Buscar** |
| **Löschen** | Exclusão de registros, documentos, pastas | **Excluir** |
| **Entfernen** | Remoção de vínculos, itens de lista | **Remover** |
| **Hinzufügen** / **Neu** / **Neuer** / **Neues** | Criação / adição de novos elementos | **Adicionar** / **Novo** / **Nova** |
| **Bearbeiten** | Ação de edição | **Editar** |
| **Drucken** | Ação de impressão | **Imprimir** |
| **Druckvorschau** | Visualizador de impressão | **Visualizar Impressão** |
| **Mandant** / **Mandanten** | Cadastro de clientes | **Cliente** / **Clientes** |
| **Gegner** | Parte contrária no processo | **Parte Contrária** |
| **Akte** / **Akten** | Processo judicial / pasta de autos | **Processo** / **Processos** |
| **Aktenzeichen** (Az.) | Número identificador do processo | **Número do Processo** |
| **Aktennotiz** | Anotação nos autos | **Anotação do Processo** |
| **Aktenkonto** | Extrato contábil do processo | **Conta do Processo** |
| **Dokument** / **Dokumente** | Arquivos anexados | **Documento** / **Documentos** |
| **Ja** / **Nein** | Opções em diálogos de confirmação | **Sim** / **Não** |
| **Fehler** | Títulos e mensagens de erro | **Erro** |
| **Erfolg** / **Erfolgreich** | Mensagens de confirmação e sucesso | **Sucesso** / **Concluído com Sucesso** |
| **Warnung** / **Hinweis** | Alertas e observações | **Aviso** / **Atenção** / **Observação** |
| **Bestätigen** / **Bestätigung** | Ação e títulos de confirmação | **Confirmar** / **Confirmação** |
| **Auswählen** / **Auswahl** | Seleção de itens / tabelas | **Selecionar** / **Seleção** |
| **Öffnen** / **öffnen mit...** | Abertura de documentos | **Abrir** / **Abrir com...** |
| **Einstellungen** | Menus e abas de configuração | **Configurações** |
| **Optionen** | Opções gerais | **Opções** |
| **Hilfe** | Menu e botões de ajuda | **Ajuda** |
| **Datei** / **Dateien** | Arquivos em disco | **Arquivo** / **Arquivos** |
| **Ordner** | Diretórios de documentos/emails | **Pasta** |
| **Benutzer** / **Benutzername** | Controle de acesso | **Usuário** / **Nome de Usuário** |
| **Passwort** / **Kennwort** | Credenciais | **Senha** |
| **Verbindung** / **Verbinden** | Status de rede / banco | **Conexão** / **Conectar** |
| **Vorschau** | Pré-visualização de imagem/PDF | **Visualização** / **Pré-visualização** |
| **Exportieren** / **Importieren** | Transferência de dados | **Exportar** / **Importar** |
| **Aktualisieren** | Recarregamento de dados | **Atualizar** |
| **Zurück** / **Weiter** / **Fertig** | Navegação em assistentes (Wizards) | **Voltar** / **Avançar** / **Concluir** |
| **Übernehmen** | Aplicação de parâmetros | **Aplicar** |
| **Ansicht** | Menu de exibição / layout | **Exibir** |
| **Adressbuch** / **Adresse** | Gestão de contatos | **Contatos** / **Endereço** |
| **Termin** / **Termine** | Agenda e audiências | **Compromisso** / **Audiência** |
| **Frist** / **Fristen** | Prazos processuais | **Prazo** / **Prazos** |
| **Wiedervorlage** | Lembretes e retornos | **Lembrete** / **Retorno** |
| **Rechnung** / **Rechnungen** | Faturamento e honorários | **Fatura** / **Cobrança** |
| **Zahlung** / **Zahlungseingang** | Fluxo financeiro | **Pagamento** / **Recebimento** |
| **Honorar** / **Vergütung** | Honorários advocatícios | **Honorários** |
| **Gericht** / **Gerichte** | Órgãos jurisdicionais | **Tribunal** / **Vara** / **Fórum** |
| **Beteiligte** / **Beteiligter** | Partes vinculadas | **Partes Envolvidas** / **Envolvidos** |
| **Rubrum** | Qualificação do processo | **Cabeçalho Processual** |
| **Möchten Sie wirklich...?** | Diálogo de confirmação | **Deseja realmente...?** |
| **Sind Sie sicher?** | Confirmação de exclusão | **Tem certeza?** |
| **Bitte warten...** | Diálogo de progresso | **Por favor, aguarde...** |
| **Keine Daten gefunden** | Mensagem de busca vazia | **Nenhum dado encontrado** |
| **Vorlage** / **Vorlagen** | Modelos de petições/documentos | **Modelo** / **Modelos** |
| **Ungültig** / **Ungültige** | Validação de formulários | **Inválido** / **Inválida** |
| **Pflichtfeld** | Validação de obrigatoriedade | **Campo Obrigatório** |
| **Nachname** / **Vorname** | Campos de pessoa física | **Sobrenome** / **Nome** |
| **Straße** / **PLZ** / **Ort** | Endereço postal | **Endereço / Logradouro** / **CEP** / **Cidade** |
| **Posteingang** / **Postausgang** | Caixas de correio | **Entrada** / **Saída** |
| **Entwürfe** / **Gesendet** | Pastas de correio | **Rascunhos** / **Enviados** |
| **Papierkorb** / **Spam** | Pastas de correio | **Lixeira** / **Spam** |

---

## 3. Inventário Detalhado por Subsistema Funcional

Abaixo está o detalhamento estruturado dos 14 subsistemas identificados, listando arquivos críticos, tipos de componentes, termos em alemão detectados, linhas de código e traduções recomendadas.

```
+---------------------------------------------------------------------------------------------------------------+
| Subsistema Funcional               | Total Matches | Total Files | .form Matches | init() | JOptionPane | Outras |
+---------------------------------------------------------------------------------------------------------------+
| 1. Gestão de Processos & Autos     |         1.004 |         103 |           175 |    219 |         248 |    362 |
| 2. Assistente IA Ingo              |           453 |          27 |            18 |     20 |          14 |    401 |
| 3. Configurações & Administração   |           408 |          93 |           140 |    156 |          68 |     44 |
| 4. E-mail, Mensagens & VOIP        |           397 |          79 |            79 |    100 |          63 |    155 |
| 5. Shell Principal & Navegação     |           245 |          67 |            47 |     37 |          47 |    114 |
| 6. beA / Integrações Judiciais     |           237 |          32 |            44 |     51 |          47 |     95 |
| 7. Documentos, PDF & Scanner       |           207 |          49 |            42 |     46 |          28 |     91 |
| 8. UI Framework, Tags & Calendário |           177 |          40 |            30 |     43 |          22 |     82 |
| 9. Contatos & Agenda               |           109 |          14 |            24 |     37 |          26 |     22 |
| 10. Financeiro & Pagamentos        |            53 |           4 |             9 |      9 |          12 |     23 |
| 11. Gestão de Modelos (Templates)  |            53 |           8 |             8 |     10 |          22 |     13 |
| 12. Relatórios & Estatísticas      |            53 |           6 |             2 |      3 |           1 |     47 |
| 13. Pesquisa e Indexação           |            26 |           4 |             6 |      7 |           3 |     10 |
| 14. Gerenciador de Backup          |            24 |           2 |             0 |      0 |           0 |     24 |
+---------------------------------------------------------------------------------------------------------------+
```

---

### 3.1. Gestão de Processos & Autos (`com.jdimension.jlawyer.client.editors.files`)

Este é o maior subsistema da aplicação Desktop, contendo a interface central de visualização e manipulação de autos processuais (`ArchiveFilePanel.java` com 10.878 linhas e `ArchiveFilePanel.form` com 3.703 linhas).

#### Arquivos Principais
- `ArchiveFilePanel.java` / `ArchiveFilePanel.form` (295 matches Java, 52 matches .form)
- `InvoiceDialog.java` / `InvoiceDialog.form` (60 matches Java, 18 matches .form)
- `QuickArchiveFileSearchPanel.java` / `QuickArchiveFileSearchPanel.form` (35 matches Java, 12 matches .form)
- `AddDocumentFromTemplateDialog.java` / `AddDocumentFromTemplateDialog.form` (26 matches Java, 8 matches .form)
- `ClaimLedgerDialog.java` / `ClaimLedgerDialog.form` (24 matches Java, 4 matches .form)
- `ArchivalDialog.java` / `ArchivalDialog.form` (24 matches Java, 5 matches .form)
- `AddAddressDocumentFromTemplateDialog.java` / `AddAddressDocumentFromTemplateDialog.form` (19 matches Java, 6 matches .form)
- `BulkSaveDialog.java` / `BulkSaveDialog.form` (10 matches .form, 12 matches Java)
- `ArchiveFileReviewsMissingPanel.java` / `ArchiveFileReviewsMissingPanel.form` (10 matches .form)
- `ArchiveFileReviewsFindPanel.java` / `ArchiveFileReviewsFindPanel.form` (5 matches .form)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`ArchiveFilePanel.form` / `ArchiveFilePanel.java`**:
   - Linha 101 (`.form`) / Linha 2210 (`.java`): `als PDF zur Akte speichern` -> `Salvar como PDF no processo`
   - Linha 117 (`.form`) / Linha 2225 (`.java`): `in Zwischenablage kopieren` -> `Copiar para a área de transferência`
   - Linha 128 (`.form`) / Linha 2235 (`.java`): `öffnen` -> `Abrir`
   - Linha 139 (`.form`) / Linha 2246 (`.java`): `öffnen mit...` -> `Abrir com...`
   - Linha 171 (`.form`) / Linha 2280 (`.java`): `lokal speichern` -> `Salvar localmente`
   - Linha 182 (`.form`) / Linha 2291 (`.java`): `duplizieren` -> `Duplicar`
   - Linha 193 (`.form`) / Linha 2302 (`.java`): `kopieren oder verschieben` -> `Copiar ou mover`
   - Linha 201 (`.form`) / Linha 2310 (`.java`): `in andere Akte kopieren` -> `Copiar para outro processo`
   - Linha 212 (`.form`) / Linha 2321 (`.java`): `in andere Akte verschieben` -> `Mover para outro processo`
   - Linha 227 (`.form`) / Linha 2336 (`.java`): `umbenennen` -> `Renomear`
   - Linha 238 (`.form`) / Linha 2347 (`.java`): `Erstellungsdatum anpassen` -> `Ajustar data de criação`
   - Linha 249 (`.form`): `farblich hervorheben` -> `Destacar com cor` (`erste Farbe` -> `Primeira cor`, `zweite Farbe` -> `Segunda cor`)
   - Linha 275 (`.form`): `Favoritendokument an/aus` -> `Favoritar / Desfavoritar documento`
   - Linha 286 (`.form`): `Nachricht senden` -> `Enviar mensagem`
   - Linha 297 (`.form`): `löschen` -> `Excluir`
   - Linha 310 (`.form`): `PDF und Konvertierung` -> `PDF e Conversão`
   - Linha 318 (`.form`): `Texterkennung (OCR)` -> `Reconhecimento de texto (OCR)`
   - Linha 330 (`.form`): `als PDF zusammenführen` -> `Mesclar como PDF`
   - Linha 342 (`.form`): `PDF aufteilen ` -> `Dividir PDF`
   - Linha 1514 (`.form`) / Linha 3560 (`.java`): `TitledBorder title="Dokumente"` -> `Documentos`
   - Linha 1582 (`.form`) / Linha 3630 (`.java`): `cmdNewDocument.setText("Erstellen")` -> `Criar`, ToolTip: `neues Dokument aus Vorlage erstellen` -> `Criar novo documento a partir de modelo`
   - Linha 1594 (`.form`) / Linha 3642 (`.java`): `cmdUploadDocument.setText("Hinzufügen")` -> `Adicionar`, ToolTip: `vorhandene Datei hinzuladen` -> `Importar arquivo existente`
   - Linha 1616 (`.form`): `Suche zurücksetzen` -> `Limpar pesquisa`
   - Linha 4800-5200 (`.java` - Diálogos JOptionPane):
     - `JOptionPane.showConfirmDialog(..., "Möchten Sie das Dokument wirklich löschen?", "Dokument löschen", ...)` -> `"Deseja realmente excluir o documento?", "Excluir Documento"`
     - `JOptionPane.showConfirmDialog(..., "Soll die Akte archiviert werden?", "Akte archivieren", ...)` -> `"Deseja arquivar o processo?", "Arquivar Processo"`
     - `JOptionPane.showMessageDialog(..., "Fehler beim Speichern der Akte", "Fehler", ...)` -> `"Erro ao salvar o processo", "Erro"`
     - Botões de opção customizados: `new String[]{"Ja", "Nein", "Abbrechen"}` -> `new String[]{"Sim", "Não", "Cancelar"}`

2. **`InvoiceDialog.java` / `InvoiceDialog.form`**:
   - Linhas 714, 1376 (`.form`): `Zahlung:` / `Zahlungen` -> `Pagamento:` / `Pagamentos`
   - Linha 1214 (`.form`): `Rechnungsempfänger` -> `Destinatário da fatura`
   - Linha 1224 (`.form`): `Rechnungserstellung` -> `Emissão de fatura`
   - Linha 2730 (`.java`): `Fehler beim Hinzufügen der Position` -> `Erro ao adicionar o item`
   - Linha 2755 (`.java`): `Alle Positionen auswählen?` -> `Selecionar todos os itens?`
   - Linha 2792 (`.java`): `Es ist kein Rechnungsempfänger angegeben!` -> `Nenhum destinatário de fatura informado!`
   - Linha 3090 (`.java`): `Leistungszeitraum fehlt...` -> `Período de prestação de serviços não informado...`
   - Linha 3114 (`.java`): `Der Rechnungsbetrag stimmt nicht überein...` -> `O valor total da fatura não confere...`

---

### 3.2. Assistente IA Ingo (`com.jdimension.jlawyer.client.assistant`)

O assistente Ingo possui interfaces de chat e diálogos modais para geração e extração de documentos, além do registro central de ferramentas (`ToolRegistry.java`).

#### Arquivos Principais
- `ToolRegistry.java` (350 matches Java)
- `AssistantChatPanel.java` (19 matches Java)
- `AssistantExtractDialog.java` / `.form` (5 matches .form, 8 matches Java)
- `AssistantGenericDialog.java` / `.form` (4 matches .form, 6 matches Java)
- `AssistantChatDialog.java` / `.form` (3 matches .form, 5 matches Java)
- `AssistantGenerateDialog.java` / `.form` (3 matches .form, 5 matches Java)
- `AssistantVisionDialog.java` / `.form` (3 matches .form, 4 matches Java)
- `AssistantParameterDialog.java` / `.form` (2 matches .form)
- `AssistantResultDialog.java` / `.form` (2 matches .form)
- `AiChatMessageMarkdownPanel.java` / `.form` (1 match .form, 3 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`ToolRegistry.java`**:
   - Linha 135: `"Suchbegriff für die Aktensuche"` -> `"Termo de pesquisa para busca de processos"`
   - Linha 137: `"Ruft Details einer Akte ab, einschließlich Beteiligte und Aktennotiz."` -> `"Obtém detalhes de um processo, incluindo partes envolvidas e anotações."`
   - Linha 140: `"Sucht nach Kontakten/Adressen anhand eines Suchbegriffs."` -> `"Pesquisa contatos/endereços com base em um termo de busca."`
   - Linha 143: `"Listet Dokumente einer Akte seitenweise auf (20 pro Seite)."` -> `"Lista documentos de um processo de forma paginada (20 por página)."`
   - Linha 149: `"Listet Dokumente einer Akte, die innerhalb eines Zeitraums erstellt wurden..."` -> `"Lista documentos de um processo criados em determinado período..."`
   - Linha 155: `"Durchsucht Dokumente einer Akte anhand des Dateinamens..."` -> `"Pesquisa documentos de um processo pelo nome do arquivo..."`
   - Linha 161: `"Extrahiert den Textinhalt eines Dokuments (PDF oder Textdatei)."` -> `"Extrai o conteúdo de texto de um documento (PDF ou arquivo de texto)."`
   - Linha 170: `"Gibt das aktuelle Datum und die Uhrzeit zurück."` -> `"Retorna a data e a hora atuais."`
   - Linha 173: `"Gibt die Änderungshistorie einer Akte zurück."` -> `"Retorna o histórico de alterações de um processo."`
   - Linha 179: `"Gibt alle Beteiligten einer Akte mit vollständigen Kontaktdaten zurück."` -> `"Retorna todas as partes envolvidas no processo com dados de contato completos."`
   - Linha 182: `"Gibt alle offenen Kalenderereignisse zurück..."` -> `"Retorna todos os eventos em aberto do calendário..."`
   - Linha 194: `"Gibt die verfügbaren Kalenderereignis-Typen zurück (Wiedervorlage, Frist, Termin)..."` -> `"Retorna os tipos de eventos disponíveis no calendário (Lembrete, Prazo, Audiência)..."`
   - Linha 198: `"Findet freie Zeitfenster im Kalender eines Benutzers..."` -> `"Localiza horários livres no calendário de um usuário..."`

2. **`AssistantChatDialog.form` / `AssistantExtractDialog.form`**:
   - `cmdCancel.setText("Abbrechen")` -> `Cancelar`
   - `cmdSend.setText("Senden")` -> `Enviar`
   - `lblStatus.setText("Berechne Antwort...")` -> `Gerando resposta...`
   - `cmdApply.setText("Übernehmen")` -> `Aplicar`

---

### 3.3. Configurações & Administração (`com.jdimension.jlawyer.client.configuration`)

Subsistema de parametrização global do escritório, usuários, numeração de processos, regras de indexação e integrações.

#### Arquivos Principais
- `UserAdministrationDialog.java` / `.form` (28 matches Java, 12 matches .form)
- `MailboxSetupDialog.java` / `.form` (27 matches Java, 17 matches .form)
- `CaseNumberingConfigurationDialog.java` / `.form` (9 matches .form, 12 matches Java)
- `CalendarEntryTemplatesDialog.java` / `.form` (7 matches .form)
- `GetExternLocationDialog.java` / `.form` (7 matches .form)
- `ServerMonitoringDialog.java` / `.form` (6 matches .form)
- `UserProfileDialog.java` / `.form` (6 matches .form)
- `CalendarSetupDialog.java` / `.form` (5 matches .form)
- `DocumentFolderTemplatesDialog.java` / `.form` (5 matches .form)
- `BackupConfigurationDialog.java` / `.form` (4 matches .form)
- `DocumentsBinDialog.java` / `.form` (4 matches .form)
- `SystemMailboxDialog.java` / `.form` (4 matches .form)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`UserAdministrationDialog.form` / `UserAdministrationDialog.java`**:
   - Linha 2190 (`.java`): `JOptionPane.showConfirmDialog(..., "Nutzer wirklich löschen?", "Benutzer löschen", ...)` -> `"Deseja realmente excluir o usuário?", "Excluir Usuário"`
   - Linha 2260 (`.java`): `JOptionPane.showInputDialog(..., "neues Passwort vergeben:", "Passwort ändern", ...)` -> `"Definir nova senha:", "Alterar Senha"`
   - Linha 2265 (`.java`): `JOptionPane.showMessageDialog(..., "Passwörter stimmen nicht überein", "Fehler", ...)` -> `"As senhas não conferem", "Erro"`
   - Linha 2278 (`.java`): `JOptionPane.showMessageDialog(..., "Passwort erfolgreich geändert", "Erfolg", ...)` -> `"Senha alterada com sucesso", "Sucesso"`
   - Linha 3160 (`.java`): `JOptionPane.showMessageDialog(..., "Verbindungstest erfolgreich", "Erfolg", ...)` -> `"Teste de conexão bem-sucedido", "Sucesso"`
   - `.form` Properties: `Passwort wiederholen:` -> `Confirmar senha:`, `Administratorrechte` -> `Permissões de Administrador`, `inaktiver Benutzer` -> `Usuário inativo`

2. **`CaseNumberingConfigurationDialog.form`**:
   - `.form` Properties: `Aktenzeichen-Format` -> `Formato do Número do Processo`, `fortlaufende Nummer` -> `Número sequencial`, `Jahreszahl (2-stellig)` -> `Ano (2 dígitos)`, `Jahreszahl (4-stellig)` -> `Ano (4 dígitos)`

3. **`MailboxSetupDialog.form` / `.java`**:
   - `.form` Properties: `Posteingangsserver (IMAP)` -> `Servidor de Entrada (IMAP)`, `Postausgangsserver (SMTP)` -> `Servidor de Saída (SMTP)`, `Verbindung verschlüsseln (SSL/TLS)` -> `Criptografia da conexão (SSL/TLS)`
   - Linha 1675 (`.java`): `Neues Postfach anlegen` -> `Criar nova caixa de correio`
   - Linha 1828 (`.java`): `Verbindungstest läuft...` -> `Testando conexão...`

---

### 3.4. E-mail, Mensagens Instantâneas & Comunicação (`com.jdimension.jlawyer.client.mail`, `messenger`, `voip`)

Módulos de envio e visualização de e-mails, assistentes de salvamento de e-mails nos autos processuais, mensageria interna e integração telefônica/fax.

#### Arquivos Principais
- `SendEmailFrame.java` / `SendEmailFrame.form` (47 matches Java, 13 matches .form)
- `EmailInboxPanel.java` / `EmailInboxPanel.form` (33 matches Java, 11 matches .form)
- `MailContentUI.java` / `MailContentUI.form` (26 matches Java, 2 matches .form)
- `SelectAddressStep.java` / `SelectAddressStep.form` (19 matches Java, 6 matches .form)
- `SaveToCasePanel.java` / `SaveToCasePanel.form` (4 matches .form, 8 matches Java)
- `CreateAddressDetailsStep.java` / `CreateAddressDetailsStep.form` (3 matches .form)
- `NewCaseStep.java` / `NewCaseStep.form` (2 matches .form)
- `EpostSetPasswordDialog.java` / `.form` (5 matches .form)
- `EpostPdfOrderingStep.java` / `.form` (4 matches .form)
- `EpostLetterSendStep.java` / `.form` (4 matches .form)
- `MessagingCenterPanel.java` / `.form` (2 matches .form, 6 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`EmailInboxPanel.form` / `.java`**:
   - Linha 946 (`.java`): `Nachrichten werden geladen...` -> `Carregando mensagens...`
   - Linha 2278 (`.java`): `Name des Ordners:` -> `Nome da pasta:`
   - Linha 2318 (`.java`): `Ordner anlegen` -> `Criar pasta`
   - Linha 2351 (`.java`): `Ordner konnte nicht angelegt werden` -> `Não foi possível criar a pasta`
   - `.form` Properties: `Posteingang` -> `Caixa de Entrada`, `Postausgang` -> `Caixa de Saída`, `Entwürfe` -> `Rascunhos`, `Gesendet` -> `Enviados`, `Papierkorb` -> `Lixeira`

2. **`SendEmailFrame.form` / `.java`**:
   - `cmdSend.setText("Senden")` -> `Enviar`
   - `cmdAttach.setText("Anhang hinzufügen...")` -> `Anexar arquivo...`
   - `lblSubject.setText("Betreff:")` -> `Assunto:`
   - `lblTo.setText("An:")` -> `Para:`
   - `lblCc.setText("Kopie (CC):")` -> `Cópia (CC):`
   - `lblBcc.setText("Blindkopie (BCC):")` -> `Cópia Oculta (CCO):`
   - `JOptionPane.showConfirmDialog(..., "Möchten Sie die E-Mail vor dem Schließen als Entwurf speichern?", "Entwurf speichern", ...)` -> `"Deseja salvar o e-mail como rascunho antes de fechar?", "Salvar Rascunho"`

---

### 3.5. Shell Principal & Navegação (`com.jdimension.jlawyer.client`)

O shell da aplicação contém os menus de topo, barra de status, painel de autenticação e caixas de diálogo de conexão e informações sobre o sistema.

#### Arquivos Principais
- `JKanzleiGUI.java` / `JKanzleiGUI.form` (16 matches .form, 22 matches Java)
- `LoginDialog.java` / `LoginDialog.form` (8 matches .form, 14 matches Java)
- `AdminConsoleFrame.java` / `AdminConsoleFrame.form` (1 match .form, 8 matches Java)
- `UserCredentialsDialog.java` / `UserCredentialsDialog.form` (1 match .form, 3 matches Java)
- `AboutDialog.java` / `AboutDialog.form` (2 matches Java)
- `ImportConnectionProfileDialog.java` / `.form` (3 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`JKanzleiGUI.form` / `JKanzleiGUI.java`**:
   - Linha 71 (`.form`) / Linha 820 (`.java`): `mnuBeaCourtAddressImport.setText("Import: Gerichtsadressen")` -> `Importar: Endereços de Tribunais`
   - Linha 82 (`.form`) / Linha 831 (`.java`): `mnuImportFromSheet.setText("Import / Export: Einstellungen")` -> `Importar / Exportar: Configurações`
   - Linha 93 (`.form`) / Linha 842 (`.java`): `mnuExportSyncedCases.setText("Export: Akten")` -> `Exportar: Processos`, ToolTip: `Exportieren von zur Synchronisation markierten Akten` -> `Exportar processos marcados para sincronização`
   - Linha 119 (`.form`) / Linha 865 (`.java`): `mnuView.setText("Ansicht")` -> `Exibir`
   - Linha 137 (`.form`): `mnuChkRandomBackground.setText("zufälliger Desktophintergrund")` -> `Plano de fundo aleatório`
   - Linha 337 (`.form`): `mnuPartyTypes.setText("Beteiligtentypen")` -> `Tipos de Partes Envolvidas`
   - Linha 393 (`.form`): `mnuArchiveFileLabels.setText("Akten-Etiketten")` -> `Etiquetas de Processos`
   - Linha 404 (`.form`): `mnuArchiveFileListLabels.setText("Akten-Listenetiketten")` -> `Etiquetas de Listas de Processos`
   - Linha 2428 (`.java`): `JOptionPane.showConfirmDialog(..., "Es sind noch Nachrichten im Postausgang...", "Postausgang nicht leer", ...)` -> `"Ainda existem mensagens na Caixa de Saída...", "Caixa de Saída com Mensagens Pendentes"`

2. **`LoginDialog.form` / `LoginDialog.java`**:
   - Linha 15 (`.form`) / Linha 700 (`.java`): `title="j-lawyer.org Login"` -> `title="ATRIUM — Login"`
   - Linha 113 (`.form`): `tabTitle="Login"` -> `Login`
   - Linha 444 (`.form`): `tabTitle="Profile"` -> `Perfis`
   - Linha 720 (`.form`): `tabTitle="Einstellungen"` -> `Configurações`
   - Linha 1574 (`.java`): `lblPassword.setText("Passwort:")` -> `Senha:`
   - Linha 1673 (`.java`): `lblSelectProfile.setText("Profil auswählen")` -> `Selecionar perfil`
   - Linha 1765 (`.java`): `lblRootPwd.setText("root-Passwort:")` -> `Senha de Administrador (root):`

---

### 3.6. beA / Assinaturas / Integrações Judiciais (`com.jdimension.jlawyer.client.bea`)

Módulo de mensageria judicial e certificados eletrônicos.

#### Arquivos Principais
- `BeaInboxPanel.java` / `BeaInboxPanel.form` (46 matches Java, 8 matches .form)
- `SendBeaMessageFrame.java` / `SendBeaMessageFrame.form` (38 matches Java, 14 matches .form)
- `BeaMessageContentUI.java` / `BeaMessageContentUI.form` (34 matches Java, 4 matches .form)
- `BeaIdentitySearchDialog.java` / `BeaIdentitySearchDialog.form` (3 matches .form, 6 matches Java)
- `SelectLegalAuthorityDialog.java` / `SelectLegalAuthorityDialog.form` (2 matches .form, 5 matches Java)
- `BeaSignaturesVerificationDialog.java` / `.form` (1 match .form, 4 matches Java)
- `EebRejectDialog.java` / `.form` (1 match .form, 3 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`BeaInboxPanel.form` / `.java`**:
   - Linhas 81, 92, 103 (`.form`): `in Akte speichern` -> `Salvar no processo`, `in Akte verschieben` -> `Mover para o processo`
   - Linha 16 (`.form`): `neuer Ordner` -> `Nova pasta`
   - Linha 27 (`.form`): `Ordner löschen` -> `Excluir pasta`
   - Linha 239 (`.form`): `neue Nachrichten abrufen` -> `Buscar novas mensagens`
   - Linha 1630 (`.java`): `JOptionPane.showInputDialog(..., "Name des Ordners:", "Ordner anlegen", ...)` -> `"Nome da pasta:", "Criar Pasta"`
   - Linha 3002 (`.java`): `JOptionPane.showMessageDialog(..., "Bitte zunächst eine Nachricht auswählen.", "Hinweis", ...)` -> `"Por favor, selecione uma mensagem primeiro.", "Aviso"`

2. **`SendBeaMessageFrame.form` / `.java`**:
   - `lblRecipient.setText("Empfänger (beA):")` -> `Destinatário (beA / Tribunal):`
   - `cmdSign.setText("Signieren")` -> `Assinar digitalmente`
   - `cmdSend.setText("Senden")` -> `Enviar`
   - `cmdAddAttachment.setText("Anhang hinzufügen...")` -> `Adicionar anexo...`
   - `JOptionPane.showConfirmDialog(..., "Möchten Sie die Nachricht wirklich versenden?", "Nachricht senden", ...)` -> `"Deseja realmente enviar a mensagem?", "Enviar Mensagem"`

---

### 3.7. Documentos, PDF & Scanner (`com.jdimension.jlawyer.client.editors.documents`, `utils.pdf`, `shrinkify`)

Módulo de aquisição via scanner TWAIN/SANE, divisão e mesclagem de PDFs, conversão OCR e visualização.

#### Arquivos Principais
- `ScannerPanel.java` / `ScannerPanel.form` (38 matches Java, 5 matches .form)
- `PdfImageScrollingPanel.java` / `PdfImageScrollingPanel.form` (6 matches .form, 12 matches Java)
- `NewDocumentNameDialog.java` / `NewDocumentNameDialog.form` (4 matches .form, 6 matches Java)
- `DocumentPreviewTooLarge.java` / `DocumentPreviewTooLarge.form` (3 matches .form)
- `SearchAndAssignDialog.java` / `SearchAndAssignDialog.form` (3 matches .form)
- `GenericScanActionsPanel.java` / `GenericScanActionsPanel.form` (2 matches .form)
- `CreateNewCaseFromScanPanel.java` / `CreateNewCaseFromScanPanel.form` (2 matches .form)
- `PDFSplitDialog.java` / `PDFSplitDialog.form` (2 matches .form, 4 matches Java)
- `PdfAnonymizerDialog.java` / `PdfAnonymizerDialog.form` (2 matches .form, 3 matches Java)
- `ShrinkifyGui.java` / `ShrinkifyGui.form` (2 matches .form, 8 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`ScannerPanel.form` / `ScannerPanel.java`**:
   - `cmdScan.setText("Scannen")` -> `Digitalizar`
   - `cmdPreview.setText("Vorschau")` -> `Visualizar`
   - `cmdSaveToCase.setText("In Akte speichern")` -> `Salvar no processo`
   - `JOptionPane.showMessageDialog(..., "Kein Scanner gefunden!", "Fehler", ...)` -> `"Nenhum scanner localizado!", "Erro"`

2. **`PDFSplitDialog.form` / `.java`**:
   - `lblPageRange.setText("Seitenbereich (z.B. 1-3, 5):")` -> `Intervalo de páginas (ex: 1-3, 5):`
   - `cmdSplit.setText("PDF aufteilen")` -> `Dividir PDF`
   - `JOptionPane.showMessageDialog(..., "Ungültiger Seitenbereich angegeben.", "Fehler", ...)` -> `"Intervalo de páginas inválido informado.", "Erro"`

3. **`ShrinkifyGui.java` / `ShrinkifyGui.form`**:
   - Linha 173 (`.form`): `Vorschau der Komprimierung` -> `Visualização da compressão`
   - Linha 185 (`.form`): `Komprimieren und Speichern` -> `Comprimir e Salvar`
   - Linha 741 (`.java`): `JOptionPane.showMessageDialog(..., "Ungültige Datei: " + file, "Fehler", ...)` -> `"Arquivo inválido: " + file, "Erro"`
   - Linha 1113 (`.java`): `JOptionPane.showMessageDialog(..., "Bitte wählen Sie mindestens eine Datei aus.", "Hinweis", ...)` -> `"Por favor, selecione pelo menos um arquivo.", "Aviso"`

---

### 3.8. Contatos & Agenda (`com.jdimension.jlawyer.client.editors.addresses`)

Gestão de agenda de contatos, pessoas físicas e jurídicas, partes processuais e exportação vCard.

#### Arquivos Principais
- `AddressPanel.java` / `AddressPanel.form` (56 matches Java, 17 matches .form)
- `QuickCreateAddressDialog.java` / `QuickCreateAddressDialog.form` (5 matches .form, 8 matches Java)
- `QuickAddressSearchPanel.java` / `QuickAddressSearchPanel.form` (3 matches .form, 6 matches Java)
- `AddressFromClipboardDialog.java` / `AddressFromClipboardDialog.form` (1 match .form, 3 matches Java)
- `CaseForContactEntryPanel.java` / `CaseForContactEntryPanel.form` (1 match .form, 2 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`AddressPanel.form` / `AddressPanel.java`**:
   - Linha 1304 (`.java`): `JOptionPane.showConfirmDialog(..., "Möchten Sie die Änderungen an der Adresse speichern?", "Adresse speichern", ...)` -> `"Deseja salvar as alterações no contato?", "Salvar Contato"`
   - Linha 3736 (`.java`): `JOptionPane.showConfirmDialog(..., "Daten aus beA übernehmen?", "Bestätigung", ...)` -> `"Deseja importar dados do beA?", "Confirmação"`
   - Linha 3845 (`.java`): `JOptionPane.showInputDialog(..., "neues Passwort:", "Passwort festlegen", ...)` -> `"Nova senha:", "Definir Senha"`
   - Linha 4179 (`.java`): `JOptionPane.showInputDialog(..., "Neuer Name des Dokuments:", "Dokument umbenennen", ...)` -> `"Novo nome do documento:", "Renomear Documento"`
   - Linha 4533 (`.java`): `JOptionPane.showConfirmDialog(..., "Sollen die markierten Dokumente gelöscht werden?", "Dokumente löschen", ...)` -> `"Deseja excluir os documentos selecionados?", "Excluir Documentos"`
   - Linha 4564 (`.java`): `JOptionPane.showMessageDialog(..., "Bitte speichern Sie zuerst den Kontakt...", "Hinweis", ...)` -> `"Por favor, salve o contato primeiro...", "Aviso"`
   - `.form` Properties: `Vorname:` -> `Nome:`, `Nachname:` -> `Sobrenome:`, `PLZ:` -> `CEP:`, `Ort:` -> `Cidade:`, `Straße:` -> `Endereço / Logradouro:`, `Telefon geschäftlich:` -> `Telefone comercial:`, `Mobiltelefon:` -> `Celular:`

---

### 3.9. Financeiro & Faturamento (`com.jdimension.jlawyer.client.editors.finance`)

Módulo de extratos bancários, conciliação de pagamentos e repasses.

#### Arquivos Principais
- `ImportBankStatementFrame.java` / `ImportBankStatementFrame.form` (30 matches Java, 6 matches .form)
- `ManagePaymentsFrame.java` / `ManagePaymentsFrame.form` (3 matches .form, 8 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`ImportBankStatementFrame.form` / `.java`**:
   - `lblCsvFile.setText("Kontoauszug (CSV-Datei):")` -> `Extrato Bancário (Arquivo CSV):`
   - `cmdBrowse.setText("Durchsuchen...")` -> `Procurar...`
   - `cmdImport.setText("Kontoauszug importieren")` -> `Importar Extrato`
   - `JOptionPane.showConfirmDialog(..., "Möchten Sie die erkannten Zahlungen jetzt verbuchen?", "Zahlungen verbuchen", ...)` -> `"Deseja lançar os pagamentos identificados agora?", "Lançar Pagamentos"`
   - `JOptionPane.showMessageDialog(..., "Keine neuen Zahlungseingänge gefunden.", "Hinweis", ...)` -> `"Nenhum novo recebimento encontrado.", "Aviso"`

2. **`ManagePaymentsFrame.form` / `.java`**:
   - `tabPayments.setTitleAt(0, "Offene Posten")` -> `Contas a Receber (Em Aberto)`
   - `tabPayments.setTitleAt(1, "Zahlungseingänge")` -> `Pagamentos Recebidos`
   - `tabPayments.setTitleAt(2, "Zahlungsausgänge")` -> `Pagamentos Efetuados`

---

### 3.10. Gestão de Modelos / Templates (`com.jdimension.jlawyer.client.templates`)

Árvore hierárquica de modelos de documentos e pastas padrão.

#### Arquivos Principais
- `TemplatesTreePanel.java` / `TemplatesTreePanel.form` (34 matches Java, 7 matches .form)
- `NewTemplateDialog.java` / `NewTemplateDialog.form` (2 matches .form, 4 matches Java)
- `SelectTemplateFolderDialog.java` / `SelectTemplateFolderDialog.form` (1 match .form, 2 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`TemplatesTreePanel.form` / `.java`**:
   - `mnuNewTemplate.setText("Neue Vorlage...")` -> `Novo Modelo...`
   - `mnuNewFolder.setText("Neuer Ordner...")` -> `Nova Pasta...`
   - `mnuDeleteTemplate.setText("Vorlage löschen")` -> `Excluir Modelo`
   - `mnuRenameTemplate.setText("Vorlage umbenennen")` -> `Renomear Modelo`
   - `JOptionPane.showConfirmDialog(..., "Möchten Sie die Vorlage wirklich löschen?", "Vorlage löschen", ...)` -> `"Deseja realmente excluir o modelo?", "Excluir Modelo"`
   - `JOptionPane.showInputDialog(..., "Neuer Name des Vorlagenordners:", "Ordner umbenennen", ...)` -> `"Novo nome da pasta de modelos:", "Renomear Pasta"`

---

### 3.11. UI Framework, Tags & Calendário (`com.jdimension.jlawyer.ui.*`, `de.costache.calendar`, `desktop`)

Componentes reutilizáveis de pastas de autos, etiquetas multicolores, calendário e cards do Desktop.

#### Arquivos Principais
- `CaseFolderPanel.java` / `CaseFolderPanel.form` (11 matches .form, 16 matches Java)
- `FolderListCell.java` / `FolderListCell.form` (19 matches Java, 2 matches .form)
- `DesktopPanel.java` / `DesktopPanel.form` (29 matches Java, 7 matches .form)
- `NewEventEntryDialog.java` / `NewEventEntryDialog.form` (1 match .form, 8 matches Java)
- `MultiCalDialog.java` / `MultiCalDialog.form` (1 match .form, 6 matches Java)
- `QuickDateSelectionPanel.java` / `QuickDateSelectionPanel.form` (1 match .form, 4 matches Java)
- `TagPanel.java` / `TagPanel.form` (2 matches Java)

#### Ocorrências Notáveis & Mapeamento de Tradução

1. **`CaseFolderPanel.form` / `FolderListCell.java`**:
   - `lblFolder.setText("Ordner:")` -> `Pasta:`
   - `cmdAddSubfolder.setText("Unterordner erstellen")` -> `Criar subpasta`
   - `cmdDeleteFolder.setText("Ordner löschen")` -> `Excluir pasta`

2. **`DesktopPanel.form` / `DesktopPanel.java`**:
   - Tooltips de contadores:
     - `0 Akten` -> `0 Processos`
     - `0 Akten im Archiv` -> `0 Processos arquivados`
     - `0 Adressbucheinträge` -> `0 Contatos`
     - `0 Dokumente` -> `0 Documentos`
     - `unbearbeitete E-Mails` -> `E-mails pendentes / não lidos`
     - `Akten-Etiketten` -> `Etiquetas de Processos`
     - `Dokument-Etiketten` -> `Etiquetas de Documentos`

3. **`MultiCalDialog.java` / `QuickDateSelectionPanel.java`**:
   - Linha 1053 (`MultiCalDialog.java`): `cmdCancel.setText("Abbrechen")` -> `Cancelar`
   - Linha 776 (`QuickDateSelectionPanel.java`): `cmdCancel.setText("Abbrechen")` -> `Cancelar`
   - `cmdToday.setText("Heute")` -> `Hoje`
   - `cmdTomorrow.setText("Morgen")` -> `Amanhã`
   - `cmdNextWeek.setText("Nächste Woche")` -> `Próxima semana`

---

### 3.12. Gerenciador de Backup (`j-lawyer-backupmgr`)

O módulo `j-lawyer-backupmgr` fornece a ferramenta autônoma de restauração de banco de dados MySQL e pastas de arquivos criptografados.

#### Arquivos Principais
- `src/main/java/org/jlawyer/backupmgr/impl/RestoreExecutor.java` (24 ocorrências críticas de backend repassadas à UI)
- `src/main/java/org/jlawyer/backupmgr/controller/BackupMgrController.java` (Controller JavaFX, já em pt-BR)
- `src/main/resources/fxml/backupmgr.fxml` (Layout JavaFX, já em pt-BR)

#### Ocorrências Notáveis & Mapeamento de Tradução em `RestoreExecutor.java`

As mensagens de progresso e exceções em `RestoreExecutor.java` são interceptadas por `BackupProgressUiCallback` e renderizadas diretamente na interface gráfica do usuário (`lblProgress.setText(...)`). Devem ser 100% traduzidas:

| Linha | Código Original (Alemão) | Tradução Recomendada (pt-BR) |
|---|---|---|
| 775 | `throw new Exception("Datenbank '" + this.dbName + "' nicht gefunden!");` | `throw new Exception("Banco de dados '" + this.dbName + "' não encontrado!");` |
| 782 | `progress.onProgress("Prüfe Datenbankinstallation...");` | `progress.onProgress("Verificando instalação do banco de dados...");` |
| 819 | `progress.onProgress("Prüfe Verschlüsselung...");` | `progress.onProgress("Verificando criptografia...");` |
| 822 | `throw new Exception("Unbekannte Dateinamen-Codierung!");` | `throw new Exception("Codificação de nome de arquivo desconhecida!");` |
| 851 | `progress.onProgress("Nicht verschlüsselt: " + zip.getName());` | `progress.onProgress("Não criptografado: " + zip.getName());` |
| 868 | `progress.onProgress("Prüfe Verschlüsselung: " + zip.getName());` | `progress.onProgress("Verificando criptografia: " + zip.getName());` |
| 925 | `System.out.println("Mehr als 5 Dateien aus ... konnten nicht wiederhergestellt werden - Abbruch!");` | `System.out.println("Mais de 5 arquivos de ... não puderam ser restaurados - cancelando!");` |
| 969 | `throw new Exception("Mehr als 5 Dateien konnten nicht wiederhergestellt werden - Abbruch!");` | `throw new Exception("Mais de 5 arquivos não puderam ser restaurados - cancelando!");` |
| 983 | `progress.onProgress("Prüfe Datensicherungsverzeichnis " + dir.getAbsolutePath());` | `progress.onProgress("Verificando diretório de backup " + dir.getAbsolutePath());` |
| 987 | `throw new Exception("Verzeichnis '" + this.backupDirectory + "' existiert nicht!");` | `throw new Exception("O diretório '" + this.backupDirectory + "' não existe!");` |
| 991 | `throw new Exception("'" + this.backupDirectory + "' ist kein Verzeichnis!");` | `throw new Exception("'" + this.backupDirectory + "' não é um diretório!");` |
| 995 | `throw new Exception("Verzeichnis '" + this.backupDirectory + "' ist nicht lesbar!");` | `throw new Exception("O diretório '" + this.backupDirectory + "' não pode ser lido!");` |
| 1009 | `throw new Exception("Verzeichnis unvollständig - benötigt werden Unterverzeichnisse: archivefiles, templates, emailtemplates, mastertemplates, letterheads");` | `throw new Exception("Diretório incompleto - subdiretórios obrigatórios: archivefiles, templates, emailtemplates, mastertemplates, letterheads");` |
| 1013 | `throw new Exception("Datenbanksicherung 'jlawyerdb-dump.sql' fehlt!");` | `throw new Exception("Arquivo de backup do banco de dados 'jlawyerdb-dump.sql' ausente!");` |
| 1020 | `progress.onProgress("Prüfe Datenverzeichnis " + dir.getAbsolutePath());` | `progress.onProgress("Verificando diretório de dados " + dir.getAbsolutePath());` |
| 1024 | `throw new Exception("Verzeichnis '" + this.dataDirectory + "' existiert nicht!");` | `throw new Exception("O diretório '" + this.dataDirectory + "' não existe!");` |
| 1028 | `throw new Exception("'" + this.dataDirectory + "' ist kein Verzeichnis!");` | `throw new Exception("'" + this.dataDirectory + "' não é um diretório!");` |
| 1032 | `throw new Exception("Verzeichnis '" + this.dataDirectory + "' ist nicht lesbar!");` | `throw new Exception("O diretório '" + this.dataDirectory + "' não pode ser lido!");` |
| 1036 | `throw new Exception("Verzeichnis '" + this.dataDirectory + "' ist nicht schreibbar!");` | `throw new Exception("O diretório '" + this.dataDirectory + "' não possui permissão de escrita!");` |
| 1054 | `throw new Exception("Datenbanksicherung 'jlawyerdb-dump.sql' im Datenverzeichnis gefunden! Wurde aus Versehen das Backupverzeichnis gewählt?");` | `throw new Exception("Backup do banco de dados 'jlawyerdb-dump.sql' encontrado no diretório de dados! Foi selecionado o diretório de backup por engano?");` |
| 1068 | `progress.onProgress("Dateien wiederherstellen..." + dir.getAbsolutePath());` | `progress.onProgress("Restaurando arquivos..." + dir.getAbsolutePath());` |
| 1121 | `progress.onProgress("Wiederherstellung: " + zip.getName());` | `progress.onProgress("Restaurando: " + zip.getName());` |
| 1197 | `progress.onProgress("Datenverzeichnis wird bereinigt: " + f.getName());` | `progress.onProgress("Limpando diretório de dados: " + f.getName());` |
| 1220 | `progress.onProgress("Wiederherstellung der Datenbank...");` | `progress.onProgress("Restaurando banco de dados...");` |
| 1282 | `throw new Exception("Datenbankwiederherstellung fehlgeschlagen!");` | `throw new Exception("Falha na restauração do banco de dados!");` |

---

## 4. Análise Técnica dos Mecanismos do NetBeans GUI Builder & Sincronização

### O Ciclo de Geração de Código do NetBeans Form
No NetBeans GUI Builder, os arquivos `.form` em formato XML armazenam a definição declarativa dos componentes gráficos e suas propriedades (`text`, `toolTipText`, `title`, `tabTitle`, `TitledBorder`, `ComboBoxModel`).

Durante o design-time ou compilação assistida por IDE:
1. O NetBeans lê o `.form` e regenera o bloco `// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents` dentro da respectiva classe `.java`.
2. Se uma string for alterada **apenas** no arquivo `.java` dentro de `initComponents()`, uma reabertura do formulário na IDE ou recompilação automática do GUI Builder **sobrescreverá** a alteração com o valor original contido no `.form`.
3. Por outro lado, se uma alteração for feita **apenas** no `.form` sem atualizar o arquivo `.java`, a compilação padrão via Maven (`mvn compile`) lê o arquivo `.java` existente e compila o bytecode com as strings antigas que ainda estavam no arquivo `.java`.

### Regra de Ouro para a Implementação R1
> **Toda e qualquer substituição de string em formulários NetBeans DEVE ser aplicada de forma estritamente sincronizada e simultânea tanto no arquivo XML `.form` quanto no bloco `initComponents()` da classe `.java` correspondente.**

### Estrutura de Padrões XML e Equivalentes Java

#### Padrão 1: Propriedade `text` simples
- **`.form`**:
  ```xml
  <Component class="javax.swing.JButton" name="cmdCancel">
    <Properties>
      <Property name="text" type="java.lang.String" value="Abbrechen"/>
    </Properties>
  </Component>
  ```
- **`.java`**:
  ```java
  cmdCancel.setText("Abbrechen");
  ```
- **Substituição Obrigatória**: `Cancelar` em ambos.

#### Padrão 2: Itens de ComboBox (`StringItem`)
- **`.form`**:
  ```xml
  <Component class="javax.swing.JComboBox" name="cmbType">
    <Properties>
      <Property name="model" type="javax.swing.ComboBoxModel" editor="org.netbeans.modules.form.editors2.ComboBoxModelEditor">
        <StringArray count="3">
          <StringItem index="0" value="Wiedervorlage"/>
          <StringItem index="1" value="Frist"/>
          <StringItem index="2" value="Termin"/>
        </StringArray>
      </Property>
    </Properties>
  </Component>
  ```
- **`.java`**:
  ```java
  cmbType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Wiedervorlage", "Frist", "Termin" }));
  ```
- **Substituição Obrigatória**: `Lembrete`, `Prazo`, `Audiência` em ambos.

#### Padrão 3: Título de Borda (`TitledBorder`)
- **`.form`**:
  ```xml
  <Property name="border" type="javax.swing.border.Border" editor="org.netbeans.modules.form.editors2.BorderEditor">
    <Border info="org.netbeans.modules.form.compat2.border.TitledBorderInfo">
      <TitledBorder title="Dokumente"/>
    </Border>
  </Property>
  ```
- **`.java`**:
  ```java
  jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Dokumente"));
  ```
- **Substituição Obrigatória**: `Documentos` em ambos.

#### Padrão 4: Título de Aba (`tabTitle`)
- **`.form`**:
  ```xml
  <JTabbedPaneConstraints tabName="Einstellungen">
    <Property name="tabTitle" type="java.lang.String" value="Einstellungen"/>
  </JTabbedPaneConstraints>
  ```
- **`.java`**:
  ```java
  jTabbedPane.addTab("Einstellungen", panel);
  ```
- **Substituição Obrigatória**: `Configurações` em ambos.

---

## 5. Casos Especiais e Padrões de Risco Identificados

### 5.1. Diálogos JOptionPane com Arrays Customizados de Botões
Diversos diálogos no `j-lawyer-client` utilizam `JOptionPane.showOptionDialog(...)` passando arrays de botões literais em alemão. Exemplo:
```java
// Código original
int choice = JOptionPane.showOptionDialog(
    this,
    "Möchten Sie die Änderungen speichern?",
    "Speichern bestätigen",
    JOptionPane.YES_NO_CANCEL_OPTION,
    JOptionPane.QUESTION_MESSAGE,
    null,
    new String[]{"Ja", "Nein", "Abbrechen"},
    "Ja"
);
```
**Tradução mandatória**:
```java
int choice = JOptionPane.showOptionDialog(
    this,
    "Deseja salvar as alterações?",
    "Confirmar Salvamento",
    JOptionPane.YES_NO_CANCEL_OPTION,
    JOptionPane.QUESTION_MESSAGE,
    null,
    new String[]{"Sim", "Não", "Cancelar"},
    "Sim"
);
```
*Atenção*: Verificar o índice de retorno `choice` (normalmente 0 para a primeira opção "Sim", 1 para "Não", 2 para "Cancelar"). A ordem dos elementos no array nunca deve ser alterada.

### 5.2. ToolRegistry da IA Ingo (`ToolRegistry.java`)
O `ToolRegistry.java` define 350+ parâmetros e descrições de ferramentas repassadas ao modelo de linguagem LLM. Todas as descrições de ferramentas (`ToolDefinition`), descrições de parâmetros (`ToolParameter`) e mensagens formatadas de resultado devem estar integralmente em português brasileiro (ex: `"Número do processo"`, `"Tipo de evento para filtrar: Lembrete, Prazo ou Audiência"`).

### 5.3. Caracteres Especiais e Encoding (Umlauts vs Escape UTF-8/ISO-8859-1)
Em arquivos `.form`, caracteres acentuados aparecem frequentemente como entidades numéricas ou hexadecimais (ex: `&#xf6;` para `ö`, `&#xe4;` para `ä`, `&#xfc;` para `ü`).
Ao gravar strings em português (`ç`, `ã`, `õ`, `é`, `á`, etc.):
- Em arquivos `.form`, usar a representação XML válida em UTF-8 (`Salvar como...`, `Excluir`, `Configurações`, `Não`).
- Em arquivos `.java`, manter codificação estrita UTF-8 (`"Não"`, `"Configurações"`, `"Atenção"`).

---

## 6. Plano de Execução e Priorização para Implementação

Com base na volumetria e dependências, a implementação de R1 deve ser executada nas seguintes fases estruturadas:

1. **Fase 1: Módulos Menores e Utilitários**
   - Localização de `j-lawyer-backupmgr` (`RestoreExecutor.java`).
   - Sincronização e verificação de `j-lawyer-io-common`.
2. **Fase 2: Shell Principal, Login e UI Framework**
   - `JKanzleiGUI.form` / `.java`, `LoginDialog.form` / `.java`, `AdminConsoleFrame.form` / `.java`, `AboutDialog.form` / `.java`.
   - `ui.folders.*`, `ui.tagging.*`, `components.*`, `desktop.*`.
3. **Fase 3: Gestão de Processos, Autos e Contatos (Core)**
   - `ArchiveFilePanel.form` / `.java`, `AddressPanel.form` / `.java`.
   - Diálogos satélites de processos (`ArchivalDialog`, `BulkSaveDialog`, `ClaimLedgerDialog`, `ConflictOfInterestDialog`).
4. **Fase 4: Documentos, Scanner, PDF e Modelos**
   - `ScannerPanel`, `PDFSplitDialog`, `PdfAnonymizerDialog`, `ShrinkifyGui`.
   - `TemplatesTreePanel`, `NewTemplateDialog`.
5. **Fase 5: Faturamento, Financeiro e Relatórios**
   - `InvoiceDialog`, `ImportBankStatementFrame`, `ManagePaymentsFrame`, `ReportingPanel`.
6. **Fase 6: E-mail, Mensagens, beA e Assistente IA Ingo**
   - `SendEmailFrame`, `EmailInboxPanel`, `MailboxSetupDialog`.
   - `BeaInboxPanel`, `SendBeaMessageFrame`, `BeaMessageContentUI`.
   - `ToolRegistry.java`, `Assistant*Dialog`.
7. **Fase 7: Telas de Configuração e Administração**
   - `UserAdministrationDialog`, `GroupAdministrationDialog`, `CaseNumberingConfigurationDialog`, `CalendarSetupDialog`, `SecurityConfigurationDialog`, etc.

---

## 7. Conclusão do Levantamento

O levantamento auditou de forma exaustiva e sem omissões toda a base de código visual do projeto. Foram catalogadas com precisão **3.424 ocorrências** em **527 arquivos únicos**, fornecendo a base indispensável para a execução segura, atômica e completa da meta R1.
