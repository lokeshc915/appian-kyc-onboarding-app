# KYC Onboarding (Appian → Spring Boot + Angular) — No BPM Engine

This project recreates the Appian onboarding **process model** using **plain Spring Boot services** (state machine + SLA timers)
and an **Angular UI** for Step-1 (Account Details) + Step-2 (Case Documents).

- **DB:** MySQL  
- **File storage:** Local filesystem  
- **Security:** JWT (username/password) **and** OAuth2 login (issues JWT on success)

---

## 1) Architecture Overview

```mermaid
flowchart LR
  subgraph UI[Angular SPA]
    A1[Login / OAuth2 Login]
    A2[Dashboard / Case List]
    A3[Step 1: Account Details Form]
    A4[Step 2: Documents Grid + Upload]
    A5[Audit Timeline]
    A6[Admin Dashboard]
  end

  subgraph API[Spring Boot API]
    S1[Auth Module\nJWT + OAuth2]
    S2[OnboardingCase Service\nState Machine]
    S3[Document Service\nLocal File Storage]
    S4[SLA Scheduler\nDue Date + Breach]
    S5[Audit Service\nTimeline Events]
    S6[Admin Reporting]
  end

  subgraph DATA[Data Stores]
    D1[(MySQL)]
    D2[(Local FS)]
  end

  UI -->|HTTPS REST + Bearer JWT| API
  S1 --> D1
  S2 --> D1
  S3 --> D1
  S3 --> D2
  S4 --> D1
  S5 --> D1
  S6 --> D1
```

---

## 2) Component Diagram

```mermaid
flowchart TB
  C[Angular Components] -->|HTTP| G[API Gateway (Spring Boot)]
  G --> AC[AuthController]
  G --> OC[OnboardingCaseController]
  G --> DC[DocumentController]
  G --> AD[AdminController]

  AC --> AS[AuthService]
  OC --> CS[OnboardingCaseService]
  DC --> DS[DocumentService]
  DS --> FS[FileStorageService]
  AD --> RS[ReportingService]

  CS --> AR[(OnboardingCaseRepository)]
  DS --> DR[(DocumentRecordRepository)]
  RS --> AR
  RS --> DR

  CS --> AUD[AuditService]
  DS --> AUD
  AUD --> AUR[(AuditEventRepository)]

  SLA[SlaScheduler] --> CS
```

---

## 3) Process Flow (Appian Process Model → Spring Services)

### Statuses (state machine)
- `DRAFT_STEP1` → (save Step 1) stays `DRAFT_STEP1`
- `DRAFT_STEP1` → (Next/Submit Step 1) → `DRAFT_STEP2`
- `DRAFT_STEP2` → (Upload docs / add rows) stays `DRAFT_STEP2`
- `DRAFT_STEP2` → (Submit Case) → `SUBMITTED`
- `SUBMITTED` → (Start Review) → `IN_REVIEW`
- `IN_REVIEW` → (Approve) → `APPROVED`
- `IN_REVIEW` → (Reject) → `REJECTED`

```mermaid
stateDiagram-v2
  [*] --> DRAFT_STEP1
  DRAFT_STEP1 --> DRAFT_STEP1: Save Step1
  DRAFT_STEP1 --> DRAFT_STEP2: Next (go to docs step)
  DRAFT_STEP2 --> DRAFT_STEP2: Upload/Add Row
  DRAFT_STEP2 --> SUBMITTED: Submit
  SUBMITTED --> IN_REVIEW: Start Review
  IN_REVIEW --> APPROVED: Approve
  IN_REVIEW --> REJECTED: Reject
```

---

## 4) SLA / Due Date Logic (Appian Timers Equivalent)

**Rule:** every case has `dueAt` and `slaState`:
- `ON_TRACK` (default)
- `BREACHED` if `now > dueAt` and case not in terminal state

Scheduler runs periodically:
1. finds cases where `dueAt < now` and `slaState=ON_TRACK`
2. marks them `BREACHED`
3. writes an `AUDIT` event (“SLA breached”)

```mermaid
sequenceDiagram
  participant SCH as SlaScheduler
  participant DB as MySQL
  participant AUD as AuditService
  SCH->>DB: Query overdue cases (dueAt < now AND slaState=ON_TRACK)
  DB-->>SCH: case list
  loop each case
    SCH->>DB: Update slaState=BREACHED, slaBreachedAt=now
    SCH->>AUD: Append "SLA breached" audit event
    AUD->>DB: Insert audit_event
  end
```

