# Nexus — Database Schema
### *AI-Powered Case Management Platform*

> **Source:** Extracted from `Nexus_SRS.md` §6 (SRS v1.1)
> **v1.1 note:** Added `escalation_rules` config table (§6.5) so `/admin/escalation-rules` has a backing store; `escalations` now references the rule that fired it.

All tables use `UUID` primary keys and `created_at`/`updated_at` timestamps unless noted. Schema is grouped by domain; exact SQL will live in Flyway migration files.

---

## 6.1 Organization & Identity

**organizations**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| name | VARCHAR | |
| created_at | TIMESTAMP | |

**users**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| organization_id | UUID | FK → organizations |
| name | VARCHAR | |
| email | VARCHAR | UNIQUE |
| password_hash | VARCHAR | |
| status | ENUM | ACTIVE, INACTIVE |
| created_at | TIMESTAMP | |

**roles**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| name | ENUM | REQUESTER, OPERATOR, TEAM_LEAD, MANAGER, ADMIN |

**user_roles**
| Column | Type | Notes |
|---|---|---|
| user_id | UUID | FK → users |
| role_id | UUID | FK → roles |

**teams**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| organization_id | UUID | FK → organizations |
| name | VARCHAR | |
| lead_user_id | UUID | FK → users |

**team_members**
| Column | Type | Notes |
|---|---|---|
| team_id | UUID | FK → teams |
| user_id | UUID | FK → users |

**categories**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| organization_id | UUID | FK → organizations |
| name | VARCHAR | |
| parent_category_id | UUID | Self-referencing FK, nullable (subcategory) |
| default_team_id | UUID | FK → teams, nullable |

---

## 6.2 Case Core

**cases**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_number | VARCHAR | UNIQUE, human-readable |
| title | VARCHAR | |
| description | TEXT | |
| category_id | UUID | FK → categories |
| subcategory_id | UUID | FK → categories, nullable |
| severity | ENUM | LOW, MEDIUM, HIGH, CRITICAL |
| priority | ENUM | LOW, MEDIUM, HIGH, URGENT |
| status | ENUM | REPORTED, UNDERSTOOD, ASSIGNED, INVESTIGATING, WAITING_FOR_INFO, AT_RISK, ESCALATED, RESOLUTION_PROPOSED, CLOSED, REOPENED, DUPLICATE, RELATED, CANCELLED |
| requester_id | UUID | FK → users |
| assigned_team_id | UUID | FK → teams, nullable |
| assigned_user_id | UUID | FK → users, nullable |
| location | VARCHAR | nullable |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |
| resolved_at | TIMESTAMP | nullable |
| closed_at | TIMESTAMP | nullable |

*Note: this table stores only confirmed/business data — AI-derived values are never written here directly.*

**case_attachments**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| uploaded_by | UUID | FK → users |
| storage_path | VARCHAR | Supabase Storage path |
| file_name | VARCHAR | |
| file_type | VARCHAR | |
| created_at | TIMESTAMP | |

**case_assignments**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| assigned_user_id | UUID | FK → users |
| assigned_team_id | UUID | FK → teams |
| assigned_by | UUID | FK → users |
| reason | TEXT | nullable |
| created_at | TIMESTAMP | |

**case_relations**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| related_case_id | UUID | FK → cases |
| relation_type | ENUM | DUPLICATE, RELATED, MASTER_INCIDENT |
| linked_by | UUID | FK → users, nullable (AI-suggested if null) |
| created_at | TIMESTAMP | |

---

## 6.3 Collaboration

**case_messages**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| sender_id | UUID | FK → users |
| message_type | ENUM | QUESTION, ANSWER, UPDATE, EVIDENCE_REQUEST, RESOLUTION_MESSAGE, FOLLOW_UP |
| content | TEXT | |
| visible_to_requester | BOOLEAN | |
| created_at | TIMESTAMP | |

**internal_notes**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| author_id | UUID | FK → users |
| content | TEXT | |
| created_at | TIMESTAMP | |

