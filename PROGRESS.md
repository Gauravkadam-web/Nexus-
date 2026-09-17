# Nexus — Project Progress Tracker

> **Last Updated:** 2026-09-17  
> **Current Strategy:** Backend-First (Phases 0–7 Backend → Flutter Frontend)  
> **Overall Status:** Phase 0, 1, 2, 3 & 4 Complete (100% Green Tests — 53/53 Passed)

---

## 📊 Phase Roadmap & Delivery Status

| Phase | Domain / Scope | User Stories | Status | Test Coverage | Branch / Integration |
|---|---|---|---|---|---|
| **Phase 0** | **Project Foundation & Scaffolding** | Infrastructure | ✅ Complete | 2/2 Passed | Merged to `dev` |
| **Phase 1** | **Core Case Management & Auth** | US-1 to US-5 | ✅ Complete | 11/11 Passed | Merged to `dev` |
| **Phase 2** | **Communication, Evidence & Investigation** | US-6 to US-10 | ✅ Complete | 24/24 Passed | Merged to `dev` |
| **Phase 3** | **AI Case Intelligence (Spring AI)** | US-11 to US-15 | ✅ Complete | 39/39 Passed | Merged to `dev` |
| **Phase 4** | **Related Cases & Smart Operations** | US-16 to US-20 | ✅ Complete | 53/53 Passed | `feature/cases/related-cases-smart-operations` |
| **Phase 5** | SLA, Risk & Escalation Automation | US-21 to US-25 | ⏳ Next | Pending | Phase 5 Roadmap |
| **Phase 6** | Resolution, Problem Mgmt & AI Copilot | US-26 to US-30 | ⏹️ Queued | Pending | Phase 6 Roadmap |
| **Phase 7** | Analytics, Audit & Security Hardening | US-31 to US-35 | ⏹️ Queued | Pending | Phase 7 Roadmap |
| **Frontend** | Flutter Web & Mobile Client Application | US-1 to US-35 UI | ⏹️ Queued | Post-Backend | Flutter Pipeline |

---

## ✅ Completed Deliverables

### Phase 0: Foundation & Infrastructure
- [x] Spring Boot 3.3.4 + Java 21 + Maven project scaffolding
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
  - Automatic error rejection for invalid jumps.
