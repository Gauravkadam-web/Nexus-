# Nexus — Functional & Non-Functional Requirements
### *AI-Powered Case Management Platform*

> **Source:** Extracted from `Nexus_SRS.md` §8 & §9 (SRS v1.1)

---

## 8. Functional Requirements

Functional requirements are traced from the PRD's Master User Story Index (Appendix A, US-1 through US-35), grouped by delivery phase:

| Phase | Focus | Linked User Stories |
|---|---|---|
| 0 | Project foundation (no user stories — infra only) | — |
| 1 | Core case management | US-1 to US-5 |
| 2 | Communication, evidence & investigation | US-6 to US-10 |
| 3 | AI case intelligence | US-11 to US-15 |
| 4 | Related cases & smart operations | US-16 to US-20 |
| 5 | SLA, risk & escalation automation | US-21 to US-25 |
| 6 | Resolution, problem management & AI copilot | US-26 to US-30 |
| 7 | Analytics, audit, search & security hardening | US-31 to US-35 |

Each user story's detailed acceptance criteria are inherited directly from the PRD sections referenced against it (see PRD Appendix A) and are not duplicated here to avoid drift between documents.

---

## 9. Non-Functional Requirements

| Category | Requirement |
|---|---|
| Security | Authentication, RBAC, secure password hashing, JWT/session security, input and file validation, API-level authorization, sensitive-data protection, audit logging, secure env variables, rate limiting. AI and email provider credentials never exposed to frontend clients |
| AI Security | Guards against prompt injection, sensitive-info exposure, unauthorized case context, excessive AI permissions, unsafe generated content, malicious uploads, cross-org data leakage. AI receives only the data required for the specific operation |
| Performance | Pagination, database indexes (esp. `case_sla`, `cases.status`), efficient queries, lazy loading, sensible response payload sizes, AI request timeouts and retry limits |
| Reliability | Core case management remains fully functional if the AI service or email provider is down; scheduled jobs are idempotent |
| Observability | Application errors, API failures, AI failures, scheduler execution, automation failures, notification failures, and database errors are all logged and traceable, without exposing sensitive data in logs; Sentry captures exceptions in production |
| Testability | Unit, service, repository, controller, and integration tests on backend; component, form validation, and role-based UI tests on frontend; explicit AI failure-mode and idempotency tests for automation |
