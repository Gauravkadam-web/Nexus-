# Nexus — Project Progress Tracker

> **Last Updated:** 2026-09-16  
> **Current Strategy:** Backend-First (Phases 0–7 Backend → Flutter Frontend)  
> **Overall Status:** Phase 0 & Phase 1 Complete (100% Green Tests)

---

## 📊 Phase Roadmap & Delivery Status

| Phase | Domain / Scope | User Stories | Status | Test Coverage | Branch / Integration |
|---|---|---|---|---|---|
| **Phase 0** | **Project Foundation & Scaffolding** | Infrastructure | ✅ Complete | 2/2 Passed | Merged to `dev` |
| **Phase 1** | **Core Case Management & Auth** | US-1 to US-5 | ✅ Complete | 11/11 Passed | Merged to `dev` |
| **Phase 2** | Communication, Evidence & Investigation | US-6 to US-10 | ⏳ Next | Pending | `feature/phase2/...` |
| **Phase 3** | AI Case Intelligence (Spring AI) | US-11 to US-15 | ⏹️ Queued | Pending | Phase 3 Roadmap |
| **Phase 4** | Related Cases & Smart Operations | US-16 to US-20 | ⏹️ Queued | Pending | Phase 4 Roadmap |
| **Phase 5** | SLA, Risk & Escalation Automation | US-21 to US-25 | ⏹️ Queued | Pending | Phase 5 Roadmap |
| **Phase 6** | Resolution, Problem Mgmt & AI Copilot | US-26 to US-30 | ⏹️ Queued | Pending | Phase 6 Roadmap |
| **Phase 7** | Analytics, Audit & Security Hardening | US-31 to US-35 | ⏹️ Queued | Pending | Phase 7 Roadmap |
| **Frontend** | Flutter Web & Mobile Client Application | US-1 to US-35 UI | ⏹️ Queued | Post-Backend | Flutter Pipeline |

---

## ✅ Completed Deliverables (Phase 0 & Phase 1)

### Phase 0: Foundation & Infrastructure
- [x] Spring Boot 3.3.4 + Java 21 + Maven Wrapper project scaffolding
- [x] Standard Layered Architecture (`config/`, `common/`, domain skeletons)
- [x] Centralized Exception Handling (`GlobalExceptionHandler.java`)
- [x] Generic Response Envelope (`ApiResponse<T>`)
- [x] Environment Profiles (`application.yml`, `application-local.yml`, `application-prod.yml`, `.env.example`)
- [x] Base Database Migration (`V1__init_org_user_role.sql`)
- [x] Operational Health Check API (`GET /api/v1/health`)

### Phase 1: Core Case Management & Authentication
- [x] **Flyway Migration `V2__create_case_tables.sql`**: `cases` & `case_assignments` with relational indexes.
- [x] **Authentication & JWT Security**:
  - `User`, `Role`, `Organization` entities & repositories.
  - JJWT 0.12.6 HMAC-SHA256 Token Provider (15m Access Token, 7d Refresh Token).
  - Stateless `JwtAuthenticationFilter` with role authorities.
  - `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh`, `GET /api/v1/auth/me`.
- [x] **Organization, Team & Category Domain**:
  - `Organization`, `Team`, `Category` entities and CRUD endpoints (`/api/v1/categories`, `/api/v1/teams`).
- [x] **Case Lifecycle State Machine (`CaseLifecycleService`)**:
  - Transition matrix strictly enforcing valid lifecycle steps.
  - Automatic error rejection for invalid jumps (e.g. `UNDERSTOOD` → `CLOSED` throws `400 Bad Request`).
- [x] **Case Operations (US-1 to US-5)**:
  - **US-1**: `POST /api/v1/cases` (Auto-generated case number `NEX-YYYYMMDD-XXXX`, category default team routing).
  - **US-2**: `GET /api/v1/cases/my` (Requester's cases with pagination).
  - **US-3**: `GET /api/v1/cases/assigned` (Operator's assigned cases with pagination).
  - **US-4**: `PATCH /api/v1/cases/{id}/status` (Status update with lifecycle validation & auto-timestamps).
  - **US-5**: `GET /api/v1/cases/team` (Team Lead's team cases with pagination).
  - **Assignment API**: `POST /api/v1/cases/{id}/assign` (with audit history in `case_assignments`).
- [x] **Automated Tests**: 11 unit & MockMvc integration tests passing cleanly.

---

## 🎯 Immediate Next Milestone (Phase 2 Backend)
1. **Flyway Migration `V3__create_collaboration_tables.sql`**: `case_messages`, `internal_notes`, `case_tasks`, `investigations`, `case_attachments`.
2. **Missing Information Flow (US-6 & US-7)**: Info requests & requester responses with automatic status transition to `WAITING_FOR_INFO`.
3. **Communication & Attachments (US-8)**: Public case messaging & operator internal notes.
4. **Investigation Tasks (US-9 & US-10)**: Task creation, assignment, tracking, and team lead workload monitoring.
