# AGENTS.md — Nexus: AI-Powered Case Management Platform

> **Every response from the agent must begin with a greeting to the user as "Gaurav Bhau".**

This file defines the behavioral rules, architectural boundaries, workflow conventions, and quality standards that all AI coding agents must follow when working on the **Nexus** codebase. Read this file before taking any action. These rules are non-negotiable unless explicitly overridden by the user.

---

## 0. Scope & Phase Discipline

- Build **only** what is traced to the current delivery phase's User Stories (US-1 through US-35, as defined in PRD Appendix A and SRS §8).
- The phases are:

  | Phase | Focus | User Stories |
  |---|---|---|
  | 0 | Project foundation (infra only) | — |
  | 1 | Core case management | US-1 to US-5 |
  | 2 | Communication, evidence & investigation | US-6 to US-10 |
  | 3 | AI case intelligence | US-11 to US-15 |
  | 4 | Related cases & smart operations | US-16 to US-20 |
  | 5 | SLA, risk & escalation automation | US-21 to US-25 |
  | 6 | Resolution, problem management & AI copilot | US-26 to US-30 |
  | 7 | Analytics, audit, search & security hardening | US-31 to US-35 |

- Do **not** implement anything from PRD §22 "Future Opportunities" or beyond the currently approved phase — even if it seems easy or "while I'm in there." This includes: voice-based reporting, natural-language analytics, advanced RAG, multi-org SaaS, message queues (RabbitMQ/Kafka/Redis Streams), n8n/Temporal/Airflow, dedicated background workers, push/SMS/WhatsApp/Slack/Teams notification channels.
- If a task seems to require touching a future-phase feature, **stop and ask** instead of assuming it is fine because it is "just a small addition."
- Every feature you build must be traceable to a specific US number (US-1 to US-35). If you cannot map it to one, ask before building it.

---

## 1. Architecture & Tech Stack

Do not change the approved architecture without explicit approval:

| Layer | Technology | Notes |
|---|---|---|
| **Backend Framework** | Spring Boot 3.x (Java 21) | Maven build |
| **Frontend** | Flutter (Web + Mobile, single codebase) | Hosted on Vercel as a Flutter Web build |
| **Authentication** | Spring Security + JWT (access + refresh tokens) | Custom — **not** Supabase Auth |
| **Database** | Supabase PostgreSQL | Used strictly as managed Postgres — **no Supabase Auth/SDK/Realtime** |
| **File Storage** | Supabase Storage | Case attachments and evidence only |
| **ORM** | Spring Data JPA (Hibernate) | |
| **Database Migrations** | Flyway (versioned SQL files: `V1__…`, `V2__…`, etc.) | |
| **AI Integration** | Spring AI `ChatModel` abstraction | Provider selected via `AI_PROVIDER` env var — supports OpenAI, Anthropic Claude, Google Gemini |
| **Email** | Custom `EmailProviderPort` abstraction | Provider selected via `EMAIL_PROVIDER` env var — Brevo (default), SMTP, or others |
| **Scheduling** | Spring `@Scheduled` | SLA checks, escalation triggers, summary refresh — **no separate worker/queue in V1** |
| **Security** | Spring Security, RBAC, Bucket4j (rate limiting) | |
| **Monitoring** | Spring Boot Actuator, Sentry | |
| **Logging** | Logback (structured JSON) | |
| **Backend Hosting** | Render | Auto-deploy on push to `main` |
| **Frontend Hosting** | Vercel | Flutter Web build (`flutter build web`, output `build/web`); auto-deploy on push to `main` |

### Layering Conventions

- **Backend layers** (per SRS §5.1): `config/` → `common/` → per-domain packages, each with `controller/`, `service/`, `repository/`, `entity/`, `dto/`.
- **Frontend layers** (per SRS §5.2): `core/` (config, network, theme, routing, widgets, utils) + `features/` (each feature: `data/`, `domain/`, `presentation/`).
- Follow the established folder structure exactly as defined in SRS §5. Do not modify files unrelated to the current task.
- Follow the PRD, SRS, and any companion specification documents exactly as written. If a task requires deviating from them, stop and ask first.

### Architecture Rules

- **No mandatory background worker, message queue (RabbitMQ/Kafka/Redis Streams), or workflow engine (n8n/Temporal/Airflow) in V1.** Spring Boot services with `@Scheduled` jobs are sufficient for expected load.
- The AI service and email provider must fail gracefully — core case management must continue functioning if either is unavailable (PRD §6.4, §13, SRS §9).
- Scheduled jobs must be **idempotent** — check `automation_events` for an existing event before creating a new one (PRD §6.4).

---

## 2. Data & Configuration

