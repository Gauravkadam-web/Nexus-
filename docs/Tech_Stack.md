# Nexus — Tech Stack
### *AI-Powered Case Management Platform*

> **Source:** Extracted from `Nexus_SRS.md` §3 & §4 (SRS v1.1)

---

## Tech Stack

| Layer | Technology | Notes |
|---|---|---|
| Backend Framework | Spring Boot 3.x (Java 21) | Maven build |
| Frontend Framework | Flutter | Single codebase for Web + Mobile |
| Authentication | Custom — Spring Security + JWT (access + refresh tokens) | Not Supabase Auth |
| Database | Supabase PostgreSQL | Database only (no Supabase Auth) |
| File Storage | Supabase Storage | Case attachments, evidence, documents |
| ORM | Spring Data JPA (Hibernate) | |
| Database Migrations | Flyway | Versioned SQL migrations |
| AI Integration | Spring AI (`ChatModel` abstraction) | Provider selected via `AI_PROVIDER` env variable — supports OpenAI, Anthropic Claude, Google Gemini without code changes |
| Email | Custom `EmailProviderPort` abstraction | Provider selected via `EMAIL_PROVIDER` env variable — Brevo (default), SMTP, or others, pluggable |
| Scheduling | Spring `@Scheduled` | SLA checks, escalation triggers, summary refresh — no separate worker/queue in V1 |
| Security | Spring Security, RBAC, Bucket4j (rate limiting) | |
| Monitoring | Spring Boot Actuator, Sentry | Health checks, error/exception tracking |
| Logging | Logback (structured JSON) | |
| CI/CD | None (Git-based auto-deploy via hosting platforms) | No separate GitHub Actions pipeline |
| Backend Hosting | Render | Auto-deploy on push to `main` |
| Frontend Hosting | Vercel | Flutter Web build, auto-deploy on push to `main` |

### Architecture Rule (V1)

No mandatory background worker, message queue (RabbitMQ/Kafka/Redis Streams), or workflow engine (n8n/Temporal/Airflow) in V1. Spring Boot services with scheduled jobs are sufficient for expected load. These are introduced later only on demonstrated need (heavy AI/document workload, high notification volume, complex async workflows).

---

## System Architecture

### High-Level Architecture

```
                          USERS
             ┌──────────────┼──────────────┐
        Requester        Operator      Management
             └──────────────┼──────────────┘
                             ▼
                    Flutter (Web + Mobile)
                             │  REST/JSON (JWT bearer)
                             ▼
                    Spring Boot API (Render)
        ┌────────────────────┼─────────────────────┐
        ▼                    ▼                     ▼
 Case Management        AI Services          Automation
        │                    │              (Scheduler + Events)
        └────────────────────┼─────────────────────┘
                              ▼
                     Supabase PostgreSQL
              ┌───────────────┼────────────────┐
           Cases            Audit          Analytics
                              │
                              ▼
                     Supabase Storage (attachments)
```

### Backend Service Layers

| Service | Responsibility |
|---|---|
| Auth Service | JWT issuance/refresh, password handling, RBAC |
| Case Management Service | Case CRUD, lifecycle/state transitions |
| Workflow Service | State machine, lifecycle rules |
| AI Service | Wraps Spring AI `ChatModel`; classification, summarization, missing-info detection, duplicate detection, assignment recommendation, communication drafting, copilot |
| Assignment Service | Smart assignment recommendation, workload balancing |
| SLA Service | SLA policy evaluation, countdown, risk detection (scheduled) |
| Escalation Service | Escalation rule evaluation and triggering |
| Notification Service | In-app + email notifications via `EmailProviderPort` |
| Audit Service | Append-only audit log writes |
| Analytics Service | Aggregated reporting for Manager dashboard |

### AI Provider Abstraction

```
AI Service ──► AIProviderPort (Spring AI ChatModel)
                    │
        AI_PROVIDER env var selects implementation
                    │
        ┌───────────┼────────────┐
     OpenAI       Claude        Gemini
```

### Email Provider Abstraction

```
Notification Service ──► EmailProviderPort
                    │
        EMAIL_PROVIDER env var selects implementation
                    │
        ┌───────────┼────────────┐
     Brevo         SMTP        (future: SendGrid/Resend)
```

### Reliability Principles

| Requirement | Implementation |
|---|---|
| Graceful degradation | If AI API fails, case creation/assignment/investigation/communication continue normally; AI analysis retried later |
| Idempotency | Scheduled jobs check `automation_events` for an existing event before creating a new one |
| Execution tracking | Every automated operation logged with type, trigger, status, timestamps, and result |
| AI output validation | Backend validates all structured AI responses; invalid output is rejected/retried/falls back |
