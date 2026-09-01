# DIAGNÓSTICO DO AMBIENTE DE COMPILAÇÃO MAVEN E JAVA 17 NO WINDOWS — PROJETO BR-LAWYER

> **Documento de Referência:** `docs/research/BUILD_TOOLCHAIN_REPORT.md`  
> **Especialista:** SUBAGENT BUILD & TOOLCHAIN SPECIALIST  
> **Data:** 31 de Agosto de 2026  
> **Workspace:** `c:\projetos IA\BR-LAWYER\br-lawyer`  
> **Alvo:** Diagnóstico da Toolchain, Resolução de Dependências, Base Modules e Procedimento de Build Reproduzível no Windows  

---

## 1. RESUMO EXECUTIVO (EXECUTIVE SUMMARY)

O projeto **BR-LAWYER** (baseado no upstream *j-lawyer.org* v2.8.0) é estruturado como um **reator Maven unificado único** (single Maven reactor), operando estritamente em **Java 17** (`<maven.compiler.release>17</maven.compiler.release>`). 

### Principais Constatações do Diagnóstico:
1. **Unificação do Reator Maven:** O projeto concluiu a migração de um sistema híbrido Ant/NetBeans (`build.xml`) para Maven puro. O build raiz (`pom.xml`) gerencia 10 módulos ativos no reator padrão, mais 1 módulo web opcional (`-Pweb`).
2. **Repositório Local In-Project (`maven-repo/`):** As dependências de terceiros que não estão disponíveis no Maven Central canônico (coordenadas sintéticas `jlawyer.thirdparty:*`, stubs do WildFly como `jboss-client`, `flyway-core/mysql 11.17.2`, `log4j 2.17.1`, `jcifs`, `jsch`, etc.) são resolvidas a partir de um repositório baseado em arquivos local (`maven-repo/`, ignorado no Git). **Na inicialização de um workspace novo no Windows, o diretório `maven-repo/` não existe e precisa ser populado pelo script de seed antes de qualquer compilação.**
3. **Módulos de Base Auditados:** Os módulos de fundação (`j-lawyer-server-common`, `j-lawyer-server-entities`, `j-lawyer-server-api`, `j-lawyer-io-common` e `j-lawyer-fax`) possuem grafos de dependência bem definidos no reator Maven, com supressão controlada de stubs de API (ex: exclusão de `javax:javaee-api` no Surefire para testes unitários em `server-common`).
4. **Requisitos Críticos do Java 17 no Windows:**
   - O projeto requer o **JDK 17 LTS**.
   - Para o módulo desktop `j-lawyer-client`, o runtime espera módulos JavaFX (fornecidos pelo BellSoft Liberica Full JDK 17 ou OpenJFX). O `j-lawyer-backupmgr` já utiliza `org.openjfx:*:17.0.9`.
   - Flags mandatórias no Windows: codificação UTF-8 explícita (`-Dfile.encoding=UTF-8`), compilação não incremental (`<useIncrementalCompilation>false</useIncrementalCompilation>`) e tratamento de caminhos longos (MAX_PATH).

---

## 2. ARQUITETURA DO REATOR MAVEN E MÓDULOS

O grafo de compilação é inferido automaticamente pelo reator Maven com base nas relações de dependência declaradas:

```
j-lawyer-parent (pom.xml raiz)
├── j-lawyer-server-common
├── j-lawyer-io-common
├── j-lawyer-fax
├── j-lawyer-cloud (shaded)
├── j-lawyer-invoicing (shaded)
├── j-lawyer-server-entities
├── j-lawyer-server-api
├── j-lawyer-server (EAR aggregator)
│    ├── j-lawyer-server-ejb
│    ├── j-lawyer-server-war
│    ├── j-lawyer-server-io
│    ├── j-lawyer-io (REST/Swagger)
│    └── j-lawyer-server-ear
├── j-lawyer-client (Swing Desktop)
└── j-lawyer-backupmgr (JavaFX Fat JAR)
```