**case_tasks**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| title | VARCHAR | |
| description | TEXT | |
| assignee_id | UUID | FK → users |
| status | ENUM | PENDING, IN_PROGRESS, COMPLETED, CANCELLED |
| priority | ENUM | LOW, MEDIUM, HIGH |
| due_date | TIMESTAMP | nullable |
| completed_at | TIMESTAMP | nullable |
| created_at | TIMESTAMP | |

**investigations**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| operator_id | UUID | FK → users |
| observation | TEXT | |
| action_taken | TEXT | |
| finding | TEXT | |
| evidence_ref | VARCHAR | nullable, links to case_attachments |
| follow_up_needed | TEXT | nullable |
| created_at | TIMESTAMP | |

---

## 6.4 AI

**ai_analysis**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| suggested_category_id | UUID | FK → categories, nullable |
| suggested_subcategory_id | UUID | FK → categories, nullable |
| suggested_priority | ENUM | nullable |
| suggested_severity | ENUM | nullable |
| suggested_team_id | UUID | FK → teams, nullable |
| missing_information | JSONB | list of detected missing fields |
| recommended_next_action | TEXT | |
| related_cases | JSONB | array of case_ids with similarity scores |
| risk_information | JSONB | |
| confidence | DECIMAL | 0.00–1.00 |
| model_information | VARCHAR | provider + model name used |
| created_at | TIMESTAMP | |

**ai_suggestions**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| suggestion_type | ENUM | CATEGORY, PRIORITY, SEVERITY, ASSIGNMENT, DUPLICATE, ROOT_CAUSE |
| suggested_value | JSONB | |
| status | ENUM | PENDING, ACCEPTED, MODIFIED, REJECTED |
| decided_by | UUID | FK → users, nullable |
| override_reason | TEXT | nullable |
| decided_at | TIMESTAMP | nullable |
| created_at | TIMESTAMP | |

**ai_summaries**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| summary_text | TEXT | |
| version | INT | incremented on each regeneration |
| created_at | TIMESTAMP | |

---

## 6.5 SLA, Risk & Escalation

**sla_policies**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| organization_id | UUID | FK → organizations |
| category_id | UUID | FK → categories, nullable |
| priority | ENUM | |
| response_time_minutes | INT | |
| resolution_time_minutes | INT | |

**case_sla**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| sla_policy_id | UUID | FK → sla_policies |
| response_deadline | TIMESTAMP | |
| resolution_deadline | TIMESTAMP | |
| status | ENUM | ON_TRACK, AT_RISK, BREACHED, MET |

*Indexed on `(status, resolution_deadline)` — scanned every minute by the SLA scheduler.*

**case_risk**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| risk_level | ENUM | LOW, MEDIUM, HIGH |
| reasons | JSONB | list of contributing risk factors |
| detected_at | TIMESTAMP | |

**escalation_rules**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| organization_id | UUID | FK → organizations |
| name | VARCHAR | e.g. "SLA breach → Manager" |
| condition_type | ENUM | SLA_APPROACHING, SLA_BREACHED, REPEATED_REOPENING, MULTIPLE_FAILED_ATTEMPTS, OPERATOR_REQUESTED, HIGH_IMPACT_INCIDENT |
| condition_config | JSONB | e.g. `{"threshold_percent": 90}`, `{"reopen_count": 2}` |
| escalation_level | ENUM | TEAM_LEAD, MANAGER |
| category_id | UUID | FK → categories, nullable (applies org-wide if null) |
| is_active | BOOLEAN | default true |
| created_by | UUID | FK → users |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

*Admin-configurable via `/admin/escalation-rules`. Evaluated by the Escalation Service (scheduled + event-based) against active cases; a match creates an `escalations` record referencing the rule that fired.*

**escalations**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| escalation_rule_id | UUID | FK → escalation_rules, nullable (nullable when manually/user-triggered rather than rule-driven) |
| escalation_level | ENUM | TEAM_LEAD, MANAGER |
| reason | TEXT | |
| status | ENUM | RECOMMENDED, CONFIRMED |
| triggered_by | ENUM | SYSTEM, USER |
| confirmed_by | UUID | FK → users, nullable |
| created_at | TIMESTAMP | |

