# BR-LAWYER — Modern Web Client Architecture

> **Document Version:** 1.0.0 (Architecture Spike)  
> **Target Branch:** `spike/modern-web-ui`  
> **Author:** BR-LAWYER Engineering Architecture Team  
> **Status:** Approved Architecture Spike Specification  

---

## 1. Executive Summary & Core Principles

The objective of this architectural spike is to establish the foundation for a modern, high-density, web-based legal practice management client for **BR-LAWYER**, directly interfacing with the existing enterprise backend (**WildFly Application Server, EJB 3.x, MariaDB, Lucene Search, StirlingPDF/Tika preview engines**).

### 1.1 Non-Negotiable Backend Preservation Constraints
1. **Zero Secondary Backends:** Strict prohibition of intermediate Node.js, Express, Next.js API routes, Supabase, Firebase, or parallel microservice backends. The web client communicates directly via REST with the WildFly backend (`/j-lawyer-io/rest/`).
2. **Preservation of Core Systems:** WildFly EAR/WAR deployments, EJB remote/local beans, JPA entity managers, database schemas, and business rules remain the authoritative source of truth.
3. **Desktop Swing Parity & Independence:** The desktop Swing client (`j-lawyer-client`) remains fully functional and unmodified, sharing the same EJB layer and database without contract disruption.
4. **Security & RBAC Enforcement:** Authoritative authorization resides strictly on the server (WildFly Elytron security domain + EJB `@RolesAllowed`). The frontend reflects permissions for UX purposes but never bypasses server checks.

---

## 2. Technical Stack Selection & Justification

| Component | Selected Technology | Technical Justification |
| :--- | :--- | :--- |
| **Language** | **TypeScript 5.x** | Strict typing across DTOs, interfaces, and API contracts directly aligned with Java backend POJOs (`RestfulCaseOverviewV8`, `RestfulPartyV1`, etc.). |
| **Framework** | **React 19** | Industry standard, component-based, lightweight, highly performant virtual DOM, unopinionated, long-term stability with no framework lock-in. |
| **Build & Tooling** | **Vite 6** | Instant HMR (Hot Module Replacement), ultra-fast ESBuild bundling, native TypeScript support, lightweight configuration, built-in development reverse proxy. |
| **Styling & Design** | **Tailwind CSS 3.4** | Utility-first CSS providing granular control over spacing, typography, and density; zero runtime overhead; built-in dark/light mode engine. |
| **Icons & Primitives** | **Lucide Icons** | Clean, accessible, lightweight SVG icon suite tailored for modern enterprise applications. |
| **State Management** | **TanStack Query (React Query) + Zustand** | **TanStack Query** manages server state (caching, background invalidation, optimistic updates, pagination); **Zustand** manages client-only UI state (theme, sidebar collapse, active drawers, modals) with minimal boilerplate. |
| **HTTP Client** | **Axios with Interceptors** | Full lifecycle interceptors for Bearer token injection, automatic silent refresh on `401 Unauthorized`, response envelope unwrapping, and structured error handling. |

### Why not Heavy Enterprise UI Frameworks?
Heavy component libraries introduce massive bundle sizes, rigid styling abstractions, and hard-to-customize DOM structures. The **BR-LAWYER MINERAL** design requires precise line heights (32-36px tabular rows), custom keyboard shortcuts, split-pane inspectors, and editorial legal typography that are vastly easier and cleaner to build with unstyled accessible primitives and Tailwind CSS.

---

## 3. Design System: BR-LAWYER MINERAL

**BR-LAWYER MINERAL** is an ergonomics-first design system tailored for high-volume legal professionals (attorneys, paralegals, legal assistants). It draws inspiration from the user's conceptual ATRIUM framework, delivering high information density, editorial clarity, and rapid keyboard navigation.

### 3.1 Visual & Ergonomic Foundations

```
+----------------------------------------------------------------------------------------------------+
|  BR-LAWYER  [ Global Search / Command Palette  Ctrl+K ]                   [Theme] [User: admin]   |
+-----------+-------------------------------------------------------------+--------------------------+
|  SIDEBAR  |  MAIN CONTENT VIEW (Dense Table / Dashboard / Detail)       |  INSPECTOR LATERAL       |
|           |                                                             |  (Right Drawer)          |
|  [Cockpit]|  CNJ Process Number       Subject      Client       Status  |  ----------------------- |
|  [Cases]  |  5001234-56.2026.8.13.0024 Cível        Silva        URGENTE |  Process: 5001234-56...  |
|  [Agenda] |  0019876-12.2026.5.03.0001 Trabalhista  Banco S/A    PRAZO   |  Parties: Silva x Banco  |
|  [Docs]   |  1045678-90.2026.4.01.3800 Tributário   Tech Ltda    TRATADA |  [Tabs: Summary|Docs]    |
|  [Config] |                                                             |  Quick action buttons    |
+-----------+-------------------------------------------------------------+--------------------------+
```

