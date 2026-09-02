# Original User Request

## Initial Request — 2026-09-02T14:42:05Z

Tradução e localização integral e exaustiva de 100% da aplicação Desktop (Swing / FlatLaf / NetBeans forms / Java code / ResourceBundles), eliminando todo e qualquer texto residual em alemão e garantindo que toda a experiência gráfica e textual funcione nativamente em Português do Brasil (pt-BR).

Working directory: c:\projetos IA\BR-LAWYER\br-lawyer
Integrity mode: development

## Requirements

### R1. Varredura e Substituição de Strings Hardcoded em Formulários Swing e Classes Java
- Auditar e varrer todas as classes Java e arquivos .form (NetBeans GUI Builder) nos módulos j-lawyer-client, j-lawyer-backupmgr e j-lawyer-io-common.
- Substituir todas as strings literais geradas em initComponents() e classes de diálogo ("Abbrechen", "Speichern", "Schließen", "Suchen", "Löschen", "Hinzufügen", "Bearbeiten", "Drucken", "Mandant", "Gegner", "Akte", "Dokument", "Ja/Nein", "Fehler", "Erfolg", etc.) pelas correspondentes em português brasileiro ("Cancelar", "Salvar", "Fechar", "Pesquisar", "Excluir", "Adicionar", "Editar", "Imprimir", "Cliente", "Parte Contrária", "Processo", "Documento", "Sim/Não", "Erro", "Sucesso", etc.).
- Garantir que nenhum botão, rótulo, título de aba, tooltip, mensagem de confirmação ou diálogo popup exiba termos em alemão.

### R2. Paridade e Sincronização dos Bundles Raiz (Bundle.properties e Messages.properties)
- Sincronizar todos os bundles padrão/raiz (Bundle.properties, Messages.properties) com o conteúdo integralmente localizado em pt-BR, de modo que qualquer fallback ou lookup sem locale retorne imediatamente a tradução em Português.
- Garantir 100% de paridade entre _pt_BR.properties e os bundles padrão do sistema em todos os 16 módulos.

### R3. Menus, Diálogos de Sistema e Títulos de Janelas do Cliente
- Traduzir a barra de menu completa do Desktop (Arquivo, Editar, Exibir, Processos, Contatos, Documentos, Financeiro, Serviços, Configurações, Ajuda), submenus, aceleradores de teclado e diálogos de splash e login.
- Localizar diálogos de autos processuais, visualizadores de PDF/imagens, exportador de relatórios, assistente de IA (Ingo), configurações de rede/servidor e gerenciador de backup.

### R4. Validação Exaustiva e Verificação de Compilação Maven
- Executar compilação Maven limpa do cliente (mvn clean package -pl j-lawyer-client -am).
- Executar 100% da suíte de testes de internacionalização e UI (PtBrLocalizationTest, M1ChallengerStressTest, BrazilianUiUtilsTest).
- Verificar se a aplicação inicializa em runtime com 0 textos em alemão.

## Acceptance Criteria

### Localização Visual & Strings
- [ ] Varredura com regex em j-lawyer-client/src não encontra strings hardcoded comuns em alemão em botões, abas e títulos.
- [ ] Todos os formulários .form e classes initComponents() utilizam labels e botões em português.
- [ ] Os diálogos de confirmação (JOptionPane, diálogos customizados) exibem títulos e opções ("Sim", "Não", "Cancelar", "OK") em português.

### Bundles & Fallbacks
- [ ] 100% dos arquivos Bundle.properties e Messages.properties no j-lawyer-client e j-lawyer-backupmgr estão sincronizados em português.
- [ ] PtBrLocalizationTest e M1ChallengerStressTest passam com 100% de sucesso.

### Build & Execução
- [ ] mvn test -pl j-lawyer-client passa com 0 erros e 0 falhas.
- [ ] O executável Desktop (run-client.bat / j-lawyer-client.jar) abre nativamente com interface 100% traduzida em pt-BR.