- [x] **Case Operations (US-1 to US-5)**:
  - **US-1**: `POST /api/v1/cases` (Auto-generated case number `NEX-YYYYMMDD-XXXX`, category default team routing).
  - **US-2**: `GET /api/v1/cases/my` (Requester's cases with pagination).
  - **US-3**: `GET /api/v1/cases/assigned` (Operator's assigned cases with pagination).
  - **US-4**: `PATCH /api/v1/cases/{id}/status` (Status update with lifecycle validation & auto-timestamps).
  - **US-5**: `GET /api/v1/cases/team` (Team Lead's team cases with pagination).
  - **Assignment API**: `POST /api/v1/cases/{id}/assign` (with audit history in `case_assignments`).

### Phase 2: Communication, Evidence & Investigation (US-6 to US-10)
- [x] **Flyway Migration `V3__create_collaboration_tables.sql`**:
  - `case_attachments`, `case_messages`, `internal_notes`, `case_tasks`, `investigations`.
- [x] **Missing Information Flow (US-6 & US-7)**:
  - **US-6 (Operator)**: Sending `QUESTION` / `EVIDENCE_REQUEST` automatically transitions Case to `WAITING_FOR_INFO`.
  - **US-7 (Requester)**: Submitting `ANSWER` automatically transitions Case back to `INVESTIGATING`.
- [x] **Communication & Notes (US-8)**:
  - Role-based message visibility filtering (`visible_to_requester`).
  - Private operator internal notes (`POST/GET /api/v1/cases/{id}/notes`) protected by RBAC.
- [x] **Investigation Tasks & Records (US-9)**:
  - Task creation, assignment, status updates (`PENDING` ➔ `IN_PROGRESS` ➔ `COMPLETED` / `CANCELLED`), and completion timestamps.
  - Structured investigation logging (`observation`, `action_taken`, `finding`, `evidence_ref`, `follow_up_needed`).
- [x] **Workload Monitoring (US-10)**:
  - `GET /api/v1/collaboration/workload/team` aggregating team cases, waiting counts, unassigned tasks, and operator workload breakdowns.
  - `GET /api/v1/tasks/pending` for filtered pending task queues.
- [x] **Attachments & Evidence**:
  - `AttachmentService` and `StorageService` (`POST/GET /api/v1/cases/{id}/attachments`).
- [x] **Technical Debt Fix**:
  - `WebMvcConfig.java` configured with `PageSerializationMode.VIA_DTO`.
- [x] **Automated Tests**: 24/24 unit, service, state-machine, and MockMvc integration tests passing cleanly.

---

### Phase 3: AI Case Intelligence (US-11 to US-15)
- [x] **Flyway Migration `V4__create_ai_tables.sql`**:
  - `ai_analysis`, `ai_suggestions`, `ai_summaries`, `automation_events` with check constraints and index optimizations.
- [x] **Multi-Provider AI Architecture (`com.nexus.ai.provider`)**:
  - `AiProviderPort`: Generic port interface for case analysis, summarization, and missing info detection.
  - `MockAiProvider`: Offline local/test provider (`AI_PROVIDER=mock`) with zero API costs.
  - `SpringAiChatProvider`: Production OpenAI/Claude/Gemini integration using Spring AI `ChatModel`.
- [x] **Asynchronous Execution & Idempotency**:
  - Dedicated `nexusAiExecutor` thread pool (`AsyncConfig.java`).
  - `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` preventing race conditions.
  - `automation_events` idempotency table preventing duplicate automated runs.
- [x] **AI Case Intelligence Deliverables (US-11 to US-15)**:
  - **US-11 (Automatic Case Analysis)**: Async classification, priority/severity recommendation, confidence scoring, next actions.
  - **US-12 (Case Summarization)**: Versioned case summary generation and on-demand operator regeneration (`POST /cases/{id}/ai/summary/regenerate`).
  - **US-13 (Missing Information Detection)**: Structured detection of missing case details for investigation.
  - **US-14 (Human-in-the-Loop Decisions)**: Operators review recommendations via `PUT /api/v1/ai/suggestions/{id}` (`ACCEPTED`, `MODIFIED`, `REJECTED`). Decisions are immutable once recorded.
  - **US-15 (Graceful Degradation)**: Case creation and lifecycle operations remain 100% unblocked even if AI provider is unreachable.
- [x] **Automated Tests**: 39/39 unit, service, idempotency, and MockMvc integration tests passing cleanly.

---

### Phase 4: Related Cases & Smart Operations (US-16 to US-20)
- [x] **Flyway Migration `V5__create_case_relations_tables.sql`**:
  - `case_relations` with foreign keys, compound indexes, and `relation_type` check constraints (`DUPLICATE`, `RELATED`, `MASTER_INCIDENT`).
- [x] **Case Relations Domain (`com.nexus.casemanagement`)**:
  - `CaseRelation` entity & `RelationType` enum.
  - `CaseRelationService`: Linking cases with self-relation prevention, cross-org boundary check, duplicate prevention, and deletion by ID.
  - `CaseRelationController`: REST endpoints (`POST /cases/{id}/relations`, `GET /cases/{id}/relations`, `DELETE /cases/relations/{relationId}`, `GET /cases/{id}/master-incident/children`).
- [x] **Duplicate Detection Engine (`CaseDuplicateDetectionService` — US-16 & US-17)**:
  - Lexical token overlap (Jaccard similarity) on title + description with category boost.
  - `GET /api/v1/cases/{id}/ai/duplicates`: Returns top suggestions with explainable match reasons and similarity score.
- [x] **Master Incidents (US-18)**:
  - Hierarchical linking under `MASTER_INCIDENT` relation type.
  - Operator query to list all child cases grouped under a master incident.
- [x] **Smart Assignment Engine (`SmartAssignmentService` — US-19 & US-20)**:
  - Intelligent recommendation combining category default team, operator workload balance (active non-closed cases), and match confidence.
  - `GET /api/v1/cases/{id}/ai/assignment-recommendation`: Suggests recommended team, recommended operator, and clear reasoning.
- [x] **Automated Tests**: 53/53 unit, service, relation, duplicate, and smart assignment tests passing cleanly.

---

## 🎯 Immediate Next Milestone (Phase 5 Backend)
1. **Flyway Migration `V6__create_sla_escalation_tables.sql`**: `sla_policies`, `sla_breaches`, `escalation_rules`, `escalations`.
2. **SLA Policy Engine (US-21)**: Priority and severity-based response and resolution targets.
3. **Automated SLA Breach & Risk Detection (US-22 & US-23)**: Idempotent `@Scheduled` background worker detecting SLA warning thresholds and breaches.
4. **Escalation Hierarchy (US-24 & US-25)**: Multi-tier escalation management and notification triggers.