- No hardcoded or dummy data anywhere — including business data (users, cases, categories, teams, SLA policies, chart data, etc.). All data must come from the real database via the API layer.
- Demo/seed data for the demo environment (PRD §20) goes in via **Flyway seed migrations** — never hardcoded in frontend or backend code.
- AI-derived values are **never written directly to the `cases` table** — they go into `ai_analysis`, `ai_suggestions`, and `ai_summaries` tables (SRS §6.2, §6.4).
- Configuration is **env-driven only** — this is a universal rule:
  - Every environment concern (DB URL, AI provider, email provider, JWT secret, storage bucket, Sentry DSN, API base URL) is set via environment variables, never hardcoded.
  - Separate env profiles for local (`application-local.yml`) and production (`application-prod.yml`) — never merge them.
  - Frontend reads `API_BASE_URL` from its own env config — never a hardcoded string anywhere in Dart code.
  - `.env` files and `*.yml` secrets are **never committed** to version control — only `.env.example` / `application.yml` with placeholder values.
- Never read, print, log, or expose the contents of `.env` files or any secret values in code, logs, or conversation.
- Never request, expose, or log secrets, API keys, connection strings, JWT secrets, or credentials — in code, commits, or conversation.
- Use `AI_PROVIDER=mock` for offline/local development to avoid AI API costs (SRS §11).

---

## 3. AI & Human-in-the-Loop Rules

These rules are non-negotiable (PRD §4, §7, §13):

- **AI recommends; humans decide.** Every AI output must be presented as a recommendation (`PENDING` status), never applied automatically to confirmed case data. Operators must explicitly Accept / Modify / Reject every suggestion.
- **AI suggestions stay visually distinct from confirmed data.** Example: `AI Recommendation: Priority HIGH (82%)` vs `Confirmed: Requester reports VPN unavailable`. Never mix AI-derived and human-confirmed values in the same display without clear labeling.
- **Core case management must work if AI is down.** Case creation, assignment, investigation, and communication must not be blocked by AI API unavailability. AI analysis is retried later.
- **Validate all structured AI responses on the backend.** Invalid or malformed AI output is rejected/retried — it must never reach the database.
- **No false certainty in AI output.** AI presents assumptions as assumptions: *"the available information suggests a VPN service issue may be involved"*, not *"the VPN server is down"*, unless confirmed by a human.
- **AI security:** Guard against prompt injection, cross-org data leakage, unauthorized case context exposure, and sensitive-info exposure in AI payloads. AI receives only the data required for the specific operation.

---

## 4. Open Risk — Do Not Silently Resolve

The SRS (§3, §11) specifies deployment to publicly reachable URLs (Render + Vercel) with JWT-based authentication. If a task touches deployment configuration (Render, Vercel, Supabase, environment variables), and you notice any security gap — e.g. an endpoint is accidentally public, or a secret is about to be committed — **stop and surface it rather than silently fixing or ignoring it**.

- If you find any conflict between the PRD, SRS, and any companion docs (e.g. the API contract, database schema, folder structure) — **flag it, don't decide it**.
- This applies to any ambiguity about RBAC scope, which roles can access which endpoints, or which escalation/notification conditions should fire automatically vs. require human confirmation.
- Any other undocumented assumption you introduce must be surfaced to the user — do not decide unilaterally.

---

## 5. Code Quality

- Keep code **clean, maintainable, and well-documented** at all times — no dead code, no commented-out blocks left behind, no TODO left unresolved without a tracking note.

- **Backend (Java/Spring Boot):**
  - No inline business logic in controllers — keep controllers thin; logic lives in Service classes.
  - Repository layer uses Spring Data JPA — no raw SQL except in complex JPQL or native queries where necessary.
  - Use `@Transactional` appropriately; never leave partial writes.
  - Entities use UUID PKs and `created_at`/`updated_at` timestamps (as per SRS §6).
  - Every enum used in entities must match the exact values defined in SRS §6 (e.g. `CaseStatus`, `Severity`, `Priority`, `SuggestionStatus`, `EscalationLevel`, etc.).
  - Never write UPDATE or DELETE on `audit_logs` — it is append-only at the application level (SRS §6.7).
  - Idempotency check required for all `@Scheduled` jobs before creating events/notifications.
  - All public service methods and REST endpoints must have Javadoc comments explaining what they do, their parameters, and any side effects.

- **Frontend (Flutter/Dart):**
  - Follow feature-based folder structure: `features/<feature>/data/`, `domain/`, `presentation/`.
  - Use `go_router` for navigation with role-based route guards.
  - Use Dio with JWT interceptors for API calls; handle 401 → refresh token → retry.
  - No hardcoded `API_BASE_URL` — read from env config.
  - **No hardcoded styling** — colors, font sizes, spacing, and radii must come from the app's `ThemeData` / design tokens, never hardcoded inline in widget trees.
  - AI recommendations must be clearly distinguished from confirmed data in all UI widgets.
  - All shared/reusable widgets must be placed in `core/widgets/` — never duplicated across features.

- **Migrations:**
  - No schema change (column/table/index/enum) without a matching new Flyway versioned migration file (`V{N}__description.sql`).
  - Never modify existing migration files — add a new one instead.
  - Ask before adding or changing schema if it was not part of the task scope.

