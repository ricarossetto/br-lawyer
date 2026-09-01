# Modelo de Segurança e Controle de Acesso — BR-LAWYER

> **Documento de Especificação de Segurança:** `docs/SECURITY_MODEL.md`  
> **Versão:** 1.0.0  
> **Status:** Aprovado para Arquitetura

---

## 1. Princípios Fundamentais de Segurança (Security-by-Design)

O **BR-LAWYER** adota uma postura rigorosa de segurança defensiva projetada para proteger informações jurídicas e sigilosas de escritórios de advocacia e seus clientes.

Os pilares fundamentais são:
1. **Backend como Única Fonte da Verdade (Server-Side Enforcement):**
   Toda validação de identidade, papéis, permissões de acesso, integridade de transações e transições de estado é estritamente executada no backend (WildFly / EJBs / REST Services). O frontend (Desktop Swing ou Web UI) é tratado como canal de apresentação não confiável para fins de autorização.
2. **Princípio do Menor Privilégio (Least Privilege):**
   Usuários e serviços operam com o conjunto estritamente mínimo de permissões necessárias para o cumprimento de suas atribuições.
3. **Defesa em Profundidade (Defense-in-Depth):**
   Múltiplas camadas de proteção: autenticação robusta, autorização granular RBAC, validação e sanitização de entrada, isolamento de dados no armazenamento e auditoria imutável.
4. **Segregação de Ambientes e Multi-Tenancy Seguro:**
   Isolamento de dados por escritório/banca e proteção contra vazamento entre processos com segredo de justiça.

---

## 2. Controle de Acesso Baseado em Papéis (RBAC)

### 2.1 Matriz de Papéis e Prerrogativas

| Papel (Role) | Descrição e Escopo | Acesso a Processos | Gestão de Documentos | Ações Financeiras | Configurações de Sistema |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`ADMIN`** | Administrador do Sistema / TI | Todos (exceto restrição explícita de sigilo) | Completo (inclui auditoria) | Completo | Gestão de usuários, backups, integrações, logs |
| **`SOCIO`** / **`PARTNER`** | Sócio / Gestor Jurídico | Todos os processos do escritório | Leitura, edição, exclusão lógica, aprovação | Relatórios consolidados, aprovação de despesas | Visualização de configurações e relatórios |
| **`ADVOGADO`** / **`LAWYER`** | Advogado Associado / Responsável | Processos atribuídos e públicos da banca | Criação, edição e visualização | Lançamento de honorários e despesas no processo | Consulta básica |
| **`ASSISTENTE`** / **`PARALEGAL`**| Assistente Jurídico / Estagiário | Processos atribuídos pela equipe | Leitura e inserção de minutas/anexos | Lançamento de despesas e custas | Sem acesso |
| **`FINANCEIRO`** / **`BILLING`** | Gestor Financeiro / Faturamento | Acesso a metadados financeiros do caso | Documentos fiscais e comprovantes | Emissão de cobranças, NFS-e, conciliação bancária | Sem acesso |
| **`AUDITOR`** / **`DPO`** | Auditor de Compliance / LGPD | Leitura de logs de auditoria e trilhas | Sem acesso ao conteúdo sigiloso | Leitura de logs | Relatórios de auditoria e acesso a PII |

---

## 3. Mecanismos de Autenticação e Gestão de Sessão

1. **Armazenamento Seguro de Senhas:**
   - Funções de derivação de chave com alto custo computacional: **Argon2id** (ou PBKDF2 com HMAC-SHA512 e mínimo de 210.000 iterações).
   - Salt criptograficamente seguro gerado por usuário via `SecureRandom` (mínimo 128 bits).
   - Bloqueio de senhas fracas e histórico de não repetição.

2. **Autenticação em Dois Fatores (MFA / 2FA):**
   - Suporte a **TOTP (Time-based One-Time Password)** conforme RFC 6238 (Google Authenticator, Microsoft Authenticator, FreeOTP).
   - Chave de recuperação de emergência com hashes armazenados separadamente.

3. **Segurança de Sessão e Transporte:**
   - Obrigatoriedade de **TLS 1.3 / 1.2** com cipher suites modernas (desativação de TLS 1.0/1.1 e ciphers fracos).
   - Desktop Client: Conexão segura via JBoss Remoting encapsulado em TLS (`remote+https://`).
   - REST API / Web: Autenticação via Tokens seguros / HTTP Basic sobre TLS com expiração e rotação periódica.
   - Proteção contra Brute-Force: Rate limiting e bloqueio progressivo por IP e conta de usuário após tentativas falhas.

---

## 4. Segurança na Gestão Documental e Arquivos

1. **Prevenção de Path Traversal:**
   - Acesso a arquivos gerenciado exclusivamente através da camada de abstração `VirtualFile`.
   - Sanitização de nomes de arquivos e bloqueio de caracteres especiais (`../`, `..\\`, `NUL`, etc.).
   - Armazenamento em disco baseado em UUIDs com metadados e nomes originais persistidos no banco de dados relacional.

2. **Validação Rigorosa de Upload:**
   - Verificação do tipo de conteúdo por Magic Bytes (inspeção binária real, e não apenas extensão de arquivo).
   - Lista branca estrita de formatos aceitos (PDF, DOCX, ODT, PNG, JPEG, TIFF, EML, MSG).
   - Limite configurável de tamanho por documento e por requisição (máx. 100MB por padrão).

3. **Criptografia em Repouso (Encryption at Rest):**
   - Documentos sensíveis e backups cifrados com **AES-256-GCM** (OpenSpec `add-document-encryption-at-rest`).
   - Gerenciamento seguro de chaves de criptografia segregadas do volume de armazenamento de dados.

---

## 5. Trilha de Auditoria Imutável (Audit Trail)

Todas as ações críticas são registradas em log de auditoria estruturado com timestamps UTC, endereço IP, identificador do usuário e detalhe da ação:
- Tentativas de autenticação (sucessos e falhas).
- Visualização e download de processos sob Segredo de Justiça.
- Exclusão ou substituição de peças processuais e documentos.
- Alterações em lançamentos financeiros, contas bancárias e cobranças.
- Homologação de prazos e tarefas sugeridos por assistentes de IA.
- Operações de importação judicial (DJEN/DataJud) e tomada de ciência formal.
- Exportação de bases de dados ou relatórios massivos de clientes.