1. **Color Palette (Mineral Theme):**
   - **Dark Slate / Zinc Base:** Slate-950 (`#020617`), Slate-900 (`#0f172a`), Slate-800 (`#1e293b`), Slate-700 (`#334155`).
   - **Light Mode Base:** White (`#ffffff`), Slate-50 (`#f8fafc`), Slate-100 (`#f1f5f9`), Slate-200 (`#e2e8f0`).
   - **Semantic High-Contrast Accents:**
     - **Fatal / Urgent / Error:** Crimson / Coral (`#ef4444` / `#dc2626`).
     - **Warning / Respite (D-3 to D-1):** Amber (`#f59e0b` / `#d97706`).
     - **Active / In Progress:** Indigo / Cobalt (`#6366f1` / `#4f46e5`).
     - **Success / Completed:** Emerald (`#10b981` / `#059669`).
     - **Archived / Neutral:** Slate-400 (`#94a3b8`).
2. **Dense Tabular Layouts:**
   - Row heights optimized at **34px** to allow 20–30 records per viewport without excessive scrolling.
   - CNJ process numbers formatted in mono typography (`font-mono tracking-tight font-medium`) for immediate scannability.
3. **Inspector Lateral (Right Drawer):**
   - Eliminates "pogo-sticking" (navigating back and forth between list and detail).
   - Allows instant inline preview of case metadata, recent documents, and parties when selecting a row with `↑` / `↓` keyboard keys.
4. **Command Palette (`Ctrl+K` / `Cmd+K`):**
   - Instant search modal supporting Lucene fulltext search over cases, contacts, documents, and navigation commands.

---

## 4. Authentication & Session Architecture

Authentication leverages the dedicated browser-friendly endpoints implemented in `AuthenticationEndpointV8`:

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Browser
    participant Client as Web Client (React)
    participant AuthAPI as WildFly (/v8/auth)
    participant SecService as SecurityService (EJB)
    participant ProtectedAPI as WildFly (/v8/cases, etc.)

    User->>Client: Enters credentials (admin / a)
    Client->>AuthAPI: POST /v8/auth/login { username, password }
    AuthAPI->>SecService: authenticateAndGetRoles(username, password)
    SecService-->>AuthAPI: List<String> roles (e.g. [readArchiveFileRole, adminRole])
    AuthAPI-->>Client: 200 OK { accessToken, expiresIn: 900, principal, roles }<br/>Set-Cookie: JLAWYER_REFRESH=<token>; HttpOnly; Path=/j-lawyer-io/rest/v8/auth; SameSite=Lax
    Note over Client: Stores accessToken in memory (Zustand state)
    
    Client->>ProtectedAPI: GET /v8/cases/page (Header: Authorization: Bearer <accessToken>)
    ProtectedAPI-->>Client: 200 OK (RestfulCasePageV8)

    Note over Client,AuthAPI: Token Expired (15 min) -> 401 Unauthorized
    Client->>AuthAPI: POST /v8/auth/refresh (Cookie JLAWYER_REFRESH sent automatically)
    AuthAPI-->>Client: 200 OK { newAccessToken, expiresIn: 900 } + Rotated Cookie
    Client->>ProtectedAPI: Retries original request with new token
