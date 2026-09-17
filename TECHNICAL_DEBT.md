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
| **Audit Logging Aspect** | Audit Domain | Core operations are logged; automatic `@Audited` AOP aspect for all entity mutations will be integrated. | Phase 7 (Audit & Hardening) | ⏳ Planned for Phase 7 |
| **Rate Limiting Configuration** | Security / Bucket4j | Bucket4j dependency is imported; IP-based rate limiting filter bean to be activated. | Phase 7 (Security Hardening) | ⏳ Planned for Phase 7 |
| **Demo Environment Seed Data** | Database Migration | Demo data migration (`V100__seed_demo_data.sql`) will be added to populate realistic cases for the demo scenario (PRD §20). | Post-Phase 7 Demo Prep | ⏳ Planned |

---

## 🛡️ Architectural Boundaries & Compliance Checks

- **Zero Hardcoded Secrets**: All DB credentials, JWT secrets, and API keys are strictly loaded via environment variables (`application.yml` / `application-local.yml` / `application-prod.yml`).
- **Idempotency & Auditing**: No hard deletion on business entities; append-only design principles maintained.
- **Strict Role-Based Access Control**: Method-level security (`@PreAuthorize`) configured on all sensitive endpoints (`/api/v1/cases/assigned`, `/api/v1/cases/team`, `/api/v1/cases/{id}/notes`, `/api/v1/collaboration/workload/team`).
- **Decoupled AI & Email Ports**: Core case management functions independently of AI/email provider availability.