### Módulos Declarados no `pom.xml`:
| Módulo | Tipo / Packaging | Responsabilidade |
| :--- | :--- | :--- |
| **`j-lawyer-fax`** | `jar` | Integração VoIP e Fax (Sipgate) |
| **`j-lawyer-server-common`** | `jar` | Utilitários de I/O, VFS, segurança, calendários |
| **`j-lawyer-io-common`** | `jar` | Utilitários comuns de monitoramento e sistema |
| **`j-lawyer-cloud`** | `jar` (shaded fat) | Integração Nextcloud/ownCloud (WebDAV/CalDAV/CardDAV) |
| **`j-lawyer-invoicing`** | `jar` (shaded fat) | Faturamento eletrônico ZUGFeRD / Mustang Project |
| **`j-lawyer-server-entities`** | `jar` | Entidades JPA / persistência (`ArchiveFileBean`, etc.) |
| **`j-lawyer-server-api`** | `jar` | Interfaces remotas EJB (`ServiceNameRemote`) |
| **`j-lawyer-server`** | `pom` (aggregator) | Agrega EJB, WARs, REST e EAR do servidor WildFly |
| **`j-lawyer-server-ear`** | `ear` | Pacote corporativo de implantação WildFly 26.1.3 |
| **`j-lawyer-client`** | `jar` | Cliente desktop Swing com Matisse Form (.form) |
| **`j-lawyer-backupmgr`** | `jar` (shaded fat) | Utilitário standalone de backup/restore JavaFX |
| **`j-lawyer-web`** | `war` (profile `-Pweb`) | Interface Web Angular standalone (opcional) |

---

## 3. MECANISMO DE RESOLUÇÃO DO REPOSITÓRIO LOCAL (`maven-repo/`)

### 3.1 Declaração no `pom.xml` Raiz
```xml
<repositories>
    <repository>
        <id>jlawyer-local</id>
        <name>j-lawyer in-project repository</name>
        <url>file://${maven.multiModuleProjectDirectory}/maven-repo</url>
        <releases><enabled>true</enabled></releases>
        <snapshots><enabled>false</enabled></snapshots>
    </repository>
</repositories>
```

### 3.2 O Processo de Semeadura (Seeding)
O script `scripts/seed-maven-repo.sh` analisa todos os JARs residuais presentes nas pastas `lib/` e `libs/` dos módulos, instalando-os no repositório baseado em arquivo `maven-repo/` sob o groupId sintético `jlawyer.thirdparty`:
- `groupId`: `jlawyer.thirdparty`
- `artifactId`: nome do arquivo anterior ao primeiro hífen numérico (ex: `log4j-api`)
- `version`: sufixo numérico (ex: `2.17.1`), ou `0.0.0` se não versionado
- Um script Python embutido substitui os POMs gerados pelo `maven-install-plugin` por stubs limpos, evitando que dependências transitivas errôneas ou pais legados poluam o classpath.

---

## 4. DIAGNÓSTICO DETALHADO DOS MÓDULOS DE BASE

### 4.1 `j-lawyer-server-common`
- **Função:** Biblioteca folha que provê abstrações de sistema de arquivos virtual (`VirtualFile`), conexões JCIFS (Samba), JSch (SSH/SFTP), Commons Net (FTP) e utilitários criptográficos.
- **Isolamento de Teste Surefire:**
  ```xml
  <classpathDependencyExcludes>
      <classpathDependencyExclude>javax:javaee-api</classpathDependencyExclude>
  </classpathDependencyExcludes>
  ```
  *Motivo:* O `javax:javaee-api` contém stubs sem código de método (`Absent Code attribute`). A exclusão no teste garante que o `jakarta.mail` real do Sun Mail seja carregado durante os testes unitários.

### 4.2 `j-lawyer-server-entities`
- **Função:** Modelo de dados JPA / Hibernate 5.3.28.
- **Processamento de Anotações:** O processamento de metamodelo estático (`Entity_`) foi desativado deliberadamente, pois a classe `AbstractFacade` opera com `CriteriaQuery` dinâmico. Isso acelera a compilação e elimina avisos de geração de código.
- **Dependências Chave:** `jakarta.persistence-api` (provided), `jakarta.validation-api` (provided), `flyway-core`, `flyway-mysql`, `hibernate-core`.

### 4.3 `j-lawyer-server-api`
- **Função:** Contratos de interface remota EJB 3.x (`ArchiveFileServiceRemote`, `SecurityServiceRemote`, etc.) compartilhados entre o WildFly Server e o cliente desktop `j-lawyer-client`.
- **Dependências:** `j-lawyer-server-entities`, `j-lawyer-server-common`, `j-lawyer-fax`, `jboss-client` (provided).

