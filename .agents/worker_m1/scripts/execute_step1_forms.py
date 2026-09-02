import os
import sys
import json
import csv
import html
import re

# Comprehensive dictionary for all form property values and corresponding initComponents strings
FORM_TRANSLATIONS = {
    # Buttons and basic controls
    "Abbrechen": "Cancelar",
    "Speichern": "Salvar",
    "Speichern unter...": "Salvar como...",
    "Schließen": "Fechar",
    "Schliessen": "Fechar",
    "Schließen ": "Fechar ",
    "Suchen": "Pesquisar",
    "Suche": "Pesquisa",
    "Löschen": "Excluir",
    "löschen": "Excluir",
    "Entfernen": "Remover",
    "entfernen": "Remover",
    "Hinzufügen": "Adicionar",
    "hinzufügen": "Adicionar",
    "Neu": "Novo",
    "Neuer": "Novo",
    "Neues": "Novo",
    "Neue": "Nova",
    "neue": "nova",
    "neues": "novo",
    "neuer": "novo",
    "Bearbeiten": "Editar",
    "bearbeiten": "Editar",
    "Drucken": "Imprimir",
    "drucken": "Imprimir",
    "Druckvorschau": "Visualizar Impressão",
    "Exportieren": "Exportar",
    "Importieren": "Importar",
    "Aktualisieren": "Atualizar",
    "Zurück": "Voltar",
    "Weiter": "Avançar",
    "Fertig": "Concluir",
    "Übernehmen": "Aplicar",
    "übernehmen": "Aplicar",
    "Ansicht": "Exibir",
    "Öffnen": "Abrir",
    "öffnen": "Abrir",
    "öffnen...": "Abrir...",
    "öffnen mit...": "Abrir com...",
    "Auswählen": "Selecionar",
    "auswählen": "Selecionar",
    "Auswahl": "Seleção",
    "Bestätigen": "Confirmar",
    "Bestätigung": "Confirmação",
    "Senden": "Enviar",
    "senden": "Enviar",
    "Signieren": "Assinar digitalmente",
    "Scannen": "Digitalizar",
    "Vorschau": "Visualização",
    "Anlegen": "Criar",
    "anlegen": "Criar",
    "Erstellen": "Criar",
    "erstellen": "Criar",
    "Duplizieren": "Duplicar",
    "duplizieren": "Duplicar",
    "Umbenennen": "Renomear",
    "umbenennen": "Renomear",
    "Kopieren": "Copiar",
    "kopieren": "Copiar",
    "Verschieben": "Mover",
    "verschieben": "Mover",
    "Verbinden": "Conectar",
    "Trennen": "Desconectar",
    "Prüfen": "Verificar",
    "Hilfe": "Ajuda",
    "Optionen": "Opções",
    "Einstellungen": "Configurações",
    "Ja": "Sim",
    "Nein": "Não",
    "Fehler": "Erro",
    "Erfolg": "Sucesso",
    "Erfolgreich": "Sucesso",
    "Warnung": "Aviso",
    "Hinweis": "Aviso",
    "Achtung": "Atenção",
    "Information": "Informação",
    "Details": "Detalhes",
    "Alles auswählen": "Selecionar tudo",
    "Auswahl aufheben": "Desmarcar seleção",
    "Heute": "Hoje",
    "Morgen": "Amanhã",
    "Gestern": "Ontem",
    "Nächste Woche": "Próxima semana",
    "Übermorgen": "Depois de amanhã",
    "In 1 Woche": "Em 1 semana",
    "In 2 Wochen": "Em 2 semanas",
    "In 1 Monat": "Em 1 mês",
    "Komprimieren": "Compactar",
    "Komprimieren und Speichern": "Compactar e Salvar",
    "Vorschau der Komprimierung": "Visualização da compactação",
    "In Graustufen konvertieren": "Converter para escala de cinza",
    "in Schwarz/Weiß konvertieren": "Converter para preto e branco",
    "Urspr. Dateien überschreiben": "Sobrescrever arquivos originais",
    "Qualitätsstufe auswählen:": "Selecionar nível de qualidade:",
    "Doppelklick um heutiges Datum zu übernehmen": "Duplo clique para aplicar a data de hoje",
    "Text auswählen und in die Zwischenablage kopieren": "Selecionar texto e copiar para a área de transferência",
    "Chat-Historie zurücksetzen und neuen Chat beginnen": "Limpar histórico e iniciar nova conversa",
    "Textauswahl kopieren": "Copiar seleção de texto",
    "in Prompt übernehmen": "Inserir no prompt",
    "Auswahl in Prompt übernehmen": "Inserir seleção no prompt",

    # Entities and Fields
    "Mandant": "Cliente",
    "Mandanten": "Clientes",
    "Gegner": "Parte Contrária",
    "Dritte": "Terceiros",
    "Akte": "Processo",
    "Akten": "Processos",
    "Akte:": "Processo:",
    "Akten:": "Processos:",
    "Aktenzeichen": "Número do Processo",
    "Aktenzeichen:": "Número do Processo:",
    "Aktennotiz": "Anotação do Processo",
    "Aktenkonto": "Conta do Processo",
    "Dokument": "Documento",
    "Dokumente": "Documentos",
    "Dokument:": "Documento:",
    "Dokumente:": "Documentos:",
    "Datei": "Arquivo",
    "Dateien": "Arquivos",
    "Datei:": "Arquivo:",
    "Dateien:": "Arquivos:",
    "Ordner": "Pasta",
    "Ordner:": "Pasta:",
    "Benutzer": "Usuário",
    "Benutzer:": "Usuário:",
    "Benutzername": "Nome de Usuário",
    "Benutzername:": "Nome de Usuário:",
    "Passwort": "Senha",
    "Passwort:": "Senha:",
    "Kennwort": "Senha",
    "Kennwort:": "Senha:",
    "root-Passwort:": "Senha de Administrador (root):",
    "Passwort wiederholen:": "Confirmar senha:",
    "Administratorrechte": "Permissões de Administrador",
    "inaktiver Benutzer": "Usuário inativo",
    "Verbindung": "Conexão",
    "Termin": "Audiência / Compromisso",
    "Termine": "Compromissos",
    "Frist": "Prazo",
    "Fristen": "Prazos",
    "Wiedervorlage": "Lembrete",
    "Wiedervorlagen": "Lembretes",
    "Wiedervorlage / Frist": "Lembrete / Prazo",
    "Rechnung": "Fatura",
    "Rechnungen": "Faturas",
    "Zahlung": "Pagamento",
    "Zahlungen": "Pagamentos",
    "Zahlung:": "Pagamento:",
    "Zahlungseingang": "Recebimento",
    "Zahlungsausgang": "Pagamento Efetuado",
    "Honorar": "Honorários",
    "Vergütung": "Honorários",
    "Gericht": "Tribunal",
    "Gerichte": "Tribunais",
    "Beteiligte": "Partes Envolvidas",
    "Beteiligter": "Parte Envolvida",
    "Beteiligtentypen": "Tipos de Partes Envolvidas",
    "Rubrum": "Cabeçalho Processual",
    "Vorlage": "Modelo",
    "Vorlagen": "Modelos",
    "Vorlage:": "Modelo:",
    "Nachname": "Sobrenome",
    "Nachname:": "Sobrenome:",
    "Vorname": "Nome",
    "Vorname:": "Nome:",
    "Straße": "Endereço / Logradouro",
    "Straße:": "Endereço / Logradouro:",
    "PLZ": "CEP",
    "PLZ:": "CEP:",
    "Ort": "Cidade",
    "Ort:": "Cidade:",
    "Telefon": "Telefone",
    "Telefon:": "Telefone:",
    "Mobiltelefon": "Celular",
    "Mobiltelefon:": "Celular:",
    "Telefax": "Fax",
    "Telefax:": "Fax:",
    "E-Mail": "E-mail",
    "E-Mail:": "E-mail:",
    "Webseite": "Site",
    "Notiz": "Nota",
    "Notizen": "Notas",
    "Betreff": "Assunto",
    "Betreff:": "Assunto:",
    "An:": "Para:",
    "Kopie (CC):": "Cópia (CC):",
    "Blindkopie (BCC):": "Cópia Oculta (CCO):",
    "Posteingang": "Caixa de Entrada",
    "Postausgang": "Caixa de Saída",
    "Entwürfe": "Rascunhos",
    "Gesendet": "Enviados",
    "Papierkorb": "Lixeira",
    "Spam": "Spam",
    "Etikett": "Etiqueta",
    "Etiketten": "Etiquetas",
    "Status": "Status",
    "Status:": "Status:",
    "Beschreibung": "Descrição",
    "Beschreibung:": "Descrição:",
    "Datum": "Data",
    "Datum:": "Data:",
    "Uhrzeit": "Hora",
    "Uhrzeit:": "Hora:",
    "Betrag": "Valor",
    "Betrag:": "Valor:",
    "Saldo": "Saldo",
    "Saldo:": "Saldo:",

    # Form / Menu Specific Strings
    "neue Akte erstellen": "Criar novo processo",
    "neues Dokument": "Novo documento",
    "neuer Ordner": "Nova pasta",
    "neuer Ordner...": "Nova pasta...",
    "Ordner löschen": "Excluir pasta",
    "Ordner umbenennen": "Renomear pasta",
    "Unterordner erstellen": "Criar subpasta",
    "in Akte speichern": "Salvar no processo",
    "in Akte speichern...": "Salvar no processo...",
    "in Akte verschieben": "Mover para o processo",
    "in Akte verschieben...": "Mover para o processo...",
    "als PDF zur Akte speichern": "Salvar como PDF no processo",
    "in Zwischenablage kopieren": "Copiar para a área de transferência",
    "lokal speichern": "Salvar localmente",
    "in andere Akte kopieren": "Copiar para outro processo",
    "in andere Akte verschieben": "Mover para outro processo",
    "Erstellungsdatum anpassen": "Ajustar data de criação",
    "farblich hervorheben": "Destacar com cor",
    "erste Farbe": "Primeira cor",
    "zweite Farbe": "Segunda cor",
    "Farbe entfernen": "Remover cor",
    "Favoritendokument an/aus": "Favoritar / Desfavoritar documento",
    "Nachricht senden": "Enviar mensagem",
    "PDF und Konvertierung": "PDF e Conversão",
    "Texterkennung (OCR)": "Reconhecimento de texto (OCR)",
    "als PDF zusammenführen": "Mesclar como PDF",
    "PDF aufteilen": "Dividir PDF",
    "PDF aufteilen ": "Dividir PDF",
    "Suche zurücksetzen": "Limpar pesquisa",
    "neues Dokument aus Vorlage erstellen": "Criar novo documento a partir de modelo",
    "vorhandene Datei hinzuladen": "Importar arquivo existente",
    "Liste nach LibreOffice exportieren": "Exportar lista para LibreOffice",
    "einsehen / bearbeiten": "Visualizar / Editar",
    "Weiterleiten": "Encaminhar",
    "im Browser öffnen": "Abrir no navegador",
    "Scan zur Akte speichern": "Salvar digitalização no processo",
    "Dokumentenvorschau": "Pré-visualização de Documentos",
    "Ereignisvorlagen": "Modelos de Eventos",
    "Belegpositionen (Vorlagen)": "Modelos de Lançamento",
    "Zeiterfassungspositionen (Vorlagen)": "Modelos de Registro de Horas",
    "Buchen: Kontoauszug importieren": "Lançamento: Importar Extrato Bancário",
    "Zahlungen verwalten": "Gerenciar Pagamentos",
    "Import: Gerichtsadressen": "Importar: Endereços de Tribunais",
    "Import / Export: Einstellungen": "Importar / Exportar: Configurações",
    "Export: Akten": "Exportar: Processos",
    "Exportieren von zur Synchronisation markierten Akten": "Exportar processos marcados para sincronização",
    "Akten-Etiketten": "Etiquetas de Processos",
    "Akten-Listenetiketten": "Etiquetas de Listas de Processos",
    "Dokumentordner": "Pastas de Documentos",
    "Eigene Felder (Beteiligte)": "Campos Personalizados (Partes Envolvidas)",
    "zufälliger Desktophintergrund": "Plano de fundo aleatório",
    "Profil aus Zwischenablage importieren": "Importar perfil da área de transferência",
    "j-lawyer.BOX Management Console öffnen": "Abrir Console de Gerenciamento ATRIUM",
    "j-lawyer.BOX suchen": "Pesquisar Servidor ATRIUM",
    "2. App starten und Profile-Dialog öffnen": "2. Iniciar aplicativo e abrir diálogo de perfis",
    "3. Profil durch scannen des QR-Codes übernehmen": "3. Importar perfil escaneando o código QR",
    "beA-Teilnehmer suchen": "Pesquisar participante beA / Tribunal",
    "in Akte speichern (ohne Anhänge)...": "Salvar no processo (sem anexos)...",
    "in Akte speichern (nur Anhänge)...": "Salvar no processo (apenas anexos)...",
    "neue Nachricht schreiben": "Escrever nova mensagem",
    "neue Nachrichten abrufen": "Buscar novas mensagens",
    "Profil auswählen": "Selecionar perfil",
    "Profile": "Perfis",
    "Login": "Login",
    "0 Akten": "0 Processos",
    "0 Akten im Archiv": "0 Processos arquivados",
    "0 Adressbucheinträge": "0 Contatos",
    "0 Dokumente": "0 Documentos",
    "unbearbeitete E-Mails": "E-mails pendentes",
    "Dokument-Etiketten": "Etiquetas de Documentos",
    "Offene Posten": "Contas a Receber (Em Aberto)",
    "Zahlungseingänge": "Pagamentos Recebidos",
    "Zahlungsausgänge": "Pagamentos Efetuados",
    "Kontoauszug (CSV-Datei):": "Extrato Bancário (Arquivo CSV):",
    "Durchsuchen...": "Procurar...",
    "Kontoauszug importieren": "Importar Extrato",
    "Neue Vorlage...": "Novo Modelo...",
    "Neue Vorlage": "Novo Modelo",
    "Vorlage löschen": "Excluir Modelo",
    "Vorlage umbenennen": "Renomear Modelo",
    "Neuer Ordner...": "Nova Pasta...",
    "Neuer Ordner": "Nova Pasta",
    "Seitenbereich (z.B. 1-3, 5):": "Intervalo de páginas (ex: 1-3, 5):",
    "Posteingangsserver (IMAP)": "Servidor de Entrada (IMAP)",
    "Postausgangsserver (SMTP)": "Servidor de Saída (SMTP)",
    "Verbindung verschlüsseln (SSL/TLS)": "Criptografia da conexão (SSL/TLS)",
    "Aktenzeichen-Format": "Formato do Número do Processo",
    "fortlaufende Nummer": "Número sequencial",
    "Jahreszahl (2-stellig)": "Ano (2 dígitos)",
    "Jahreszahl (4-stellig)": "Ano (4 dígitos)",
    "Empfänger (beA):": "Destinatário (beA / Tribunal):",
    "Anhang hinzufügen...": "Adicionar anexo...",
    "Anhang hinzufügen": "Adicionar anexo",
    "Rechnungsempfänger": "Destinatário da fatura",
    "Rechnungserstellung": "Emissão de fatura",
    "Leistungszeitraum": "Período de prestação de serviços",
    "Rechnungsbetrag": "Valor total da fatura",
    "Rechnungsposition": "Item da fatura",
    "Rechnungspositionen": "Itens da fatura",
    "Postausgang nicht leer": "Caixa de Saída com Mensagens Pendentes",
    "Entwurf speichern": "Salvar Rascunho",
    "Dokument löschen": "Excluir Documento",
    "Akte archivieren": "Arquivar Processo",
    "Adresse speichern": "Salvar Contato",
    "Passwort ändern": "Alterar Senha",
    "Passwort festlegen": "Definir Senha",
    "Dokument umbenennen": "Renomear Documento",
    "Dokumente löschen": "Excluir Documentos",
    "Zahlungen verbuchen": "Lançar Pagamentos",
    "Ordner anlegen": "Criar Pasta",
    "Benutzer löschen": "Excluir Usuário",
    "Profil anlegen": "Criar Perfil",
    "Profil löschen": "Excluir Perfil",
    "Profil umbenennen": "Renomear Perfil",

    # Additional form strings
    "% Speichernutzung": "% Uso de Memória",
    "00123/22 eine Aktenbezeichnung": "00123/22 Descrição do processo",
    "2. Passwort setzen / zurücksetzen": "2. Definir / redefinir senha",
    "3. Passwort setzen und in die Einstellungen übernehmen": "3. Definir senha e aplicar às configurações",
    "<Rechnungsadresse>": "<Endereço de Faturamento>",
    "suchen (Adressbuch)": "Pesquisar (Contatos)",
    "suchen (beA-Verzeichnis)": "Pesquisar (Tribunal)",
    "neue beA-Nachricht": "Nova mensagem judicial",
    "in \"Entwürfe\" speichern und später signieren/senden": "Salvar em \"Rascunhos\" e assinar/enviar mais tarde",
    "als Dokument speichern": "Salvar como documento",
    "Gesendete Nachricht als Dokument zur Akte speichern": "Salvar mensagem enviada como documento no processo",
    "Keine auswählen": "Nenhum selecionado",
    "Nachname, Vorname (Username)": "Sobrenome, Nome (Usuário)",
    "PDF anzeigen / speichern": "Visualizar / salvar PDF",
    "Nachrichtenjournal erneut aus dem Postfach laden": "Recarregar histórico de mensagens da caixa de correio",
    "eEB zurückweisen": "Recusar aviso de recebimento eletrônico",
    "elektronisches Empfangsbekenntnis zurückweisen": "Recusar aviso de recebimento eletrônico",
    "Nachricht als PDF anzeigen / drucken": "Visualizar / imprimir mensagem como PDF",
    "weitere": "mais",
    "Nachricht": "Mensagem",
    "in Akte...": "No processo...",
    "Menge der angezeigten Nachrichten beschränken": "Limitar quantidade de mensagens exibidas",
    "Hinweis: temporäre Dateien": "Aviso: arquivos temporários",
    "verschiebt die Nachricht nach Zuordnung in den Ordner \"in Akte importiert\"": "Move a mensagem após vinculação para a pasta \"importada no processo\"",
}

