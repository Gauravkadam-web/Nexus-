# Nexus
### *AI-Powered Case Management Platform*
## Product Requirements Document (Refined — Tabular Edition)

**Version:** 2.2 (Refined, Tabular)
**Product Stage:** College Project with Production-Ready Ambition
**Primary Goal:** An AI-assisted case management platform with workflow automation, human oversight, SLA monitoring, escalation, collaboration, analytics, and complete case traceability.

> **Every case should have a clear story, clear ownership, clear progress, and a clear next step.**

---

## Table of Contents

1. [Product Overview & Vision](#1-product-overview--vision)
2. [Problem Statement & Opportunity](#2-problem-statement--opportunity)
3. [Users & Roles](#3-users--roles)
4. [Product Principles](#4-product-principles)
5. [Core Concept & Case Lifecycle](#5-core-concept--case-lifecycle)
6. [System & Automation Architecture](#6-system--automation-architecture)
7. [AI Features](#7-ai-features)
8. [Case Collaboration Features](#8-case-collaboration-features)
9. [SLA, Risk & Escalation](#9-sla-risk--escalation)
10. [Notifications](#10-notifications)
11. [Timeline & Audit](#11-timeline--audit)
12. [Problem Management](#12-problem-management)
13. [AI Reliability & Trust](#13-ai-reliability--trust)
14. [Role Dashboards](#14-role-dashboards)
15. [Search & Security](#15-search--security)
16. [Data Model](#16-data-model)
17. [Non-Functional Requirements](#17-non-functional-requirements)
18. [Delivery Roadmap (Phases 0–7)](#18-delivery-roadmap-phases-07)
19. [MVP Definition](#19-mvp-definition)
20. [Demo Environment & Scenario](#20-demo-environment--scenario)
21. [Success Criteria](#21-success-criteria)
22. [Future Opportunities & Architecture Evolution](#22-future-opportunities--architecture-evolution)
23. [Final Product Definition](#23-final-product-definition)
24. [Appendix A — Master User Story Index](#24-appendix-a--master-user-story-index)

---

## 1. Product Overview & Vision

Nexus is an AI-powered platform that helps organizations **receive, understand, assign, investigate, track, escalate, and resolve** real-world cases.

A "case" is deliberately generic — it can represent a complaint, IT incident, service request, maintenance issue, customer problem, internal request, facility issue, or any other situation requiring human action.

Unlike a traditional ticketing system that just stores cases and statuses, Nexus combines: case management, AI-assisted understanding, workflow automation, human decision-making, communication, tasks, SLA monitoring, risk detection, escalation, notifications, audit history, and operational analytics — so the system proactively helps users manage cases instead of requiring every repetitive step to be done manually.

**Vision.** Build a platform where an organization can confidently answer: *What was reported? What has happened since? Who is responsible? What's missing? What's next? Is it at risk? Does it need escalation? Was it actually resolved?*

The product should feel like a real software product — organized, transparent, proactive, fast, auditable, intelligent, and low on repetitive manual work — not an AI demo.

---

## 2. Problem Statement & Opportunity

### 2.1 The typical (broken) workflow

```
Report → Read → Triage/assign → Investigate → Request info → Requester
responds → Operator acts → Possible reassignment → Deadline approaches →
Possible escalation → Resolution → Requester confirms
```

| Problem observed | Effect |
|---|---|
| Cases assigned to the wrong team | Delays, rework, frustrated requesters |
| Important details missed | Repeated back-and-forth, slower diagnosis |
| Duplicate/related cases not recognized | Wasted effort investigating the same issue twice |
| Evidence scattered, long histories | Operators waste time re-reading before acting |
| Requesters left in the dark | Low trust, repeated "any update?" follow-ups |
| Deadlines missed | SLA breaches discovered too late to prevent |
| Recurring problems discovered late | Same incident repeats before root cause is found |
| Cases closed without confirmation | Problem may not actually be fixed |
| Actions not properly recorded | No traceability, hard to audit or improve |

A basic ticketing system *records* these events. Nexus should **understand, automate, monitor, and manage** them.

### 2.2 The opportunity

| What AI can do | Why it matters here |
|---|---|
| Understand text, conversations, images, documents, historical case data, and cross-case patterns | This is the raw material every case is made of — AI can process it faster and more consistently than a human re-reading it each time |
| Sit inside the case workflow instead of a separate chatbot | Removes the extra step of a user having to explain the situation all over again to a bot |
| Automatically assist with category, severity, priority, missing info, related/duplicate cases, suggested team, next action, summary, risk, and communication drafting | The user shouldn't have to manually trigger each of these — the system should offer them proactively, saving repetitive manual work |

---

## 3. Users & Roles

| Role | Who | Wants to... |
|---|---|---|
| **Requester** | Employee, customer, student, resident, or other reporter | Report a problem, provide evidence, answer questions, track progress, receive updates, confirm resolution, reopen if needed |
| **Case Operator** | Support/IT/facility/maintenance/service staff | Quickly understand cases, review AI recommendations, investigate, communicate, create tasks, update and resolve cases, manage SLA risk |
| **Team Lead** | Owns a team of Operators | Monitor workload, review high-risk cases, handle escalations, reassign work, monitor SLA, intervene on blocked cases |
| **Manager** | Owns operational performance | Org-wide visibility, case trends, SLA performance, escalations, workload insight, recurring-problem detection, analytics |
| **Administrator** | Owns configuration | Users, roles, teams, categories, SLA policies, escalation rules, notification config, org settings, audit access |

### Role-based experience

| Role | What they see |
|---|---|
| Requester | My cases, status, updates, questions/requested info, resolution requests, notifications |
| Operator | New/assigned cases, cases needing action, AI analysis, tasks, SLA status, at-risk cases, escalations, related cases |
| Team Lead | Team workload, at-risk cases, escalations, SLA breaches, unassigned cases, operator workload, intervention queue |
| Manager | Org-wide cases, trends, SLA performance, escalations, recurring problems, operational insights, team performance |
| Administrator | Users, teams, categories, policies, SLA config, escalation rules, org settings, audit history |

---

## 4. Product Principles

| # | Principle | What it means | Why it matters |
|---|---|---|---|
| 1 | AI is part of the workflow, not a separate chatbot | AI surfaces naturally wherever it helps — analysis, summary, missing info, related cases, assignment, next action, SLA risk, drafts, insight | Forcing users into a separate AI tool breaks the workflow and duplicates effort |
| 2 | Humans remain in control | AI recommends; humans accept, modify, reject, or override. Important decisions are never executed blindly by AI | Case outcomes affect real people and real deadlines — accountability must stay human |
| 3 | Automation reduces repetitive work, not accountability | The system auto-runs AI analysis, updates summaries, checks SLA, records events, detects risk, triggers configured escalations | Frees operators to focus on judgment calls instead of manual bookkeeping |
| 4 | No lost context | Anyone with appropriate access can understand a case without contacting the previous Operator | Staff turnover, shift changes, and handoffs shouldn't cause information loss |
| 5 | Resolution matters more than closure | A `Closed` status doesn't mean the problem is fixed — the Requester must confirm the outcome | Prevents cases being closed prematurely just to hit a metric |
| 6 | AI must never be a single point of failure | Core case management keeps working even if the AI service is down | An outage in a third-party AI API should never stop the organization from handling real cases |

---

## 5. Core Concept & Case Lifecycle

The **Case** is the system's central object. It aggregates: the original report, additional information, attachments, AI analysis/summary/recommendations, related cases, assignment, communication, internal notes, tasks, investigation records, SLA data, risk data, escalations, resolution, requester confirmation, a complete timeline, and audit records. The full journey must stay traceable end to end.

### Lifecycle

```
Reported → AI Analysis → Understood → Assigned → Investigating →
Action Taken → Resolution Proposed → Requester Confirmation → Closed
```

A case may temporarily enter: `Waiting for Information`, `At Risk`, `Escalated`, `Duplicate`, `Related`, `Reopened`, `Cancelled`. The exact lifecycle configuration is left to the Administrator.

---

## 6. System & Automation Architecture

### 6.1 High-level architecture (V1)

```
Flutter / Web Client
        │
        ▼
   Spring Boot Backend
        │
        ├── Case Management Service
        ├── Workflow Service
        ├── AI Service
        ├── Assignment Service
        ├── SLA Service
        ├── Escalation Service
        ├── Notification Service
        ├── Audit Service
        └── Analytics Service
        │
        ├──► PostgreSQL / Supabase
        ├──► AI APIs
        └──► Email / Notification Providers
```

### 6.2 Automation patterns

| Pattern | What triggers it | Why this pattern | Examples |
|---|---|---|---|
| **Event-based** | Application events (case created, requester responds) | Reacts instantly to something that already happened, keeping the case current | AI analysis → save recommendations → notify operator; response → active → summary update |
| **Time-based** | Scheduled checks (`@Scheduled` jobs) | Catches conditions that build up over time and wouldn't fire from a single event | SLA approaching/breached, inactivity, pending response, escalation deadline |
| **AI-based** | AI processing on demand or on trigger | Handles anything requiring understanding of unstructured text/content | Classification, summary, missing-info, duplicates, assignment, risk, drafts, insights |
| **User-triggered** | Explicit user request | Gives users control to ask for AI help exactly when they need it | Analyze case, regenerate summary, find similar cases, explain SLA risk, suggest next action |

### 6.3 Architecture rule — no mandatory worker/queue/n8n in V1

| What | Why |
|---|---|
| Not required for V1: separate background worker, RabbitMQ, Kafka, Redis Streams, n8n, Temporal, Airflow, Kubernetes-based workers | Spring Boot services + scheduled jobs are sufficient for expected initial load — extra infrastructure adds cost and complexity without a proven need |
| Introduce a worker/queue **later**, only on real engineering need | Heavy background processing, large AI workload, long-running jobs, high notification volume, large-scale document processing, large org workloads, or complex async workflows are the actual triggers to add this — not the presence of "automation" as a concept |

### 6.4 Reliability requirements

| Requirement | What it means | Why it matters |
|---|---|---|
| Graceful degradation | If the AI API is unavailable when a case is created, the case is still created and fully usable (assign, investigate, communicate); AI analysis retries later | Users must never be blocked from doing their job by a third-party AI outage |
| Idempotency | Scheduled/automated jobs (SLA checks, escalations, notifications, timeline events, status changes) must check for an existing event before creating a new one | Prevents the same SLA warning or escalation firing repeatedly for one condition |
| Execution tracking | Automated operations are logged with what ran, when, the result, and the action taken | Makes automated behavior explainable and debuggable instead of a black box |

---

## 7. AI Features

| # | Feature | What it does | Why it's needed |
|---|---|---|---|
| 7.1 | **Automatic Case Analysis** | On case creation, analyzes likely category/subcategory, severity, priority, important details, missing information, related cases, potential duplicates, suggested team, and recommended next action; results stored with the case | Removes the manual triage step and gives the Operator a head start instead of a blank case |
| 7.2 | **Automatic Case Summarization & Memory** | Continuously maintains an AI-generated summary (original report, new information, confirmed findings, actions taken, failed attempts, blocker, status, remaining work), updated as the case evolves; AI retains context across the case | Saves operators from re-reading long histories and prevents requesters having to re-explain themselves |
| 7.3 | **Missing Information Detection** | Detects likely-required information (device type, error message, timing, etc.) and drafts suggested questions for the Operator to review and send | Speeds up investigation by front-loading the questions that would otherwise cause a delay later |
| 7.4 | **Related / Duplicate Case Detection & Master Incidents** | Compares new cases against historical ones to flag duplicates/related cases; multiple cases behind one issue can be grouped under a human-approved Master Incident | Avoids wasted effort investigating the same root problem multiple times |
| 7.5 | **Smart Assignment Recommendation** | Recommends a team/person based on category, team responsibility, similar past cases, current workload, availability, and location, with a stated reason | Reduces mis-routing and balances load without a human manually checking every operator's plate |
| 7.6 | **AI-Generated Communication** | Drafts information requests, progress updates, resolution messages, and escalation summaries; Operator edits/regenerates/cancels/sends | Cuts down repetitive writing while keeping the human in charge of what's actually said |
| 7.7 | **AI Operator Copilot** | A case-scoped assistant answering "what happened / what's confirmed / what's missing / what's blocking this / what's next / is this at risk / what should I send" using case context | Gives operators instant answers without hunting through the timeline manually |
| 7.8 | **Root-Cause Assistance & Operational Insights** | Analyzes similar cases, findings, and patterns to suggest a possible root cause (labeled as suggestion until confirmed); surfaces org-level patterns like rising cases in a category/location, workload imbalance, long resolution times | Helps operators diagnose faster and helps managers catch systemic issues before they become bigger problems |

Every AI output in the table above stays distinguishable from confirmed data — e.g. `AI Recommendation: Priority High (82%)` vs. `Confirmed: Requester reports VPN unavailable`. Operator actions on any recommendation: **Accept / Modify / Reject** — AI must never silently overwrite confirmed case data.

---

## 8. Case Collaboration Features

| Feature | What it does | Why it's needed |
|---|---|---|
| **Investigation records** | Operators log observations, actions taken, findings, evidence, and follow-up needs; stays part of case history | Preserves the "how we got here" so no one has to reconstruct it from memory later |
| **Tasks** | A case can contain multiple tasks, each with title, description, assignee, status, priority, due date, and completion info | Breaks investigation into trackable, assignable, accountable units of work |
| **Case communication** | Questions, answers, progress updates, evidence requests, resolution messages, follow-ups — all tied to the case; clearly distinguishes Requester-visible messages from internal notes | Keeps all case conversation in one traceable place instead of scattered emails/chats |
| **Internal notes** | Restricted to authorized internal users, never visible to the Requester, but part of the auditable internal history | Lets staff discuss sensitive or in-progress reasoning without exposing it to the Requester |
| **Resolution** | Operator submits what was done, what was found, supporting evidence, remaining limitations, and a final resolution message | Documents proof of work, not just a status flip to "Closed" |

### Resolution confirmation

```
Resolution Proposed → Requester notified → Requester confirms → Case Closed
Resolution Proposed → Requester rejects  → Case Reopened (history preserved) → Investigation continues
```

---

## 9. SLA, Risk & Escalation

### 9.1 SLA & risk monitoring

| What | Why |
|---|---|
| Continuously monitors case health for risk signals: inactivity, repeated follow-ups, multiple reassignments, missing information, rising complexity, approaching deadline, SLA % consumed, repeated reopening, similar cases taking unusually long | Catches at-risk cases *before* the deadline is missed, not after |
| Risk explanations are visible to authorized users, e.g. *"Case #CM-10482 — Resolution SLA 4h, elapsed 3h35m — Risk: HIGH. Reasons: no activity 55 min; reassigned twice; deadline approaching."* | Trust in an automated flag requires a visible reason, not just a red label |
| Monitoring loop (Scheduler, e.g. every 1 min): find active cases → check SLA → calculate remaining time → detect risk → create/update risk state → notify if needed → escalate if configured. Must be idempotent | Keeps SLA state fresh without needing a human to poll it, and without spamming duplicate alerts |

### 9.2 Escalation

| What | Why |
|---|---|
| Escalation conditions: SLA approaching/breached, serious issue, repeated unresolved complaint, multiple failed attempts, repeated reopening, operator-requested help, high-impact incident | Ensures the right level of authority gets involved automatically when a case outgrows normal handling |
| `SLA Risk Detected → Team Lead Alert → Still unresolved → Escalation Rule Triggered → Manager Notification` | Escalation should step up gradually, not jump straight to the top for every issue |
| Escalation stays human-controlled: a *recommendation* ("Consider escalating") is distinct from a *confirmed action* ("Case escalated to Team Lead"); the actual event is always recorded | Prevents the system from unilaterally pulling in management without a human decision, while still keeping a clear audit trail |

### 9.3 Responsibility matrix

| Activity | AI | Automation | Human |
|---|---|---|---|
| Classification | Recommend | Trigger | Review |
| Summary | Generate | Update trigger | Review |
| Missing information | Detect | Notify | Decide/send |
| Duplicate detection | Recommend | Trigger search | Decide |
| Assignment | Recommend | Surface suggestion | Decide |
| SLA monitoring | Analyze | Scheduler | Intervene |
| Escalation | Recommend | Trigger configured rule | Review/intervene as required |
| Communication | Draft | Generate on request | Edit/send |
| Audit logging | — | Automatic | — |
| Resolution | — | Notify | Responsible |
| Case closure | — | Support workflow | Confirm |

---

## 10. Notifications

| Role | Notified on (What) | Why this role needs it |
|---|---|---|
| Requester | Case created/updated, information requested, operator response, case resolved, case reopened | Keeps them informed without needing to log in and check status manually |
| Operator | New case assigned, requester response, new task, SLA warning, escalation, assignment change | Tells them exactly when action is needed on their queue |
| Team Lead | High-risk case, escalation, SLA breach, team workload issue, major incident | Flags what needs intervention before it becomes a bigger problem |
| Manager | Critical escalation, major incident, significant SLA issue, important operational pattern | Surfaces only what's genuinely organization-level, avoiding noise |

Notifications should be meaningful, not noisy. **V1 architecture:** Notification Service → in-app + email. **Future (not mandatory for V1):** push, SMS, WhatsApp, Teams, Slack.

---

## 11. Timeline & Audit

| Component | What it does | Why it's needed |
|---|---|---|
| **Timeline** | Records every important event chronologically (created → AI analysis → suggestion → assignment → info requested → response → task → SLA risk → notification → resolution → confirmation) | Becomes the single source of truth for a case's journey — no need to piece it together from memory or chat logs |
| **Audit log** | Automatically records case created/updated/assigned/reassigned, priority/severity/status changes, AI recommendation generated/accepted/rejected/overridden, info requested, requester response, task created/completed, SLA risk detected, escalation created, notification generated, resolution submitted/rejected, case reopened/closed — each with event type, actor, timestamp, previous/new value, related case, and source | Provides accountability and traceability for every meaningful action, which is essential for trust, compliance, and debugging automation |

---

## 12. Problem Management

| Concept | What it is | Why it matters |
|---|---|---|
| **Incident** | Something is currently wrong and needs restoring/resolving | This is the day-to-day unit of work most cases represent |
| **Problem** | An underlying recurring cause potentially responsible for multiple incidents (e.g. Incidents 101/102/103, all "VPN unavailable" → Problem: potential VPN infrastructure instability) | Fixing incidents one by one never stops the pattern — the problem is the actual thing to fix |
| **Recurring problem detection** | Repeated similar cases over time (e.g. weekly VPN failures) flag a potential underlying problem, escalatable into formal investigation | Surfaces systemic issues before a manager would otherwise notice the pattern manually |
| **Problem record** | Holds related incidents, suspected root cause, confirmed root cause, investigation, corrective action, preventive action, and status | Gives a structured place to track root-cause work separately from individual incident tickets |

---

## 13. AI Reliability & Trust

| Requirement | What it means | Why it matters |
|---|---|---|
| No false certainty | AI presents assumptions as assumptions, not facts — *"the available information suggests a VPN service issue may be involved"*, not *"the VPN server is down"*, unless confirmed | Prevents operators acting on an unverified guess as if it were established fact |
| Confidence, not certainty | Recommendations may carry a confidence score (e.g. "Network — 91%") | Communicates likelihood so humans can judge how much to trust a suggestion |
| Human override, always | Any AI recommendation can be changed by a human; system can record the original AI recommendation, human decision, override reason, timestamp, and user | Preserves accountability and creates data to evaluate AI performance over time |
| AI failure handling | If AI is unavailable, case creation, viewing, assignment, reassignment, status updates, investigation, tasks, communication, resolution, reopening, closure, notifications, audit logging, SLA monitoring, and escalation must all keep working; AI analysis retries later with a clear, non-alarming UI message | AI is an enhancement layer, not the foundation — an outage shouldn't stop real work |
| Output validation | AI responses for structured operations are never blindly trusted — backend validates the response; invalid output is rejected/retried/falls back | Business rules must stay enforced by the backend, not by whatever the AI happens to return |

---

## 14. Role Dashboards

| Dashboard | Answers | Shows |
|---|---|---|
| **Requester** | "What's happening with my cases?" | Active, waiting-for-info, recently updated, resolved, and reopened cases; notifications; a clear way to create a new case |
| **Operator** | "What needs my attention?" | New/assigned cases, high-priority, at-risk, waiting-for-info, escalated, related cases, pending tasks, SLA warnings, recent updates |
| **Team Lead** | "Where does my team need intervention?" | Team workload, cases by priority, at-risk, SLA breaches, escalations, unassigned cases, approaching deadlines, operator workload, resolution performance |
| **Manager** | "What's happening across the organization?" | Total/active/resolved cases, SLA performance, avg. resolution time, escalations, reopens, case/category trends, team performance, recurring problems, operational insights |

---

## 15. Search & Security

| Capability | What it does | Why it's needed |
|---|---|---|
| Search & filtering | By case number, title, requester, category, status, priority, severity, team, assignee, location, date, SLA state | Lets users find the exact case(s) they need without scrolling a full list |
| Natural-language search (future) | e.g. *"Show unresolved high-priority network cases"* | Faster, more intuitive querying once the core structured search is proven |
| Authorization | Strict role-based access — Requesters can't see others' cases or internal notes; Operators need permission for admin config; Team Leads see team-scoped cases; Managers see org-level analytics; Administrators manage configuration | Protects sensitive case data and keeps each role's view scoped to what they should see |
| Traceability | Important actions must remain traceable | Ties security directly back to the audit requirements in §11 |

---

## 16. Data Model

### Major entities

```
Organization · User · Role · Team · Category
Case · CaseAttachment · CaseMessage · InternalNote
CaseTask · Investigation · CaseAssignment · CaseRelation
AIAnalysis · AISuggestion · AISummary
SLAPolicy · CaseSLA · CaseRisk · Escalation
Notification · Resolution · AuditLog
Problem · ProblemIncidentRelation
```
*(schema may evolve during implementation)*

### Key fields (indicative, not exhaustive)

| Entity | Key fields |
|---|---|
| **Case** | `id, case_number, title, description, category, subcategory, severity, priority, status, requester_id, assigned_team_id, assigned_user_id, location, created_at, updated_at, resolved_at, closed_at` (AI-derived data stored separately, never overwriting core business data) |
| **AIAnalysis** | `case_id, suggested_category, suggested_subcategory, suggested_priority, suggested_severity, suggested_team, missing_information, recommended_next_action, related_cases, risk_information, confidence, model_information, created_at` |
| **AutomationEvent** | `id, case_id, event_type, trigger_type, status, started_at, completed_at, result, error_message, created_at` — types: `AI_ANALYSIS, SUMMARY_UPDATE, DUPLICATE_DETECTION, ASSIGNMENT_RECOMMENDATION, SLA_CHECK, RISK_DETECTION, ESCALATION_TRIGGER, NOTIFICATION, AUDIT_EVENT` |
| **Notification** | `id, user_id, case_id, type, title, message, read, created_at` — types: `CASE_ASSIGNED, REQUESTER_REPLIED, TASK_ASSIGNED, SLA_WARNING, SLA_BREACH, ESCALATION, RESOLUTION, CASE_REOPENED` |

---

## 17. Non-Functional Requirements

| Category | What | Why |
|---|---|---|
| **Security** | Authentication, authorization, RBAC, secure password handling, JWT/session security, input & file validation, API authorization, sensitive-data protection, audit logging, secure environment variables, rate limiting. **AI API credentials must never be exposed to frontend clients** | Protects the system and its users from unauthorized access and data exposure |
| **AI security** | Guards against prompt injection, sensitive-info exposure, unauthorized case context, excessive AI permissions, unsafe generated content, malicious uploads, cross-org data leakage; AI only receives data required for the specific operation | AI introduces new attack surfaces beyond standard app security — these need explicit handling |
| **Performance** | Pagination, DB indexes, efficient queries, lazy loading, sensible API response sizes, caching where justified, background processing only where required, AI request timeouts, retry limits | Keeps the system responsive without adding infrastructure that isn't yet justified |
| **Observability** | Visibility into application errors, API failures, AI failures, scheduler execution, automation failures, notification failures, database errors, response times, key workflow events; logs carry context without exposing sensitive data | Production issues must be diagnosable quickly, especially for automated/background processes |

### Failure scenarios & handling

| Failure | Expected behavior |
|---|---|
| AI API failure | Core application continues normally |
| Email failure | In-app notification still delivered; failure logged |
| Database failure | Controlled error returned; no state corruption |
| Scheduler failure | Next run detects and handles pending conditions |
| Duplicate automation | Idempotency prevents duplicate notifications/escalations |
| Invalid AI output | Rejected by backend validation |
| AI hallucination | Output stays labeled as recommendation until confirmed |

### Testing strategy

| Layer | What's tested |
|---|---|
| Backend | Unit, service, repository, controller, integration, security tests |
| Frontend | Component tests, form validation, API integration, role-based UI tests |
| AI | Valid output, invalid output, missing response, timeout, API failure, hallucinated assumptions, human override |
| Automation | Scheduler execution, SLA thresholds, duplicate prevention, escalation rules, notification rules, retry behavior, idempotency |

---

## 18. Delivery Roadmap (Phases 0–7)

Each phase is a runnable, vertical product increment (frontend + backend + database + API + tests + deployment) — not just a UI mockup. User stories for each phase are tabulated below; the full index with all 35 stories is in **Appendix A**.

### Phase 0 — Project Foundation
**Goal:** technical foundation up and running.
**What:** Spring Boot, Java 21, Maven, PostgreSQL, Spring Web, Spring Data JPA, validation, baseline security, exception handling, API structure, logging (backend); Flutter/React/Next.js (frontend); Git/GitHub, env & DB config, basic deployment (infra).
**Deliverable:** app starts; frontend talks to backend. *(No user stories — infrastructure only.)*

### Phase 1 — Core Case Management
**Goal:** a complete, functional (non-AI) case management system.
**What:** user registration/login, roles, organizations, teams, categories, case creation/listing/detail, assignment/reassignment, status management, lifecycle, Requester & Operator dashboards, basic timeline.

| ID | Role | User Story |
|---|---|---|
| US-1 | Requester | I want to create a case |
| US-2 | Requester | I want to view my cases |
| US-3 | Operator | I want to view assigned cases |
| US-4 | Operator | I want to update case status |
| US-5 | Team Lead | I want to view team cases |

### Phase 2 — Communication, Evidence & Investigation
**Goal:** real collaboration around a case.
**What:** attachments/screenshots/documents, case messages, internal notes, information requests & responses, `Waiting for Information` state, investigation records, tasks (create/assign/status/complete), basic notifications.

| ID | Role | User Story |
|---|---|---|
| US-6 | Operator | I want to request missing information |
| US-7 | Requester | I want to respond to an information request |
| US-8 | Operator | I want to communicate with the Requester |
| US-9 | Operator | I want to create investigation tasks |
| US-10 | Team Lead | I want to monitor pending work |

### Phase 3 — AI Case Intelligence
**Goal:** meaningful AI inside the workflow.
**What:** automatic AI analysis (category/subcategory/priority/severity), missing-info detection, suggested team, recommended next action, automatic summary, AI memory, explainable recommendations with confidence, accept/reject/override.
**Flow:** `Case Created → Automatic AI Analysis → Results Stored → Operator Notification`

| ID | Role | User Story |
|---|---|---|
| US-11 | Operator | I want AI to automatically analyze new cases |
| US-12 | Operator | I want an automatically maintained case summary |
| US-13 | Operator | I want AI to identify missing information |
| US-14 | Operator | I want to review and override AI recommendations |
| US-15 | Any user | I want AI failure to not prevent normal case management |

### Phase 4 — Related Cases & Smart Operations
**Goal:** intelligence across multiple cases.
**What:** duplicate/related-case detection, case linking, duplicate marking, master/common incident, smart assignment recommendation (team + operator), workload-aware recommendations, impact detection.
**Flow:** `New Case → Search Historical Cases → Similarity Analysis → Related Cases → Operator Review`

| ID | Role | User Story |
|---|---|---|
| US-16 | Operator | I want AI to identify potential duplicate cases |
| US-17 | Operator | I want to link related cases |
| US-18 | Team Lead | I want to identify common incidents |
| US-19 | Operator | I want a smart assignment recommendation |
| US-20 | Team Lead | I want recommendations to consider workload |

### Phase 5 — SLA, Risk & Escalation Automation
**Goal:** proactive, not reactive.
**What:** SLA policies (response/resolution), SLA countdown & percentage, at-risk state, automatic SLA checks, risk detection & explanation, SLA warning/breach notifications, escalation rules & auto-triggers, Team Lead/Manager alerts, major-incident detection.
**Flow:** `Scheduler → Periodic SLA Check → Risk Evaluation → Notification → Escalation Rule → Escalation`

| ID | Role | User Story |
|---|---|---|
| US-21 | Operator | I want to see remaining SLA time |
| US-22 | Team Lead | I want to know which cases are at risk |
| US-23 | Team Lead | I want automatic alerts when cases approach deadlines |
| US-24 | Manager | I want to know about SLA breaches |
| US-25 | System | I want configured escalation conditions to trigger automatically |

### Phase 6 — Resolution, Problem Management & AI Copilot
**Goal:** move from incident-processing to complete intelligent case management.
**What:** resolution workflow & evidence, requester confirmation/rejection, reopening, root-cause assistance, recurring-problem detection, problem records, incident-problem relationships, AI knowledge base, AI Operator Copilot, communication drafting, escalation summaries, next-action suggestions.

| ID | Role | User Story |
|---|---|---|
| US-26 | Operator | I want to submit a resolution |
| US-27 | Requester | I want to confirm or reject a resolution |
| US-28 | Manager | I want recurring problems identified automatically |
| US-29 | Operator | I want AI assistance while investigating a case |
| US-30 | Operator | I want AI to draft professional communication |

### Phase 7 — Analytics, Audit & Production Readiness
**Goal:** complete production-quality product.
**What:** manager analytics, team performance, case/SLA/resolution-time/reopen-rate/escalation/recurring-problem analytics, operational insights, complete audit history, advanced search & filtering, NL-search foundation, error/empty/loading states, security hardening, API docs, logging, monitoring, deployment, backup strategy, performance tuning.

| ID | Role | User Story |
|---|---|---|
| US-31 | Manager | I want organization-wide analytics |
| US-32 | Manager | I want to identify operational patterns |
| US-33 | Administrator | I want complete audit history |
| US-34 | Operator | I want advanced case search |
| US-35 | Administrator | I want a secure production-ready system |

---

## 19. MVP Definition

| Area | Scope |
|---|---|
| **User Management** | Registration, login, RBAC, profile |
| **Requester** | Create case, upload evidence, view cases, respond to questions, receive notifications, confirm/reject resolution, reopen cases |
| **Operator** | Assigned cases, AI analysis & summary, missing-info detection, related cases, assignment, communication, internal notes, tasks, investigation, SLA status, risk info, resolution |
| **Team Lead** | Team cases, workload, at-risk cases, escalations, SLA breaches, intervention |
| **Manager** | Operational dashboard, trends, SLA analytics, escalations, operational insights |
| **AI** | Case analysis, classification, priority/severity recommendation, summary, missing information, related/duplicate detection, assignment recommendation, next-action recommendation, risk analysis, communication drafts, operational insights |
| **Automation** | Automatic AI analysis, automatic summary updates, automatic missing-info detection, automatic related-case detection, automatic SLA checking, automatic risk detection, configured escalation automation, automated notifications, automatic timeline, automatic audit logging |

---

## 20. Demo Environment & Scenario

**Demo accounts:** Demo Requester, Demo Operator, Demo Team Lead, Demo Manager, Demo Administrator — pre-loaded with realistic sample data so an evaluator can experience the full workflow.

### Scenario: IT VPN Incident (end to end)

| Step | Event |
|---|---|
| 1 | Requester logs in, creates a VPN incident with description + screenshot, submits it |
| 2 | Automatic AI analysis runs: category = Network, priority = High, missing detail = error message, similar incidents found, suggested team = Network Support |
| 3 | Operator reviews the AI analysis, accepts the team recommendation, sends an AI-generated information request |
| 4 | Requester provides the requested information; Operator is notified; AI summary updates |
| 5 | Operator creates investigation tasks; SLA timer starts |
| 6 | Scheduler detects an approaching SLA, marks the case **At Risk**; Team Lead is notified, reviews, reassigns a task if needed |
| 7 | Operator completes the investigation, submits a resolution; AI drafts the resolution message; Operator reviews and sends it |
| 8 | Requester confirms the resolution; case closes |
| 9 | Manager reviews case analytics; system flags whether similar incidents are becoming a recurring pattern |

This demonstrates the product as one connected system, not a set of disconnected screens.

---

## 21. Success Criteria

| # | The product should demonstrably... |
|---|---|
| 1 | Capture a real-world problem and preserve its complete history |
| 2 | Support multiple roles with distinct experiences |
| 3 | Use AI meaningfully inside the actual workflow |
| 4 | Automatically analyze new cases and maintain useful summaries |
| 5 | Detect missing information and related/duplicate cases |
| 6 | Recommend appropriate assignments |
| 7 | Monitor SLA conditions and detect potential risk |
| 8 | Trigger configured escalation workflows |
| 9 | Generate useful communication drafts and notify relevant users |
| 10 | Maintain a complete timeline and trustworthy audit history |
| 11 | Let humans override AI, and keep functioning if AI is unavailable |
| 12 | Confirm whether a resolution actually worked |
| 13 | Give managers operational insight and detect recurring problems |
| 14 | Provide a foundation for formal problem management |

---

## 22. Future Opportunities & Architecture Evolution

| Area | Beyond V1 (not mandatory) |
|---|---|
| Input & understanding | Voice-based reporting, advanced document understanding, semantic search |
| Analytics | Natural-language analytics, predictive workload management |
| AI depth | Org-specific AI knowledge / advanced RAG, advanced incident correlation, AI agents & tool calling, automated investigation checklists |
| Channels | SMS, WhatsApp, Teams, Slack, mobile field operations |
| Platform | Multi-org SaaS, subscription/billing, advanced reporting |
| Infrastructure | Advanced workflow engine, message queues, dedicated background workers, n8n integrations where genuinely useful |

**When to introduce a worker/queue/n8n:** only on demonstrated need — not because a feature is labeled "automation."

```
Frontend → Spring Boot API → Message Queue → [AI Worker | Notification Worker | Document Worker | Analytics Worker]
```

n8n may later help with external integrations (email, Slack, Teams, external systems, reporting) but is not required for V1.

> **Principle:** Do not add infrastructure merely because a feature is called "automation." Add it when workload, reliability, scalability, or integration requirements justify it.

---

## 23. Final Product Definition

Nexus is an intelligent case-management platform connecting:

> **People → Cases → Evidence → Communication → Tasks → AI → Automation → Deadlines → Risk → Escalation → Resolution → Operational Learning**

It transforms case management from *"Create a ticket and wait"* into *"Understand the situation, know who owns it, know what's next, automate repetitive work, catch risk early, escalate when required, and confirm the problem is actually resolved."*

### Final architecture

```
                         USERS
            ┌──────────────┼──────────────┐
       Requester       Operator      Management
            └──────────────┼──────────────┘
                           ▼
                    FRONTEND / MOBILE
                           ▼
                    SPRING BOOT API
        ┌──────────────────┼───────────────────┐
        ▼                  ▼                   ▼
 Case Management       AI Services       Automation
        │                  │              (Scheduler + Events)
        └──────────────────┼───────────────────┘
                           ▼
                    PostgreSQL / Supabase
             ┌─────────────┼──────────────┐
          Cases          Audit        Analytics
```

### Defining principles

| Layer | Role |
|---|---|
| System | Manages the case |
| AI | Helps people understand and move the case forward |
| Automation | Handles repetitive, predictable workflow activity |
| Humans | Remain responsible for important decisions |
| Core application | Stays functional even when AI is temporarily unavailable |

> **Don't add infrastructure just because a feature is automated** — start with Spring Boot services and scheduled jobs; introduce workers, queues, or workflow platforms only when real workload or integration needs justify it.
>
> **Goal:** a complete, intelligent, traceable, production-ready case-management system — not a collection of AI screens.

---

## 24. Appendix A — Master User Story Index

| ID | Phase | Role | User Story | Linked Requirement (§) |
|---|---|---|---|---|
| US-1 | 1 | Requester | Create a case | §5 Case Lifecycle |
| US-2 | 1 | Requester | View my cases | §14 Requester Dashboard |
| US-3 | 1 | Operator | View assigned cases | §14 Operator Dashboard |
| US-4 | 1 | Operator | Update case status | §5 Case Lifecycle |
| US-5 | 1 | Team Lead | View team cases | §14 Team Lead Dashboard |
| US-6 | 2 | Operator | Request missing information | §7.3 Missing Information Detection |
| US-7 | 2 | Requester | Respond to an information request | §8 Case Communication |
| US-8 | 2 | Operator | Communicate with the Requester | §8 Case Communication |
| US-9 | 2 | Operator | Create investigation tasks | §8 Tasks |
| US-10 | 2 | Team Lead | Monitor pending work | §14 Team Lead Dashboard |
| US-11 | 3 | Operator | AI automatically analyzes new cases | §7.1 Automatic Case Analysis |
| US-12 | 3 | Operator | Automatically maintained case summary | §7.2 Automatic Case Summarization |
| US-13 | 3 | Operator | AI identifies missing information | §7.3 Missing Information Detection |
| US-14 | 3 | Operator | Review and override AI recommendations | §13 Human Override |
| US-15 | 3 | Any user | AI failure doesn't block case management | §13 AI Failure Handling |
| US-16 | 4 | Operator | AI identifies potential duplicate cases | §7.4 Related/Duplicate Detection |
| US-17 | 4 | Operator | Link related cases | §7.4 Related/Duplicate Detection |
| US-18 | 4 | Team Lead | Identify common incidents | §7.4 Master Incidents |
| US-19 | 4 | Operator | Smart assignment recommendation | §7.5 Smart Assignment Recommendation |
| US-20 | 4 | Team Lead | Recommendations consider workload | §7.5 Smart Assignment Recommendation |
| US-21 | 5 | Operator | See remaining SLA time | §9.1 SLA & Risk Monitoring |
| US-22 | 5 | Team Lead | Know which cases are at risk | §9.1 SLA & Risk Monitoring |
| US-23 | 5 | Team Lead | Automatic alerts on approaching deadlines | §9.1 SLA & Risk Monitoring |
| US-24 | 5 | Manager | Know about SLA breaches | §10 Notifications |
| US-25 | 5 | System | Configured escalation conditions auto-trigger | §9.2 Escalation |
| US-26 | 6 | Operator | Submit a resolution | §8 Resolution |
| US-27 | 6 | Requester | Confirm or reject a resolution | §8 Resolution Confirmation |
| US-28 | 6 | Manager | Recurring problems identified automatically | §12 Problem Management |
| US-29 | 6 | Operator | AI assistance while investigating a case | §7.7 AI Operator Copilot |
| US-30 | 6 | Operator | AI drafts professional communication | §7.6 AI-Generated Communication |
| US-31 | 7 | Manager | Organization-wide analytics | §14 Manager Dashboard |
| US-32 | 7 | Manager | Identify operational patterns | §7.8 Operational Insights |
| US-33 | 7 | Administrator | Complete audit history | §11 Timeline & Audit |
| US-34 | 7 | Operator | Advanced case search | §15 Search & Security |
| US-35 | 7 | Administrator | Secure production-ready system | §17 Non-Functional Requirements |