---

## 5) UI Layer Flow (Authentication + Validations + Step Screens)

```mermaid
sequenceDiagram
  participant U as User
  participant UI as Angular UI
  participant API as Spring Boot API
  participant DB as MySQL
  U->>UI: Login (username/password)
  UI->>API: POST /api/auth/login (credentials)
  API->>DB: Validate user + password hash
  DB-->>API: OK
  API-->>UI: { token: JWT }
  UI->>UI: Store token (memory/localStorage) + set auth state

  U->>UI: Create New Case
  UI->>API: POST /api/cases (Bearer JWT)
  API->>DB: Insert onboarding_case (status=DRAFT_STEP1)
  API-->>UI: { caseId }

  U->>UI: Step 1: enter account details
  UI->>UI: Client validation (required fields)
  UI->>API: PUT /api/cases/{id}/step1 (validated payload)
  API->>API: Server validation (@Valid)
  API->>DB: Persist accountDetails + audit event
  API-->>UI: Updated case DTO

  U->>UI: Next (go to documents)
  UI->>API: PUT /api/cases/{id}/submit (moves to DRAFT_STEP2 / SUBMITTED depending on config)
  API->>DB: Update status + dueAt + audit event
  API-->>UI: Updated case DTO

  U->>UI: Upload documents (Add Row)
  UI->>API: POST /api/cases/{id}/documents (multipart/form-data)
  API->>DB: Insert document_record
  API->>API: Save file to local filesystem
  API-->>UI: { id: docId }
```

### Validations
- **Angular (client-side):** required fields, simple formats, disabling buttons until valid
- **Spring Boot (server-side):** `@Valid` DTO validation + role-based guards per endpoint

---

## 6) API Endpoints

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`

### Cases
- `POST /api/cases` (create)
- `GET /api/cases` (list my cases / authorized)
- `GET /api/cases/{caseId}`
- `PUT /api/cases/{caseId}/step1` (save Step 1)
- `PUT /api/cases/{caseId}/submit` (submit / move forward)
- `PUT /api/cases/{caseId}/review/start`
- `PUT /api/cases/{caseId}/approve`
- `PUT /api/cases/{caseId}/reject`
- `GET /api/cases/{caseId}/audit` (audit timeline)

### Documents
- `POST /api/cases/{caseId}/documents` (multipart upload: `file`, `type`, `label`)
- `GET /api/cases/{caseId}/documents`
- `GET /api/documents/{docId}/download`
- `DELETE /api/documents/{docId}`

### Admin
- `GET /api/admin/stats`

---

## 7) Sequence Diagram — Approval Path

```mermaid
sequenceDiagram
  participant Req as Requester (UI)
  participant Api as API
  participant App as Approver (UI)
  participant DB as MySQL
  participant Aud as AuditService

  Req->>Api: PUT /api/cases/{id}/submit
  Api->>DB: status=SUBMITTED, dueAt=...
  Api->>Aud: "Submitted"
  Aud->>DB: insert audit_event
  Api-->>Req: 200 case DTO

  App->>Api: PUT /api/cases/{id}/review/start
  Api->>DB: status=IN_REVIEW
  Api->>Aud: "Review started"
  Aud->>DB: insert audit_event
  Api-->>App: 200 case DTO

  App->>Api: PUT /api/cases/{id}/approve
  Api->>DB: status=APPROVED
  Api->>Aud: "Approved"
  Aud->>DB: insert audit_event
  Api-->>App: 200 case DTO
```

---

## 8) Local Run

### Backend
1. Configure MySQL + update `backend/src/main/resources/application.yml`
2. Run:
   - `mvn -q -f backend/pom.xml spring-boot:run`

### Frontend
- `cd frontend`
- `npm install`
- `npm start`

---

## 9) Postman
Import the included Postman collection:
- `postman/KYC-Onboarding.postman_collection.json`

Set variables:
- `baseUrl` (e.g., `http://localhost:8080`)
- `token` (auto-set after login)

---

## 10) OAuth2 Notes
OAuth2 requires browser-based redirects:
- `/oauth2/authorization/{provider}` (configured in Spring Security)
- On success, backend redirects to `app.oauth2.redirectUrl?token=<JWT>`