```

### 4.1 Security Properties
- **Access Token:** Short-lived (15 minutes), RS256 signed JWT containing `sub` (principal username), `roles` (WildFly security roles), `aud: "j-lawyer-web"`, `iss: "j-lawyer"`. Stored **only in JavaScript memory** (never in `localStorage` to prevent XSS exfiltration).
- **Refresh Token:** Long-lived (8 hours), RS256 signed JWT delivered via an **`HttpOnly`**, **`SameSite=Lax`** cookie scoped strictly to `Path=/j-lawyer-io/rest/v8/auth`. Inaccessible to JavaScript.
- **Silent Refresh Interceptor:** Axios response interceptor catches `401 Unauthorized`, pauses queued requests, invokes `/v8/auth/refresh`, updates in-memory token, and replays failed requests transparently.
- **Logout:** Invokes `POST /v8/auth/logout`, clearing the server refresh cookie (Max-Age=0) and purging client memory state.

---

## 5. Comprehensive REST API Audit & Inventory

The BR-LAWYER backend exposes over 120 RESTful operations across versions v1 to v8 under the base path `/j-lawyer-io/rest/`.

### 5.1 Evaluated REST Endpoints

| Resource | Version | HTTP Method & Path | Description | Roles Allowed |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | **v8** | `POST /v8/auth/login` | Login, issues JWT + refresh cookie | `@PermitAll` |
| **Auth** | **v8** | `POST /v8/auth/refresh` | Refresh JWT via cookie rotation | `@PermitAll` |
| **Auth** | **v8** | `POST /v8/auth/logout` | Clears refresh cookie | `@PermitAll` |
| **Cases** | **v8** | `GET /v8/cases/page` | Server-paginated cases overview (q, filter, offset, limit) | `readArchiveFileRole` |
| **Cases** | **v8** | `GET /v8/cases/list` | List all cases (v8 rich overview) | `readArchiveFileRole` |
| **Cases** | **v8** | `GET /v8/cases/list/active`| List active cases (v8 rich overview) | `readArchiveFileRole` |
| **Cases** | **v1** | `GET /v1/cases/{id}` | Full case metadata (name, fileNumber, lawyer, etc.) | `readArchiveFileRole` |
| **Cases** | **v1** | `GET /v1/cases/{id}/parties`| Involvements/parties (authors, defendants, lawyers) | `readArchiveFileRole` |
| **Cases** | **v1** | `GET /v1/cases/{id}/documents/with-tags` | Case documents with associated tag labels | `readArchiveFileRole` |
| **Cases** | **v8** | `GET /v8/cases/document/{id}/preview-pdf` | Base64 PDF / Text rendering for in-browser preview | `readArchiveFileRole` |
| **Cases** | **v1** | `GET /v1/cases/document/{id}/content` | Raw document bytes (Base64) for download | `readArchiveFileRole` |
| **Cases** | **v8** | `PUT /v8/cases/document/{id}/content` | Upload / update document content (Base64) | `writeArchiveFileRole` |
| **Cases** | **v1** | `GET /v1/cases/{id}/duedates` | Case due dates, respites, follow-ups, events | `readArchiveFileRole` |
| **Cases** | **v8** | `GET /v8/cases/{id}/history` | Chronological audit trail / history of changes | `readArchiveFileRole` |
| **Cases** | **v1** | `GET /v1/cases/{id}/tags` | Case tags | `readArchiveFileRole` |
| **Cases** | **v8** | `GET /v8/cases/document/{id}/eml` | Structured MIME preview for `.eml` emails | `readArchiveFileRole` |
| **Cases** | **v8** | `GET /v8/cases/document/{id}/bea` | Structured XML preview for `.bea` messages | `readArchiveFileRole` |
| **Contacts**| **v8** | `GET /v8/contacts/page` | Server-paginated contacts overview | `readArchiveFileRole` |
| **Contacts**| **v1/v2**| `GET /v1/contacts/{id}` | Detailed contact data (address, phones, emails) | `readArchiveFileRole` |
| **Calendar**| **v8** | `GET /v8/calendar/events` | Calendar events/deadlines by date range (`from`, `to`) | `readArchiveFileRole` |
| **Calendar**| **v8** | `DELETE /v8/calendar/events/{id}` | Remove calendar event by ID | `writeArchiveFileRole` |
| **Search** | **v8** | `GET /v8/search/fulltext` | Lucene global fulltext search over cases/docs | `readArchiveFileRole` |
| **Search** | **v8** | `POST /v8/search/reindex` | Trigger async index rebuild | `adminRole` |
| **Profile**| **v8** | `GET /v8/profile` | Caller's profile, settings, member groups | `loginRole` |
| **Profile**| **v8** | `GET /v8/profile/dashboard`| User's customized web dashboard configuration | `loginRole` |
| **Profile**| **v8** | `PUT /v8/profile/dashboard`| Update user's web dashboard configuration | `loginRole` |
| **Assistant**| **v8**| `GET /v8/assistant/status` | Live status of Ingo AI assistant connections | `loginRole` |
| **Office** | **v8** | `GET /v8/office/settings` | Collabora/WOPI/StirlingPDF settings | `loginRole` |

---

## 6. Document Preview & Content Pipeline

Documents in BR-LAWYER span multiple formats (`.pdf`, `.docx`, `.odt`, `.xlsx`, `.eml`, `.msg`, `.bea`, `.png`, `.jpg`). The web client handles them seamlessly:

```mermaid
graph LR
    DocReq[Client requests Document Preview] --> DocEndpoint[GET /v8/cases/document/{id}/preview-pdf]
    DocEndpoint --> CachedPDF{Cached PDF exists? StirlingPDF}
    CachedPDF -->|Yes| RetPDF[Return Base64 PDF]
    CachedPDF -->|No| TikaTxt{Tika Text Available?}
    TikaTxt -->|Yes| RetTxt[Return Clean Text Content]
    TikaTxt -->|No| Fallback[Return Empty Preview + Download Link]
    
    RetPDF --> WebPDFViewer[Client renders inline via PDF.js / iframe object]
    RetTxt --> WebTxtViewer[Client renders formatted markdown/text viewer]