### 4.4 Módulos Shaded (`j-lawyer-cloud` e `j-lawyer-invoicing`)
- Ambos usam o `maven-shade-plugin` com realocação estrita de pacotes para evitar conflitos de classpath:
  - `j-lawyer-cloud`: realoca `org.apache.http` para `shaded.org.apache.http` e `com.fasterxml.jackson` para `shaded.com.fasterxml.jackson` (evita conflitos com o JBoss Remoting/WildFly client).
  - `j-lawyer-invoicing`: realoca `org.apache.pdfbox` para `shaded.org.apache.pdfbox` (evita colisão entre o Apache PDFBox 3.x exigido pelo Mustang/ZUGFeRD e o PDFBox 2.0.24 do cliente).

---

## 5. MAPEAMENTO DE FLAGS E COMPORTAMENTOS ESPECÍFICOS NO WINDOWS

| Categoria | Comportamento no Windows | Configuração / Mitigação Recomendada |
| :--- | :--- | :--- |
| **Encoding UTF-8** | O Windows adota por padrão CP1252 / Windows-1252 em shells nativos, corrompendo acentuação e caracteres especiais em fontes Java e templates SQL. | O `pom.xml` define `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`. Adicionalmente, definir a variável de ambiente `JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"`. |
| **Espaços nos Caminhos** | O workspace `c:\projetos IA\BR-LAWYER\br-lawyer` contém espaço (`projetos IA`). | A propriedade `${maven.multiModuleProjectDirectory}` no POM raiz lida com isso, mas todos os scripts `.sh` e `.ps1` devem envolver caminhos entre aspas (`"$ROOT"`, `"$REPO"`). |
| **Limite MAX_PATH (260 chars)** | Árvores profundas de empacotamento (`target/j-lawyer-server-ear-...`) podem exceder 260 caracteres no NTFS. | Habilitar caminhos longos no Git (`git config --system core.longpaths true`) e no Registro do Windows (`LongPathsEnabled = 1`). |
| **Compilação Não-Incremental** | Recompilações incrementais no Windows/NetBeans podem reter classes obsoletas ou métodos sintéticos duplicados de lambdas. | Configuração ativa no POM raiz: `<useIncrementalCompilation>false</useIncrementalCompilation>`. |
| **Line Endings (CRLF vs LF)** | Scripts `.sh` convertidos para CRLF falham com `\r\n: command not found` no Git Bash/WSL. | Manter `.gitattributes` com `*.sh text eol=lf` e `core.autocrlf=input`. |

---

## 6. GUIA DE COMPILAÇÃO REPRODUZÍVEL (ENGENHARIA)

### 6.1 Pré-Requisitos de Ambiente
1. **JDK 17 LTS:** BellSoft Liberica Full JDK 17 (recomendado por embutir JavaFX) ou Eclipse Temurin OpenJDK 17.
2. **Apache Maven:** Versão 3.8.6 ou 3.9.x.
3. **Git for Windows & Python 3** (para execução de scripts de suporte).

### 6.2 Configuração de Variáveis de Ambiente (Windows PowerShell)
```powershell
$env:JAVA_HOME = "C:\Program Files\BellSoft\LibericaJDK-17-Full"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
```

### 6.3 Passo 1: Semeadura do Repositório Local (`maven-repo/`)
Execute no Git Bash na raiz do projeto:
```bash
./scripts/seed-maven-repo.sh
```

### 6.4 Passo 2: Teste de Compilação Isolada dos Módulos de Base
```bash
# Compilar e instalar os utilitários comuns
mvn -pl j-lawyer-server-common clean install

# Compilar e instalar as entidades JPA
mvn -pl j-lawyer-server-entities clean install

# Compilar e instalar as interfaces de API
mvn -pl j-lawyer-server-api clean install
```

### 6.5 Passo 3: Build Rápido do Reator Completo (Fast Build)
```bash
mvn clean install -DskipTests
```

### 6.6 Passo 4: Build Completo com Testes Unitários
```bash
mvn clean install
```

### 6.7 Localização dos Artefatos Gerados
- **Servidor EAR (WildFly):** `j-lawyer-server/j-lawyer-server-ear/target/j-lawyer-server.ear`
- **Cliente Desktop (Swing):** `j-lawyer-client/target/j-lawyer-client.jar` (e pasta de dependências `j-lawyer-client/target/lib/`)
- **Backup Manager (JavaFX):** `j-lawyer-backupmgr/target/j-lawyer-backupmgr.jar`
- **Especificação Swagger OpenAPI:** `j-lawyer-server/j-lawyer-io/target/swagger-final/swagger.json`
