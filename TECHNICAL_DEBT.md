# Nexus — Technical Debt & Architectural Watchlist

> **Last Updated:** 2026-09-17  
> **Status:** Healthy & Zero Unresolved Blockers

This document tracks intentional trade-offs, temporary scaffolding defaults, and architectural considerations to keep Nexus maintainable, performant, and compliant with PRD & SRS standards.

---

## 🟢 Open Items & Planned Improvements

| Item | Scope / Domain | Description | Action / Target Phase | Status |
|---|---|---|---|---|
| **Spring Data Page Serialization Warning** | Spring Boot / Web | In Spring Boot 3.3, serializing `PageImpl` directly emits a minor stability warning. | Configured `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` in `WebMvcConfig.java` | ✅ Resolved in Phase 2 |
| **Case Attachments & Storage Service** | Storage / Evidence | File uploads track metadata in `case_attachments`; pluggable `StorageService` interface implemented with local file handling & Supabase readiness. | `V3__create_collaboration_tables.sql` & `AttachmentService` | ✅ Resolved in Phase 2 |
| **Audit Logging Engine** | Audit Domain | Immutable, append-only audit trail with `REQUIRES_NEW` transaction boundary and entity timeline extraction. | `V8__create_audit_logs_and_search_indexes.sql` & `AuditService` | ✅ Resolved in Phase 7 |
| **Rate Limiting Configuration** | Security / Bucket4j | Token bucket rate limiting (10 req/min for auth, 100 req/min for general API) with 429 payload and health endpoint bypass. | `RateLimitingFilter.java` & `SecurityConfig.java` | ✅ Resolved in Phase 7 |
| **PostgreSQL 18+ JSONB Mapping** | Hibernate 6 / JPA | PostgreSQL requires explicit JSON type casting for string-mapped JSONB columns. | Added `@JdbcTypeCode(SqlTypes.JSON)` to `AiAnalysis`, `AiSuggestion`, `AutomationEvent` | ✅ Resolved in Live E2E Testing |
| **Case Assignment HTTP Method Parity** | REST Controllers | Support both `POST` and `PATCH` methods on `/api/v1/cases/{id}/assign` for frontend flexibility. | Updated `@RequestMapping(method = {RequestMethod.POST, RequestMethod.PATCH})` in `CaseController` | ✅ Resolved in Live E2E Testing |
| **Java 25 Mockito Byte Buddy Warning** | Maven / Testing | Java 21+ / Java 25 early access prints warning on dynamic agent loading. | Configured `-XX:+EnableDynamicAgentLoading` in `maven-surefire-plugin` `pom.xml` | ✅ Resolved |
| **Demo Environment Seed Data** | Database Migration | Demo data migration (`V100__seed_demo_data.sql`) will be added to populate realistic cases for the demo scenario (PRD §20). | Post-Backend Demo Prep | ⏳ Planned |

---

## 🛡️ Architectural Boundaries & Compliance Checks

- **Zero Hardcoded Secrets**: All DB credentials, JWT secrets, and API keys are strictly loaded via environment variables (`application.yml` / `application-local.yml` / `application-prod.yml`).
- **Idempotency & Auditing**: No hard deletion on business entities; append-only design principles maintained.
- **Strict Role-Based Access Control**: Method-level security (`@PreAuthorize`) configured on all sensitive endpoints (`/api/v1/cases/assigned`, `/api/v1/cases/team`, `/api/v1/cases/{id}/notes`, `/api/v1/collaboration/workload/team`, `/api/v1/admin/**`, `/api/v1/audit-logs/**`, `/api/v1/analytics/**`).
- **Decoupled AI & Email Ports**: Core case management functions independently of AI/email provider availability (graceful degradation).
- **Immutable State Machine**: All lifecycle status transitions strictly validated via `CaseLifecycleService`.