- **Validation — match specs exactly (SRS §9):**
  - Backend: Bean Validation annotations + service-layer checks must match the rules in SRS §9 and PRD.
  - Frontend: Form validation must **mirror** backend validation rules exactly — no double-standard, no looser client-side rules.
  - Specific rules to enforce consistently (examples):
    - Case title: required, max 100 characters.
    - Case description: required.
    - Attachments: file type and size limits as per SRS §9.
    - Status transitions: only valid lifecycle moves allowed — invalid transitions must be rejected with a clear error.
    - AI suggestion decisions: `ACCEPTED`, `MODIFIED`, `REJECTED` only — no other values.
  - If the PRD/SRS specifies a validation rule and the current implementation does not enforce it, fix it rather than leaving it.

- **Error & Loading States:**
  - Every screen that fetches data must handle three states: loading, error, and empty — never leave a blank/frozen UI.
  - Error messages shown to users must be user-friendly; raw stack traces and exception class names must never appear in the UI.

---

## 6. Workflow

### 6.1 Git Branching Strategy (Industry Standard)

**Branch hierarchy:**

```
main          ← production-ready only; auto-deploys to Render + Vercel
  └── dev     ← integration branch; all feature work lands here first
        └── feature/<scope>/<short-description>   ← one branch per task/US
```

**Rules — non-negotiable:**

- **Never push directly to `main` or `dev`.** All changes go through a Pull Request.
- **Always branch off `dev`**, not `main`:
  ```
  git checkout dev
  git pull origin dev
  git checkout -b feature/case-management/create-case-api
  ```
- **Branch naming convention** — `feature/<scope>/<short-description>`:
  - `feature/auth/jwt-refresh-endpoint`
  - `feature/case/create-case-flutter-form`
  - `feature/sla/scheduler-risk-detection`
  - `fix/case/status-transition-validation`
  - `chore/flyway/V3-add-collaboration-tables`
- **One branch = one US (User Story) or one focused task.** Do not mix unrelated work in a single branch.
- **PR flow:**
  1. Feature branch → `dev` PR (for integration + review)
  2. `dev` → `main` PR (only when `dev` is stable and tested — production release)
- **Before raising a PR**, all checks must pass locally: `./mvnw verify` + `dart analyze` + `flutter test`.
- **Ask for permission** before pushing or opening a PR — never do it autonomously.
- Commit messages follow **Conventional Commits**: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:` — always scoped to the US or area, e.g. `feat(case): add case creation endpoint (US-1)`.

### 6.2 General Workflow Rules

- Run all required checks before considering a task complete:
  - **Backend lint + static analysis:** `./mvnw checkstyle:check` (or equivalent configured linter) before `./mvnw verify`.
  - **Backend tests:** `./mvnw verify` (unit + integration tests via JUnit 5 + Mockito + Spring Boot Test).
  - **Frontend lint:** `dart analyze` — zero warnings or errors before any commit.
  - **Frontend tests:** `flutter test` (unit + widget + integration tests).
- Test locally in development first; only move toward production discussion after local verification passes.
- **NEVER run `git commit`, `git push`, `git merge`, or open a PR autonomously** — not even with permission. Instead, always provide the exact ready-to-run commands for the user to execute themselves, e.g.:
  ```bash
  git add .
  git commit -m "feat(case): add case creation endpoint (US-1)"
  git push origin feature/case/create-case-api
  ```
  The user will copy and run these commands manually. This is the permanent working mode for this project.
- Update the relevant documentation (SRS or PRD) whenever architecture or specs change, so docs and code never drift apart.
- Don't silently resolve any flagged open risk (see §4 above) or introduce new undocumented assumptions — surface them, don't decide unilaterally.
- When making changes to shared utilities, `core/` modules, or database schema — explicitly check downstream impact on other features before submitting.

---

## 7. Feature Guide Protocol (`SESSION_FEATURES_EXPLAINED.md`)

Whenever the user asks to create or update the feature guide (e.g. *"feature guide bana"*):

1. Read the existing `SESSION_FEATURES_EXPLAINED.md` file from the workspace root.
2. Maintain the established friendly, structured, and easy-to-understand explanation style (with problem context, architecture, security highlights, and flow diagrams).
3. **Concatenate/append** the current session's newly implemented features into `SESSION_FEATURES_EXPLAINED.md` without losing past session records.
4. Ensure `SESSION_FEATURES_EXPLAINED.md` remains untracked in `.gitignore`.

---

## 8. When In Doubt

If a request is ambiguous, conflicts with the PRD/SRS, or would require touching a future-phase or out-of-scope feature, **stop and ask rather than guessing** — this project explicitly favors surfacing conflicts over silently resolving them.

**Key documents to cross-reference:**
- [`docs/Nexus_PRD.md`](docs/Nexus_PRD.md) — Product requirements, user stories (US-1 to US-35), lifecycle, AI features, SLA rules, delivery roadmap (Phases 0–7), demo scenario, MVP definition.
- [`docs/Nexus_SRS.md`](docs/Nexus_SRS.md) — Tech stack, architecture, folder structure, database schema (SRS §6), API contract (SRS §7), functional requirements (SRS §8), NFRs (SRS §9), environment config (SRS §10), deployment (SRS §11).
