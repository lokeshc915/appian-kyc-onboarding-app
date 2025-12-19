# Appian → Code Traceability Matrix (PV → Field → API → UI)

> PV = “Process/Requirement” from the Appian requirements document.

| PV / Requirement | Data field(s) | API | UI Screen/Component |
|---|---|---|---|
| Create a **new onboarding case** | `onboarding_case.id`, `created_by_user_id`, `status` | `POST /api/cases` | Dashboard → “New Case” button |
| Step 1: **Enter account details** | `account_*` columns (embedded `AccountDetails`) | `PUT /api/cases/{id}/step1` | Step 1 page (Account Details Form) |
| Milestone wizard: Step 1 → Step 2 | `status` transitions (`DRAFT_STEP1 → DRAFT_STEP2`) | `PUT /api/cases/{id}/submit` (or `next`) | Wizard “Next” button |
| Step 2: **Upload identity / financial / recommendation documents** | `document_record.*` | `POST /api/cases/{id}/documents` (multipart) | Step 2 Documents Grid + Upload dialog |
| “Add Row” (multiple docs) | multiple `document_record` rows | repeat `POST /documents` | Documents grid “Add Row” |
| **View / Download** onboarding docs | `storage_path`, `original_filename` | `GET /api/documents/{docId}/download` | Document list row actions |
| Security privileges | `users`, `user_roles` | Spring Security role checks | Guards/route protection + button visibility |
| SLA / Due Date (timer) | `dueAt`, `slaState`, `slaBreachedAt` | Background scheduler (no endpoint) + shown in case DTO | Case details header + SLA badge |
| Audit history timeline | `audit_event.*` | `GET /api/cases/{id}/audit` | Audit Timeline page |
| Admin dashboard & reports | Aggregations | `GET /api/admin/stats` | Admin Dashboard |
