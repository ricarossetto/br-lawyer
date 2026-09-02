# ⚖️ BR-LAWYER — Plataforma Jurídica Integrada

[![License: AGPLv3 / MIT](https://img.shields.io/badge/License-AGPLv3%20%2F%20MIT-blue.svg)](LICENSE)
[![Node: >=24](https://img.shields.io/badge/Node.js-24.x-green.svg)](https://nodejs.org/)
[![Java: 17](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Security: AES-256-GCM](https://img.shields.io/badge/Security-AES--256--GCM-blue.svg)](#segurança-e-criptografia)
[![2FA: TOTP](https://img.shields.io/badge/2FA-TOTP%20RFC%206238-blueviolet.svg)](#segurança-e-criptografia)

O **BR-LAWYER** é uma solução completa, modular e open-source para escritórios de advocacia e departamentos jurídicos no Brasil. Combina a robustez de gestão processual e empresarial com a agilidade do workspace web local-first **Juris-Flow (ATRIUM v2.0)**, coletores de tribunais automatizados e assistente de inteligência artificial supervisionado.

---

## 🚀 Funcionalidades Principais

### 1. 🏛️ Integrações Judiciais & Coletores Nacionais
- **DataJud (CNJ API)**: Consulta e sincronização de metadados processuais de todos os tribunais do país.
- **DJEN (Diário da Justiça Eletrônico Nacional)**: Coleta automática de publicações e intimações.
- **Robôs de Raspagem (PJe, eproc, esaj)**: Monitoramento de movimentações processuais.
- **Discovery Judicial Read-Only**: Descoberta segura por OAB/CNJ sem produzir ciência ou ato judicial prematuro.

### 2. 🔐 Segurança & Criptografia Avançada
- **Criptografia AES-256-GCM em Repouso**: Estado e segredos armazenados de forma blindada com gravações atômicas.
- **Autenticação com 2FA TOTP (RFC 6238)**: Segundo fator de autenticação por usuário e proteção contra força bruta.
- **Sandbox de Certificado Digital A1**: Assinatura e autenticação isolada em memória, sem persistência ou vazamento de chaves privadas e senhas.
- **Backups Cifrados (.atrium-backup)**: Exportação e restauração com validação de checksum SHA-256 e snapshot pré-restauração.

### 3. 📊 Gestão Jurídica (UI V2 com 17 Views Canônicas + Classic)
- **Dashboard**: Métricas executivas, prazos iminentes e visão geral do escritório.
- **Processos**: Gestão por número CNJ, árvore de movimentações, partes e tribunal.
- **Publicações / DJEN**: Triagem com status de tratamento (`untreated`, `in_review`, `treated`, `discarded`) e geração de boletins.
- **Agenda & Prazos Processuais**: Contagem de prazos em dias úteis (CPC/CLT), feriados nacionais/locais, suspensões forenses e conferência humana obrigatória.
- **Tarefas & Kanban**: Gestão visual de fluxos de trabalho e pendências processuais.
- **Contatos & Clientes**: Cadastro completo de clientes, partes e advogados.
- **Leads & CRM**: Funil de captação e novos atendimentos.
- **Financeiro**: Honorários contratuais e sucumbenciais, custas, despesas e reembolsos.
- **Documentos & Minutas**: Inteligência documental e indexação para busca textual full-text.
- **Banco de Prompts Jurídicos**: Mais de 350KB de modelos de petições, defesas e recursos pré-configurados.
- **Assistente IA (Google Gemini)**: Minimização de contexto enviado e supervisão humana obrigatória.
- **Auditoria, Monitoramento & Importador**: Histórico completo de eventos, logs de robôs e importação em lote via Excel/CSV.

---

## 💻 Como Executar

### Pré-requisitos
- **Node.js 24.x** (para o Web Workspace & Coletores)
- **Java 17** & **Maven** (para o servidor corporativo Java EE e cliente desktop Swing)

### Iniciar o BR-LAWYER Web (Windows)
Basta dar duplo clique em:
```text
iniciar-brlawyer-web.bat
```
ou via PowerShell:
```powershell
.\iniciar-brlawyer-web.ps1
```
O script verifica o ambiente, instala dependências se necessário e abre automaticamente em `http://127.0.0.1:4173`.

### Iniciar o Coletor de Tribunais
```cmd
iniciar-coletor.bat
```

### Executar Testes Automatizados
```bash
corepack pnpm test
```

---

## 📜 Licença
- Módulos Juris-Flow / Web Workspace: Licença **MIT** (Copyright © Ricardo de Luca Rossetto).
- Módulos Core Java EE: Licença **AGPLv3**.