---

## 6.6 Notifications, Resolution

**notifications**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| user_id | UUID | FK → users |
| case_id | UUID | FK → cases, nullable |
| type | ENUM | CASE_ASSIGNED, REQUESTER_REPLIED, TASK_ASSIGNED, SLA_WARNING, SLA_BREACH, ESCALATION, RESOLUTION, CASE_REOPENED |
| title | VARCHAR | |
| message | TEXT | |
| read | BOOLEAN | default false |
| created_at | TIMESTAMP | |

**resolutions**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| submitted_by | UUID | FK → users |
| what_was_done | TEXT | |
| findings | TEXT | |
| evidence_ref | VARCHAR | nullable |
| limitations | TEXT | nullable |
| resolution_message | TEXT | |
| requester_decision | ENUM | PENDING, CONFIRMED, REJECTED |
| decided_at | TIMESTAMP | nullable |
| created_at | TIMESTAMP | |

---

## 6.7 Governance

**audit_logs**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| entity_type | VARCHAR | e.g. CASE, TASK, ESCALATION |
| entity_id | UUID | |
| actor_id | UUID | FK → users, nullable (system actions) |
| action | VARCHAR | e.g. CREATED, STATUS_CHANGED, AI_OVERRIDDEN |
| old_value | JSONB | nullable |
| new_value | JSONB | nullable |
| source | ENUM | USER, SYSTEM, AI |
| created_at | TIMESTAMP | |

*Append-only — no UPDATE/DELETE permitted at application level.*

**automation_events**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| case_id | UUID | FK → cases |
| event_type | ENUM | AI_ANALYSIS, SUMMARY_UPDATE, DUPLICATE_DETECTION, ASSIGNMENT_RECOMMENDATION, SLA_CHECK, RISK_DETECTION, ESCALATION_TRIGGER, NOTIFICATION, AUDIT_EVENT |
| trigger_type | ENUM | EVENT, SCHEDULED, USER, AI |
| status | ENUM | PENDING, SUCCESS, FAILED |
| started_at | TIMESTAMP | |
| completed_at | TIMESTAMP | nullable |
| result | JSONB | nullable |
| error_message | TEXT | nullable |
| created_at | TIMESTAMP | |

*Unique constraint on `(case_id, event_type, trigger_window)` for idempotency.*

**problems**
| Column | Type | Notes |
|---|---|---|
| id | UUID | PK |
| title | VARCHAR | |
| suspected_root_cause | TEXT | nullable |
| confirmed_root_cause | TEXT | nullable |
| investigation_notes | TEXT | nullable |
| corrective_action | TEXT | nullable |
| preventive_action | TEXT | nullable |
| status | ENUM | OPEN, INVESTIGATING, RESOLVED |
| created_at | TIMESTAMP | |

**problem_incident_relations**
| Column | Type | Notes |
|---|---|---|
| problem_id | UUID | FK → problems |
| case_id | UUID | FK → cases |

---

## 6.8 Entity Relationship Summary

```
organizations ──< users ──< user_roles >── roles
organizations ──< teams ──< team_members >── users
organizations ──< categories (self-referencing for subcategories)

users ──< cases (as requester)
teams ──< cases (as assigned_team)
users ──< cases (as assigned_user)

cases ──< case_attachments
cases ──< case_assignments
cases ──< case_relations >── cases (self-referencing)
cases ──< case_messages
cases ──< internal_notes
cases ──< case_tasks
cases ──< investigations
cases ──< ai_analysis
cases ──< ai_suggestions
cases ──< ai_summaries
cases ──< case_sla >── sla_policies
cases ──< case_risk
organizations ──< escalation_rules ──< escalations
cases ──< escalations
cases ──< notifications
cases ──< resolutions
cases ──< automation_events

problems ──< problem_incident_relations >── cases
```