```

1. **PDF Rendering:** Delivered via `base64content` payload, rendered directly in the browser via an integrated PDF canvas viewer.
2. **Email Preview (`.eml` / `.msg`):** Parsed MIME structures returned by `/v8/cases/document/{id}/eml` containing `subject`, `from`, `to`, `cc`, `htmlBody`, and `attachments`.
3. **Download / Local Edit / Re-upload:**
   - Download document: `GET /v1/cases/document/{id}/content` -> Blob download.
   - Upload modified document: `PUT /v8/cases/document/{id}/content` `{ base64content }` -> creates audit history entry on server.

---

## 7. Role-Based Access Control (RBAC) Mapping

The web client maps the WildFly Elytron roles received upon login:

| Role Name | Scope in Web Client | UI Behavior |
| :--- | :--- | :--- |
| `loginRole` | Authenticated user base | Access to dashboard, personal profile, settings. |
| `readArchiveFileRole` | Read access to cases, contacts, documents | Displays cases list, case detail, documents, duedates. |
| `writeArchiveFileRole`| Modification rights | Enables editing case fields, uploading document content, deleting events. |
| `createArchiveFileRole`| Creation rights | Enables "Novo Processo", "Nova Tarefa", "Novo Contato" buttons. |
| `adminRole` | System administration | Enables administration panel, AI config, fulltext reindexing. |

---

## 8. API Gaps & Missing Endpoints Analysis

While the existing REST APIs v1–v8 fully support the core modules (Cases, Contacts, Documents, Deadlines, Search, Profile), the following gaps must be addressed in subsequent iterations:

### 8.1 Critical Gaps for Phase 2 & Beyond

| Area | Missing Capability / Endpoint | Recommended Solution |
| :--- | :--- | :--- |
| **Brazilian Publicações** | Endpoints for `/v8/publications` (list, filter by court/DJEN, homologate AI suggested deadlines). | Add `PublicationsEndpointV8` in `j-lawyer-io` mapping to `PublicationBean` EJB. |
| **Legal Tasks (Tarefas)**| Endpoints for `/v8/tasks/kanban` and `/v8/tasks/{id}/status`. | Add `TasksEndpointV8` mapping to `LegalTaskBean` EJB. |
| **Push / Realtime Notifications** | WebSocket or Server-Sent Events (SSE) for instant messaging and fatal deadline alerts. | Add Undertow SSE/WebSocket handler at `/j-lawyer-io/events`. |
| **Calendar Setup & Templates**| Dedicated endpoint for CalDAV setups and appointment templates. | Extend `CalendarEndpointV8`. |
| **Invoicing / Boletos** | Brazilian Boletos/Pix generation & financial integration. | Add `BrazilianFinanceEndpointV8`. |

---

## 9. Swing to Web Client Migration Strategy

To transition smoothly without disrupting daily law office operations, a 4-phase staged adoption roadmap is recommended:

```
[ Phase 1: Architecture Spike & Core Read-Only Access ] (CURRENT)
  ├── Auth JWT + HttpOnly refresh
  ├── Dashboard Cockpit
  ├── Cases List & Detail (Parties, Documents, History, Deadlines)
  └── Global Search (Ctrl+K)
        │
        ▼
[ Phase 2: Core Read-Write & Document Management ]
  ├── Create & Edit Cases / Contacts
  ├── Document Upload / Drag-and-Drop / Tagging
  ├── Calendar Event & Deadline CRUD
  └── Timesheets & Time Tracking
        │
        ▼
[ Phase 3: Brazilian Practice Workflows & Ingo AI ]
  ├── Publicações & Intimações DJEN / DJe
  ├── Human-in-the-loop AI Deadline Homologation
  ├── Legal Tasks Kanban & Checklists
  └── Ingo AI Chat & Document Extraction
        │
        ▼
[ Phase 4: Full Parity & Sunset of Desktop Swing ]
  ├── Invoicing, Financial Accounts, Boletos / Pix
  ├── Email Client & Court Portals Integration
  ├── System Administration & Backup Management
  └── Complete transition to Modern Web Client
```

---

## 10. Objective Evaluation: GO WITH CHANGES

### Verdict: **GO WITH CHANGES**

#### Justification:
- **GO:** The existing WildFly/EJB REST API (especially v8 and v1/v7) already provides over 80% of the foundational endpoints required for a high-performance modern web client (Auth, Paginated Cases, Case Details, Parties, Documents + Previews, Due Dates, History, Search). The architecture is completely feasible without changing the backend database or business logic.
- **CHANGES NEEDED:** 
  1. Add additive endpoints for Brazilian Publicações and Legal Tasks (`/v8/publications`, `/v8/tasks`).
  2. Implement an SSE/WebSocket notification bridge for real-time alerts.
  3. Package the modern web client as an automated Vite build artifact deployed as a WAR alongside the WildFly EAR.