
# Nexus — API Contract
### *AI-Powered Case Management Platform*

> **Source:** Extracted from `Nexus_SRS.md` §7 (SRS v1.1)

**Base URL:** `/api/v1`
All endpoints (except auth) require `Authorization: Bearer <JWT>`. Responses follow a consistent envelope: `{ success, data, error, meta }`.

---

## 7.1 Auth

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| POST | `/auth/register` | Register a new user | Public (or Admin-invited, per org policy) |
| POST | `/auth/login` | Login, returns access + refresh token | Public |
| POST | `/auth/refresh` | Refresh access token | Authenticated |
| POST | `/auth/logout` | Invalidate refresh token | Authenticated |
| GET | `/auth/me` | Get current user profile | Authenticated |

---

## 7.2 Organization / Admin Config

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| GET/POST | `/admin/teams` | List/create teams | Admin |
| PUT/DELETE | `/admin/teams/{id}` | Update/delete team | Admin |
| GET/POST | `/admin/categories` | List/create categories | Admin |
| PUT/DELETE | `/admin/categories/{id}` | Update/delete category | Admin |
| GET/POST | `/admin/sla-policies` | List/create SLA policies | Admin |
| PUT/DELETE | `/admin/sla-policies/{id}` | Update/delete SLA policy | Admin |
| GET/POST | `/admin/escalation-rules` | List/create escalation rules | Admin |
| PUT/DELETE | `/admin/escalation-rules/{id}` | Update/delete (or deactivate) an escalation rule | Admin |
| GET/POST | `/admin/users` | List/invite users | Admin |
| PUT | `/admin/users/{id}/role` | Change user role | Admin |

---

## 7.3 Cases

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| POST | `/cases` | Create a case | Requester |
| GET | `/cases` | List cases (filtered/paginated by role scope) | All |
| GET | `/cases/{id}` | Get case detail (with timeline) | Scoped |
| PUT | `/cases/{id}` | Update case fields | Operator, Team Lead |
| PUT | `/cases/{id}/status` | Change case status | Operator, Team Lead |
| PUT | `/cases/{id}/assign` | Assign/reassign case | Operator, Team Lead |
| POST | `/cases/{id}/attachments` | Upload attachment | Requester, Operator |
| GET | `/cases/{id}/timeline` | Get full timeline | Scoped |
| POST | `/cases/{id}/relations` | Link related/duplicate case | Operator |
| GET | `/cases/search` | Advanced search/filter | Operator, Team Lead, Manager |

---

## 7.4 Collaboration

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| POST | `/cases/{id}/messages` | Send message / info request / update | Operator, Requester |
| GET | `/cases/{id}/messages` | List messages (visibility-filtered) | Scoped |
| POST | `/cases/{id}/notes` | Add internal note | Operator, Team Lead |
| GET | `/cases/{id}/notes` | List internal notes | Operator, Team Lead |
| POST | `/cases/{id}/tasks` | Create task | Operator |
| GET | `/cases/{id}/tasks` | List tasks | Scoped |
| PUT | `/tasks/{id}` | Update task status | Assignee, Operator |
| POST | `/cases/{id}/investigations` | Log investigation record | Operator |
| GET | `/cases/{id}/investigations` | List investigation records | Operator, Team Lead |

---

## 7.5 AI

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| POST | `/cases/{id}/ai/analyze` | Trigger/re-trigger AI analysis | Operator (also auto on case creation) |
| GET | `/cases/{id}/ai/analysis` | Get latest AI analysis | Scoped |
| GET | `/cases/{id}/ai/summary` | Get current AI summary | Scoped |
| POST | `/cases/{id}/ai/summary/regenerate` | Regenerate summary | Operator |
| PUT | `/ai/suggestions/{id}` | Accept / Modify / Reject a suggestion | Operator |
| GET | `/cases/{id}/ai/duplicates` | Get related/duplicate suggestions | Operator |
| GET | `/cases/{id}/ai/assignment-recommendation` | Get smart assignment suggestion | Operator, Team Lead |
| POST | `/cases/{id}/ai/draft-communication` | Generate a communication draft | Operator |
| POST | `/cases/{id}/ai/copilot` | Ask the case-scoped AI copilot a question | Operator |

---

## 7.6 SLA & Escalation

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| GET | `/cases/{id}/sla` | Get SLA status for a case | Scoped |
| GET | `/sla/at-risk` | List at-risk cases | Operator, Team Lead |
| GET | `/sla/breached` | List breached cases | Team Lead, Manager |
| POST | `/cases/{id}/escalate` | Confirm an escalation | Operator, Team Lead |
| GET | `/escalations` | List escalations (scoped) | Team Lead, Manager |

---

## 7.7 Resolution

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| POST | `/cases/{id}/resolution` | Submit a resolution | Operator |
| PUT | `/cases/{id}/resolution/confirm` | Confirm resolution → close case | Requester |
| PUT | `/cases/{id}/resolution/reject` | Reject resolution → reopen case | Requester |

---

## 7.8 Problem Management

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| GET | `/problems` | List problems (recurring issues) | Manager, Team Lead |
| GET | `/problems/{id}` | Get problem detail with linked incidents | Manager, Team Lead |
| POST | `/problems` | Create a problem record | Manager |
| PUT | `/problems/{id}` | Update problem (root cause, actions, status) | Manager |
| POST | `/problems/{id}/incidents` | Link a case as an incident of this problem | Manager, Team Lead |

---

## 7.9 Notifications

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| GET | `/notifications` | List current user's notifications | All |
| PUT | `/notifications/{id}/read` | Mark as read | All |
| PUT | `/notifications/read-all` | Mark all as read | All |

---

## 7.10 Analytics & Dashboards

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| GET | `/dashboard/requester` | Requester dashboard data | Requester |
| GET | `/dashboard/operator` | Operator dashboard data | Operator |
| GET | `/dashboard/team-lead` | Team Lead dashboard data | Team Lead |
| GET | `/dashboard/manager` | Manager dashboard data | Manager |
| GET | `/analytics/trends` | Case/category trends | Manager |
| GET | `/analytics/sla-performance` | SLA performance metrics | Manager |
| GET | `/analytics/recurring-problems` | Recurring problem patterns | Manager |

---

## 7.11 Audit (Admin)

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| GET | `/audit-logs` | Query audit history (filterable) | Administrator |
| GET | `/audit-logs/case/{id}` | Full audit trail for one case | Administrator, Team Lead |