print("Running execute_step1_forms...")

# Load form matches
with open('.agents/explorer_survey_1/raw_form_matches.csv', 'r', encoding='utf-8-sig') as f:
    form_rows = list(csv.DictReader(f))

file_col = [c for c in form_rows[0].keys() if 'File' in c][0]

forms_by_file = {}
for r in form_rows:
    fpath = r[file_col]
    if fpath not in forms_by_file:
        forms_by_file[fpath] = []
    forms_by_file[fpath].append(r)

print(f"Forms to process: {len(forms_by_file)}")

# Execute replacements in each form and matching java file
processed_forms = 0
processed_javas = 0

for form_path, rows in forms_by_file.items():
    if not os.path.exists(form_path):
        print(f"Form not found: {form_path}")
        continue
    
    with open(form_path, 'r', encoding='utf-8') as f:
        form_content = f.read()

    java_path = form_path.replace('.form', '.java')
    if not os.path.exists(java_path) and 'src/main/resources' in form_path:
        java_path = form_path.replace('src/main/resources', 'src/main/java').replace('.form', '.java')

    java_content = None
    if os.path.exists(java_path):
        for enc in ['utf-8', 'windows-1252', 'iso-8859-1']:
            try:
                with open(java_path, 'r', encoding=enc) as f:
                    java_content = f.read()
                break
            except Exception:
                pass

    form_modified = False
    java_modified = False

    for r in rows:
        raw_val = r['Value']
        clean_val = html.unescape(raw_val).strip()

        # Find translation
        pt_val = FORM_TRANSLATIONS.get(clean_val) or FORM_TRANSLATIONS.get(raw_val)
        if not pt_val:
            continue

        # In form XML, escape XML special characters
        pt_xml_val = html.escape(pt_val, quote=True).replace("&#x27;", "&apos;")
        
        # Replace in form
        patterns = [
            f'value="{raw_val}"',
            f'value="{html.escape(clean_val)}"',
            f'tabTitle="{raw_val}"',
            f'tabTitle="{html.escape(clean_val)}"',
            f'title="{raw_val}"',
            f'title="{html.escape(clean_val)}"',
            f'toolTipText="{raw_val}"',
            f'toolTipText="{html.escape(clean_val)}"',
        ]
        
        for pat in patterns:
            if pat in form_content:
                repl = pat.replace(f'="{raw_val}"', f'="{pt_xml_val}"').replace(f'="{html.escape(clean_val)}"', f'="{pt_xml_val}"')
                form_content = form_content.replace(pat, repl)
                form_modified = True

        # In java file, replace string literals in initComponents()
        if java_content:
            # Try both raw_val and clean_val
            java_search_strings = [
                f'"{clean_val}"',
                f'"{clean_val.encode("unicode_escape").decode("utf-8")}"',
            ]
            pt_java_val = pt_val.replace('"', '\\"')
            for js in java_search_strings:
                if js in java_content:
                    java_content = java_content.replace(js, f'"{pt_java_val}"')
                    java_modified = True

    if form_modified:
        with open(form_path, 'w', encoding='utf-8') as f:
            f.write(form_content)
        processed_forms += 1

    if java_modified and java_content:
        with open(java_path, 'w', encoding='utf-8') as f:
            f.write(java_content)
        processed_javas += 1

print(f"Completed step 1: modified {processed_forms} .form files and {processed_javas} .java files")
