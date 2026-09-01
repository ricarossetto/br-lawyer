# Modelo de Conformidade com a LGPD (Lei nº 13.709/2018) — BR-LAWYER

> **Documento de Conformidade Regulatória:** `docs/LGPD_MODEL.md`  
> **Versão:** 1.0.0  
> **Status:** Aprovado para Arquitetura

---

## 1. Contexto Jurídico e Aplicação da LGPD na Advocacia

A advocacia lida diariamente com dados pessoais altamente sensíveis (dados cadastrais, bancários, fiscais, de saúde em ações previdenciárias e acidentárias, segredos industriais e detalhes íntimos em ações de família). O **BR-LAWYER** foi concebido com arquitetura de privacidade por padrão (*Privacy by Design and by Default*), em plena conformidade com a **Lei Geral de Proteção de Dados Pessoais (Lei nº 13.709/2018 - LGPD)** e o **Estatuto da Advocacia e da OAB (Lei nº 8.906/1994)**.

---

## 2. Bases Legais para o Tratamento de Dados Pessoais

O BR-LAWYER opera o tratamento de dados pessoais no âmbito contencioso e consultivo fundamentado nas seguintes hipóteses legais da LGPD:

1. **Exercício Regular de Direitos em Processo Judicial, Administrativo ou Arbitral:**
   - *Art. 7º, VI* (Dados Pessoais Gerais) e *Art. 11, II, "d"* (Dados Pessoais Sensíveis).
   - Fundamenta a guarda de autos, documentos comprobatórios, certidões e peças processuais das partes, litisconsortes e testemunhas.
2. **Execução de Contrato de Prestação de Serviços Advocatícios:**
   - *Art. 7º, V*.
   - Fundamenta o cadastro de clientes, dados de contato, emissão de honorários, recibos e relatórios de prestação de contas.
3. **Cumprimento de Obrigação Legal ou Regulatória:**
   - *Art. 7º, II*.
   - Fundamenta a emissão de notas fiscais (NFS-e), retenções tributárias e comunicações obrigatórias à Receita Federal e órgãos de classe.

---

## 3. Diretrizes de Proteção de Dados e Sigilo Profissional

### 3.1 Minimização de Dados (Data Minimization)
- Coleta estrita dos dados necessários à condução do caso concreto.
- Formulários com campos opcionais para dados secundários.
- Ingestão automática via DataJud e DJEN filtra metadados desnecessários antes da persistência local.

### 3.2 Tratamento de Processos em Segredo de Justiça (CPC Art. 189)
- Flag automática `segredoJustica = true` herdada do DataJud ou marcada pelo operador.
- Restrição de visualização na interface para usuários sem autorização expressa no processo.
- Ofuscação de nomes em telas gerais, relatórios consolidados e logs do sistema.

### 3.3 Anonimização e Integração com Modelos de Linguagem (LLMs / IA)
- **Sanitização Prévia de Dados Pessoais (PII Scrubbing):** Antes de enviar trechos de documentos ou e-mails para provedores externos de IA (OpenAI, Claude, Gemini, Ingo AI local), o sistema executa rotina de mascaramento de CPFs, dados bancários e nomes que não sejam essenciais à análise solicitada.
- **Provider-Agnostic e Suporte a Modelos Locais:** Capacidade de operação 100% on-premises via modelos locais (Ollama, vLLM) para escritórios que lidam com sigilo extremo ou vedação contratual de envio de dados à nuvem.

### 3.4 Direitos dos Titulares de Dados (Data Subject Rights)
O BR-LAWYER fornece ferramentas nativas para atender às requisições de titulares:
- **Direito de Confirmação e Acesso:** Relatório consolidado de todos os registros e processos vinculados ao CPF/CNPJ de um cliente.
- **Direito de Correção:** Interface centralizada de atualização cadastral que sincroniza alterações com os processos ativos.
- **Eliminação e Descarte Controlado:** Rotina de arquivamento definitivo com expurgo seguro de anexos após o término do prazo prescricional legal (CPC art. 205 / Estatuto da OAB).
