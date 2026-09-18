# Nexus — Master Stitch Screen Specification & Prompt Catalog
### *AI-Powered Case Management Platform (Flutter Web, Desktop, Tablet, Mobile)*

> **Version:** 3.0 (Spacious Luxury & Pastel sRGB Dual-Theme Edition)  
> **Target Tool:** Google Stitch MCP / AI UI Generator  
> **Backend Base URL:** `/api/v1` (Spring Boot 3.3.4 + Java 21)  
> **Aesthetic Philosophy:** *Refined Enterprise Minimalism* — Breathable negative space, spacious 8pt grid, soft sRGB pastel accents, dual Light & Dark themes, and subtle, native AI intelligence without loud gimmicks or cluttered slopes.

---

## 🎨 1. Global Design System & Spacing Tokens

### 1.1 Dual-Theme Color Palette (sRGB Pastel & Slate Harmony)

The palette completely removes heavy/saturated green dominance and adopts a sophisticated, calibrated **sRGB Pastel & Slate** system with complete support for **Light Mode** and **Dark Mode**.

| Token Name | Light Mode (Crisp Slate) | Dark Mode (Deep Obsidian) | Usage / Semantics |
|---|---|---|---|
| **Canvas / Background** | `#FAFAFC` (Pure soft mist) | `#0D1117` (Deep Obsidian) | Root scaffold background |
| **Surface / Primary Card** | `#FFFFFF` (Crisp white) | `#161B22` (Subtle dark surface) | Cards, panels, sidebars |
| **Surface Elevated / Modal** | `#F8FAFC` (Soft pearl) | `#21262D` (Elevated dark card) | Modals, dropdowns, popovers |
| **Border / Divider** | `#E2E8F0` (1px hairline) | `#30363D` (1px subtle border) | Clean containment, no heavy strokes |
| **Text Primary** | `#0F172A` (Slate 900) | `#F0F6FC` (Slate 50) | Main headings, primary labels |
| **Text Secondary** | `#475569` (Slate 600) | `#8B949E` (Slate 400) | Timestamps, secondary metadata |
| **Text Muted** | `#94A3B8` (Slate 400) | `#6E7681` (Slate 500) | Placeholders, disabled states |
| **Accent Primary (Periwinkle)** | `#6366F1` (Soft Indigo) | `#818CF8` (Pastel Periwinkle) | Primary actions, brand mark |
| **Accent Tint (Pastel Ice)** | `#EEF2FF` (Periwinkle Wash) | `#1E1E38` (Muted Indigo tint) | Active nav item, selected chips |
| **Accent Sky (Cool Mist)** | `#0284C7` (Sky Mist) | `#38BDF8` (Pastel Sky) | Information tags, active filters |
| **AI Intelligence (Pastel Lilac)**| `#9333EA` (Soft Amethyst) | `#C084FC` (Pastel Lilac) | Subtle AI suggestions, Copilot |
| **AI Background Tint** | `#FAF5FF` (Lilac Whisper) | `#251833` (Deep Lilac Wash) | AI card surface background |
| **Status: Reported** | `#64748B` / Tint `#F1F5F9` | `#94A3B8` / Tint `#1E293B` | Soft Slate pill |
| **Status: Understood / Triage** | `#0284C7` / Tint `#E0F2FE` | `#38BDF8` / Tint `#0C2D48` | Pastel Sky Blue pill |
| **Status: Assigned** | `#6366F1` / Tint `#EEF2FF` | `#818CF8` / Tint `#1E1E38` | Pastel Periwinkle pill |
| **Status: Investigating** | `#D97706` / Tint `#FEF3C7` | `#FBBF24` / Tint `#382808` | Pastel Butter Honey pill |
| **Status: Waiting for Info** | `#EA580C` / Tint `#FFEDD5` | `#FB923C` / Tint `#3D1B06` | Pastel Apricot pill |
| **Status: Resolution Proposed** | `#7C3AED` / Tint `#F3E8FF` | `#A78BFA` / Tint `#2E1065` | Pastel Iris pill |
| **Status: Closed / Confirmed** | `#0D9488` / Tint `#CCFBF1` | `#2DD4BF` / Tint `#0D3331` | Soft Sage / Pastel Teal pill |
| **Status: Breached / High Risk** | `#E11D48` / Tint `#FFE4E6` | `#FB7185` / Tint `#3D0C15` | Pastel Rose / Coral Blush pill |

---

### 1.2 Spacing & Advancement View (Anti-Clutter System)

To eliminate visual congestion and provide a world-class luxury feel:

1. **Airy Margins & Gutters:**
   - Desktop Screen Gutters: `32px` padding around all primary workspaces.
   - Mobile Screen Gutters: `16px` padding with `12px` card vertical gaps.
   - Internal Card Padding: `24px` on Desktop, `16px` on Mobile (never cramped `8px`).
2. **Generous Line Height & Typography Hierarchy:**
   - Headings: `Outfit` / `Inter` Medium (Letter spacing `-0.02em`, line-height `1.3`).
   - Body: `Inter` Regular (`14px` size with comfortable `22px` line-height).
   - Metadata / Tags: `12px` with uppercase letter spacing `+0.04em`.
3. **Discrete, Integrated AI Experience (No Gimmicky Slopes):**
   - **No loud neon slopes or flashy banners.** AI assistance is presented as a sleek, organic micro-card with a subtle `1px` soft lilac border (`#C084FC33`), a clean `✨ AI Suggestion` header chip, and explicit `[Accept]`, `[Modify]`, `[Dismiss]` ghost buttons.
   - Confirmed business data sits in clean white/dark cards with crisp contrast.

---

### 1.3 Multi-Device Responsive Breakpoint Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          RESPONSIVE BREAKPOINT GRID                         │
├──────────────────────────┬──────────────────────────┬───────────────────────┤
│   MOBILE (< 600px)       │   TABLET (600 - 1199px)  │  DESKTOP (>= 1200px)  │
│   • Target: 390px        │   • Target: 834px        │  • Target: 1440px     │
├──────────────────────────┼──────────────────────────┼───────────────────────┤
│ • BottomNavigationBar    │ • Collapsible NavRail    │ • Spacious Sidebar    │
│ • Single Column Stack    │ • 2-Column Adaptive Split│ • 3-Column Studio     │
│ • Modal Bottom Sheets    │ • Side Drawers           │ • Inline Docked Panels│
│ • Touch-Friendly Cards   │ • Compact Data Tables    │ • Expansive Data Grid │
│ • 16px Padding Gutters   │ • 24px Padding Gutters   │ • 32px Padding Gutters│
└──────────────────────────┴──────────────────────────┴───────────────────────┘
```

---

## 📑 2. Complete Screen Inventory & RBAC Matrix

| Screen ID | Screen Name | Target Persona | Primary Route | Supported Themes |
|---|---|---|---|---|
| `SCR-01` | **Auth & Login Portal** | All Users | `/auth/login` | ☀️ Light / 🌙 Dark |
| `SCR-02` | **User Registration & Onboarding** | Requesters / Staff | `/auth/register` | ☀️ Light / 🌙 Dark |
| `SCR-03` | **Requester Self-Service Dashboard** | Requester | `/dashboard/requester` | ☀️ Light / 🌙 Dark |
| `SCR-04` | **Smart Case Creation Wizard** | Requester / Operator | `/cases/new` | ☀️ Light / 🌙 Dark |
| `SCR-05` | **Requester Case Tracker & Confirmation** | Requester | `/cases/{id}/track` | ☀️ Light / 🌙 Dark |
| `SCR-06` | **Operator Triage & Workstation Feed** | Case Operator | `/cases` | ☀️ Light / 🌙 Dark |
| `SCR-07` | **Operator 3-Column Investigation Studio** | Case Operator | `/cases/{id}` | ☀️ Light / 🌙 Dark |
| `SCR-08` | **Case Collaboration, Tasks & Evidence Hub** | Operator / Investigator | `/cases/{id}/collaboration`| ☀️ Light / 🌙 Dark |
| `SCR-09` | **AI Copilot & Smart Drafter** | Case Operator | Drawer / BottomSheet | ☀️ Light / 🌙 Dark |
| `SCR-10` | **Resolution Proposal & Closure Modal** | Case Operator | `/cases/{id}/resolve` | ☀️ Light / 🌙 Dark |
| `SCR-11` | **Team Lead Command & Workload Monitor** | Team Lead | `/dashboard/team-lead` | ☀️ Light / 🌙 Dark |
| `SCR-12` | **SLA Risk Radar & Escalation Console** | Team Lead / Manager | `/sla/risk-console` | ☀️ Light / 🌙 Dark |
| `SCR-13` | **Problem Management & Root Cause Hub** | Manager / Team Lead | `/problems` | ☀️ Light / 🌙 Dark |
| `SCR-14` | **Executive Analytics & KPI Command Center**| Manager / Executive | `/dashboard/manager` | ☀️ Light / 🌙 Dark |
| `SCR-15` | **Admin Configuration & User Management** | Administrator | `/admin/users` | ☀️ Light / 🌙 Dark |
| `SCR-16` | **Admin SLA Policy & Escalation Rule Builder**| Administrator | `/admin/policies` | ☀️ Light / 🌙 Dark |
| `SCR-17` | **Audit Trail & Immutable Timeline Explorer**| Administrator / Auditor | `/admin/audit-logs` | ☀️ Light / 🌙 Dark |
| `SCR-18` | **Global Notification Center** | All Users | `/notifications` | ☀️ Light / 🌙 Dark |

---

## 🛠️ 3. Detailed Screen Specifications & Stitch Prompts (Light & Dark + Pastel sRGB)

---

### Screen `SCR-01`: Auth & Login Portal
- **Route:** `/auth/login`
- **User Roles:** All
- **Backend Endpoints:**
  - `POST /api/v1/auth/login` -> Payload: `{ "email": "user@example.com", "password": "securePassword" }`
  - Response: `{ "accessToken": "...", "refreshToken": "...", "user": { "id": "...", "name": "...", "email": "...", "roles": ["OPERATOR"] } }`
- **Visual Design (Light & Dark):**
  - **Light Mode:** Crisp `#FAFAFC` canvas, elevated `#FFFFFF` card with soft shadow (`rgba(0,0,0,0.04)`), soft slate typography, pastel periwinkle `#6366F1` button.
  - **Dark Mode:** `#0D1117` canvas, `#161B22` card with `#30363D` 1px border, `#818CF8` button.
  - **Spacing:** `48px` gutter padding, generous `24px` gap between input fields.

```markdown
### Stitch Prompt — SCR-01 (Auth & Login Portal)
Generate a clean, spacious, minimalist enterprise login screen for 'Nexus Case Management' in Flutter.
Theme & Colors:
- Light Mode: #FAFAFC background, #FFFFFF cards, #0F172A text, #6366F1 pastel periwinkle primary button.
- Dark Mode: #0D1117 background, #161B22 cards, #F0F6FC text, #818CF8 pastel periwinkle primary button.
- No heavy neon glows or loud slopes. Clean typography with generous whitespace.
Layout:
- Desktop (1440px): 2-Column airy layout. Left: Subtle brand showcase with soft periwinkle tint badge, clean tagline "Intelligent Case Management with Complete Human Oversight", and 3 minimalist bullet points. Right: Centered, spacious Sign-In card (440px width, 32px padding).
- Mobile (390px): Single clean column, 20px padding, minimal logo, full-width touch fields.
Components:
1. Brand Mark: Minimalist hexagonal Nexus logo with subtle periwinkle accent.
2. Form: Clean outlined email & password inputs with 48px height and 16px internal padding.
3. Actions: "Remember Me" toggle, "Forgot Password" link, and solid pastel periwinkle "Sign In" button.
4. Quick Demo Switcher: Row of 5 soft muted pills ('Requester', 'Operator', 'Team Lead', 'Manager', 'Admin') with pastel hover states.
```

---

### Screen `SCR-02`: User Registration & Onboarding
- **Route:** `/auth/register`
- **Backend Endpoints:** `POST /api/v1/auth/register`
- **Visual Design:** Spacious card with organization dropdown, password strength meter in pastel sage/honey, and generous 24px field spacing.

---

### Screen `SCR-03`: Requester Self-Service Dashboard
- **Route:** `/dashboard/requester`
- **User Roles:** `REQUESTER`
- **Backend Endpoints:**
  - `GET /api/v1/dashboard/requester`
  - `GET /api/v1/cases/my`
- **Visual Design (Light & Dark):**
  - **Light Mode:** Canvas `#FAFAFC`, KPI cards `#FFFFFF` with `#E2E8F0` borders, soft pastel badges (Sky `#E0F2FE`, Butter Honey `#FEF3C7`, Pastel Teal `#CCFBF1`).
  - **Dark Mode:** Canvas `#0D1117`, KPI cards `#161B22` with `#30363D` borders.
  - **Spacing:** `32px` page gutters, `24px` grid gap, `16px` row padding in tables.

```markdown
### Stitch Prompt — SCR-03 (Requester Self-Service Dashboard)
Design a spacious, uncluttered Self-Service Dashboard for Requester users in Nexus.
Theme & Colors:
- Light Mode: #FAFAFC canvas, #FFFFFF cards, #0F172A text.
- Dark Mode: #0D1117 canvas, #161B22 cards, #F0F6FC text.
- Soft sRGB Pastel Status Accents: Sky Blue (#E0F2FE / #0284C7), Butter Honey (#FEF3C7 / #D97706), Sage Teal (#CCFBF1 / #0D9488).
Layout:
- Desktop: 4 KPI cards row with 24px gaps, prominent "+ Report Case" periwinkle CTA, clean filter tabs, and spacious cases table.
- Mobile: 2x2 metric grid, swipeable case cards with 16px padding, BottomNavigationBar, floating "+ Case" button.
Components:
1. Top Navigation: User profile avatar, Organization name pill, Theme toggle (☀️ / 🌙), and "+ Report a Case" button.
2. KPI Tiles (Row of 4):
   - "Active Cases" (3 - Pastel Sky pill)
   - "Awaiting Your Reply" (1 - Pastel Apricot pill)
   - "Resolved" (14 - Pastel Teal pill)
   - "Avg. Turnaround" (4.2 hrs - Soft Slate pill)
3. "Action Required" Clean Callout Box: Soft apricot tinted card (#FFEDD5 / #3D1B06) with "Operator requested VPN logs" and a clean 1-tap "Reply" button.
4. Reported Cases Data List: Clean rows showing Case ID (`NEX-2026-0042`), Title, Category, Status pill, and a minimalist 4-dot milestone progress indicator.
```

---

### Screen `SCR-04`: Smart Case Creation Wizard
- **Route:** `/cases/new`
- **User Roles:** `REQUESTER`, `CASE_OPERATOR`, `ADMIN`
- **Backend Endpoints:**
  - `POST /api/v1/cases` -> Payload: `{ "title": "...", "description": "...", "categoryId": "...", "severity": "MEDIUM" }`
  - `POST /api/v1/cases/{id}/attachments`
- **Visual Design:**
  - Uncluttered 2-column layout on Desktop (65% form, 35% helper).
  - Clean input fields with 20px padding and character counter (`0/100`).
  - Subtle AI Assistance Box: Soft pastel lilac tint card (`#FAF5FF` Light / `#251833` Dark) with discreet, friendly tips (No loud slopes).

```markdown
### Stitch Prompt — SCR-04 (Smart Case Creation Wizard)
Design an elegant, breathable Case Submission form screen for Nexus.
Theme: Dual Theme (Light #FAFAFC / Dark #0D1117).
Layout:
- Desktop (1440px): 2-Column Split with 32px gap (65% Main Form, 35% Smart Assistant Card).
- Mobile (390px): Single-column scrollable form with 16px padding and sticky bottom CTA.
Components:
1. Page Header: "Create a Support Case" with subtle breadcrumb back to Dashboard.
2. Title Input: Clean bordered textfield with floating label and character counter (`45/100`).
3. Category Chips: Clean horizontal pills (IT, Facilities, HR, Security, Software) with soft periwinkle selected state.
4. Severity Selector: Segmented control (Low, Medium, High, Critical) in soft pastel tones.
5. Description Box: Multi-line textfield with 16px internal padding and markdown helper chips.
6. File Attachment Dropzone: Minimalist 1px dashed border zone with cloud upload icon and removeable attachment chips.
7. Right Helper Panel ("✨ Proactive Tips"):
   - Soft pastel lilac card (#FAF5FF Light / #251833 Dark) with subtle border (#C084FC33).
   - "💡 Including error logs helps operators diagnose 2x faster."
8. Bottom Actions: "Save Draft" ghost button + "Submit Case" solid periwinkle CTA.
```

---

### Screen `SCR-05`: Requester Case Tracker & Confirmation
- **Route:** `/cases/{id}/track`
- **Backend Endpoints:**
  - `GET /api/v1/cases/{id}`, `POST /api/v1/cases/{id}/messages`
  - `PUT /api/v1/cases/{id}/resolution/confirm`
  - `PUT /api/v1/cases/{id}/resolution/reject`
- **Visual Design:**
  - Minimalist horizontal step tracker with soft pastel dots.
  - Interactive Resolution Confirmation Card: Soft pastel teal tint card with dual buttons (`[✓ Confirm Resolution]` in soft teal, `[✕ Reject & Reopen]` in soft coral).

---

### Screen `SCR-06`: Operator Triage & Workstation Feed
- **Route:** `/cases`
- **User Roles:** `CASE_OPERATOR`, `TEAM_LEAD`, `MANAGER`
- **Backend Endpoints:**
  - `GET /api/v1/dashboard/operator`
  - `GET /api/v1/cases`
  - `GET /api/v1/cases/search`
- **Visual Design (Light & Dark):**
  - **Light Mode:** Canvas `#FAFAFC`, surface cards `#FFFFFF`, soft slate `#0F172A` text, subtle `#E2E8F0` borders.
  - **Dark Mode:** Canvas `#0D1117`, surface cards `#161B22`, `#F0F6FC` text, `#30363D` borders.
  - **Spacing:** `32px` page padding, spacious table rows with `18px` vertical cell padding.

```markdown
### Stitch Prompt — SCR-06 (Operator Triage & Workstation Feed)
Design a spacious, high-clarity Case Management Workstation for Operators in Nexus.
Theme & Colors:
- Light Mode: #FAFAFC canvas, #FFFFFF data table, #0F172A headings.
- Dark Mode: #0D1117 canvas, #161B22 data table, #F0F6FC headings.
- Status Pastels: Slate (Reported), Sky Blue (Triage), Butter Honey (Investigating), Sage Teal (Closed), Coral Blush (SLA Breached).
Components:
1. Top Toolbar:
   - Search textfield with keyboard shortcut tag `[Ctrl + K]`.
   - Filter pills: "Assigned to Me (8)", "Unassigned Triage (4)", "SLA Breaching (2)", "High Priority (3)".
   - "+ Create Case" primary button.
2. KPI Summary Bar (4 minimal cards with 24px gaps):
   - Active Cases (8), Avg Response (18m), SLA Compliance (98.2%), Resolved Today (5).
3. Main Data Table:
   - Columns: Case ID (Pill), Title & Preview, Requester, Category, Priority (Pastel dot + label), Status (Pastel pill), SLA Timer (Countdown tag, e.g. `⏳ 02h 15m`), Quick Action menu.
   - Clean 1px border dividers, no cramped cells, hover highlight on rows.
```

---

### Screen `SCR-07`: Operator 3-Column Investigation Studio
- **Route:** `/cases/{id}`
- **User Roles:** `CASE_OPERATOR`, `TEAM_LEAD`
- **Backend Endpoints:**
  - `GET /api/v1/cases/{id}`
  - `GET /api/v1/cases/{id}/ai/analysis`
  - `PUT /api/v1/ai/suggestions/{id}` (`ACCEPTED` / `MODIFIED` / `REJECTED`)
  - `GET /api/v1/cases/{id}/sla`
  - `PATCH /api/v1/cases/{id}/status`
  - `POST /api/v1/cases/{id}/assign`
- **Visual Design (3-Column Spacious Workspace):**
  - **Left Column (25%):** AI Intelligence & Case Summary (Subtle lilac micro-cards with ghost `[Accept]`, `[Modify]`, `[Reject]` controls).
  - **Center Column (50%):** Primary Activity Feed with Segmented Tabs (`Public Messages`, `Internal Notes 🔒`, `Tasks`, `Timeline`) and generous `24px` spacing.
  - **Right Column (25%):** Requester Profile, SLA Progress Bar (Clean pastel indicator), Subtasks Checklist, and `[Propose Resolution]` button.
  - **Mobile Adaptation:** Segmented Top Tabs (`✨ AI Triage`, `Chat & Notes`, `Tasks & Details`) with spacious single-column cards.

```markdown
### Stitch Prompt — SCR-07 (Operator 3-Column Investigation Studio)
Design an ultra-clean, spacious 3-Column Investigation Studio screen for Nexus in Flutter.
Theme: Dual Theme (Light #FAFAFC / Dark #0D1117), Slate typography, Pastel Periwinkle (#6366F1 / #818CF8) and Pastel Lilac (#9333EA / #C084FC) accents.
Layout (Desktop 1440px): 3-Column Studio with 24px gaps (25% Left AI & Summary, 50% Center Feed, 25% Right Metadata & SLA).
Left Column (AI Intelligence & Summary):
- Card: "✨ Live Case Summary" with subtle refresh icon and clean bulleted facts.
- Card: "✨ AI Recommendations (PENDING)" in soft lilac tint (#FAF5FF / #251833) with 1px border (#C084FC33):
  - "Priority: HIGH (Confidence 92%)" with 3 clean outline buttons: [✓ Accept], [✎ Modify], [✕ Reject].
  - "Missing Info Detected: Gateway IP & OS Version".
  - "Related Case: NEX-2026-0091 (84% similarity)".
Center Column (Primary Feed):
- Top Banner: Case Number `NEX-2026-0104`, Status Dropdown (`INVESTIGATING`), SLA Countdown Chip `⏳ 02:45:10`.
- Segmented Tabs: `Public Messages`, `Internal Notes 🔒`, `Tasks`, `Timeline`.
- Message Feed: Clean chat bubbles with sender avatar, timestamp, and attachment chips.
- Message Input: Multi-line composer with Toggle: "Public Message" vs "Internal Note 🔒" + "✨ Draft Reply" button.
Right Column (Metadata & SLA):
- Requester Info Card: Avatar, Name, Department, Contact info.
- SLA Progress Bar: Soft pastel honey bar (65% elapsed).
- Tasks Checklist: Minimal checkboxes with "Add Task" link.
- Primary Action CTAs: Soft Lilac "✨ Open Copilot" + Soft Teal "Propose Resolution".
```

---

### Screen `SCR-08`: Case Collaboration, Tasks & Evidence Hub
- **Route:** `/cases/{id}/collaboration`
- **Visual Design:** Spacious Kanban checklist for subtasks, formal findings card, and internal notes feed with 24px padding.

---

### Screen `SCR-09`: AI Copilot & Smart Drafter
- **Route:** Slide-Over Drawer on `/cases/{id}`
- **User Roles:** `CASE_OPERATOR`, `TEAM_LEAD`
- **Backend Endpoints:**
  - `POST /api/v1/cases/{id}/ai/copilot`
  - `POST /api/v1/cases/{id}/ai/draft-communication`
- **Visual Design:**
  - **Desktop:** Right-docked slide-over drawer (440px width) with soft `#FFFFFF` (Light) or `#161B22` (Dark) background and subtle `#C084FC33` border.
  - **Mobile:** Draggable Modal Bottom Sheet (85% height).
  - **Spacing:** `24px` internal padding, clean conversational message bubbles with citation pills.

```markdown
### Stitch Prompt — SCR-09 (AI Copilot & Smart Drafter Slide-Over Drawer)
Design an uncluttered, elegant AI Copilot Drawer (Right-docked, 440px width) for Nexus.
Theme: Dual Theme (Light #FAFAFC / Dark #0D1117).
Components:
1. Drawer Header: "✨ Nexus Case Copilot", Scoped Case badge `NEX-2026-0104`, Close (✕) button.
2. Quick Question Chips (Row): "Summarize Blockers", "Draft Requester Update", "Check SLA Risk".
3. Copilot Conversation Thread:
   - AI Bubble: Soft lilac tinted background (#FAF5FF / #251833) with clean typography.
   - Citation tag: `[Ref: Note #2 by Alex]`.
4. Drafter Studio Box:
   - Generated draft message with 1-click "Insert into Message Box", "Regenerate", and "Copy" buttons.
5. Input Bar: Clean textfield with Send icon.
```

---

### Screen `SCR-10`: Resolution Proposal & Closure Modal
- **Route:** `/cases/{id}/resolve`
- **Visual Design:** Clean modal dialog (560px width, 32px padding) with structured resolution fields and soft pastel teal submit button.

---

### Screen `SCR-11`: Team Lead Command & Workload Monitor
- **Route:** `/dashboard/team-lead`
- **User Roles:** `TEAM_LEAD`, `MANAGER`, `ADMIN`
- **Backend Endpoints:**
  - `GET /api/v1/dashboard/team-lead`
  - `GET /api/v1/collaboration/workload/team`
  - `GET /api/v1/cases/team`
- **Visual Design:**
  - 3-pane airy dashboard with operator workload capacity indicators (`AVAILABLE` in pastel teal, `OPTIMAL` in pastel sky, `OVERLOADED` in pastel coral) and 1-click smart rebalance suggestions.

```markdown
### Stitch Prompt — SCR-11 (Team Lead Command & Workload Monitor)
Design a spacious, executive Operations Console for Team Leads in Nexus.
Theme: Dual Theme (Light #FAFAFC / Dark #0D1117).
Components:
1. Team Header: "Network Operations Team", 6 Active Operators, 28 Open Cases, 96.4% SLA Adherence.
2. Operator Workload Balancer Grid (Cards with 24px padding):
   - Operator Card: Avatar, Name, Active count, Capacity pill (e.g. "Alex: 8 cases [Overloaded - Coral]", "Sarah: 2 cases [Available - Teal]").
   - "Smart Rebalance" button with subtle lilac AI badge.
3. Unassigned Cases Triage Queue:
   - Cards showing case summary with recommended assignee pill (e.g. "✨ Match: Sarah (Least Loaded)").
4. At-Risk / Escalation Queue: Direct reassign and expedite action buttons.
```

---

### Screen `SCR-12`: SLA Risk Radar & Escalation Console
- **Route:** `/sla/risk-console`
- **User Roles:** `TEAM_LEAD`, `MANAGER`, `ADMIN`
- **Backend Endpoints:**
  - `GET /api/v1/sla/at-risk`, `GET /api/v1/sla/breached`, `POST /api/v1/cases/{id}/escalate`, `GET /api/v1/escalations`
- **Visual Design:** Minimalist donut chart with pastel segments (Teal 82%, Honey 12%, Coral 6%), filterable live at-risk table with countdown tags, and multi-tier escalation rule statuses.

---

### Screen `SCR-13`: Problem Management & Root Cause Hub
- **Route:** `/problems`
- **User Roles:** `MANAGER`, `TEAM_LEAD`
- **Backend Endpoints:** `GET/POST /api/v1/problems`, `GET /api/v1/problems/{id}`, `POST /api/v1/problems/{id}/incidents`
- **Visual Design:** Split layout with Master Problems list, AI recurring pattern alert box in pastel lilac, and linked incident clusters table.

---

### Screen `SCR-14`: Executive Analytics & KPI Command Center
- **Route:** `/dashboard/manager`
- **User Roles:** `MANAGER`, `ADMIN`
- **Backend Endpoints:**
  - `GET /api/v1/dashboard/manager`, `GET /api/v1/analytics/trends`, `GET /api/v1/analytics/sla-performance`, `GET /api/v1/analytics/recurring-problems`
- **Visual Design (Light & Dark):**
  - **Light Mode:** Canvas `#FAFAFC`, surface `#FFFFFF` with `#E2E8F0` borders, pastel charts in Periwinkle `#6366F1`, Sky `#0284C7`, Lilac `#9333EA`, and Coral `#E11D48`.
  - **Dark Mode:** Canvas `#0D1117`, surface `#161B22` with `#30363D` borders.
  - **Spacing:** `32px` page gutters, `24px` gap between chart panels.

```markdown
### Stitch Prompt — SCR-14 (Executive Analytics & KPI Command Center)
Design a breathable, high-end Executive Analytics Dashboard for Nexus Management in Flutter.
Theme & Colors:
- Light Mode: #FAFAFC canvas, #FFFFFF chart panels, Slate typography.
- Dark Mode: #0D1117 canvas, #161B22 chart panels, Slate 100 typography.
- Soft Pastel Chart Accents: Periwinkle (#818CF8), Sky Blue (#38BDF8), Amethyst (#C084FC), Sage Teal (#2DD4BF), Coral (#FB7185).
Components:
1. Executive Header: Date range picker (`Last 7 Days`, `Last 30 Days`, `Quarter to Date`), Export report button.
2. 4 Top Metric Cards (24px padding):
   - Total Inflow (`1,248`), MTTR (`3.4 hrs`), SLA Compliance (`96.8%`), First-Contact Resolution (`72.4%`).
3. Charts Grid (2x2 with 24px gaps):
   - Chart 1: Interactive Multi-Line Case Inflow vs Resolution Trend.
   - Chart 2: Donut Chart of Cases by Category with clean legend.
   - Chart 3: Horizontal Bar Chart of Department SLA Compliance %.
   - Panel 4: "✨ AI Executive Digest" card with clean bullet points highlighting workload bottlenecks and incident spikes.
```

---

### Screen `SCR-15`: Admin Configuration & User Management
- **Route:** `/admin/users`
- **User Roles:** `ADMINISTRATOR`
- **Backend Endpoints:** `GET/POST /admin/users`, `PUT /admin/users/{id}/role`, `GET/POST /admin/teams`, `GET/POST /admin/categories`
- **Visual Design:** Clean sub-navigation tabs (`Users & Roles`, `Teams`, `Categories`, `Settings`), user directory table with 18px cell padding, and team membership manager.

---

### Screen `SCR-16`: Admin SLA Policy & Escalation Rule Builder
- **Route:** `/admin/policies`
- **User Roles:** `ADMINISTRATOR`
- **Backend Endpoints:** `GET/POST /admin/sla-policies`, `GET/POST /admin/escalation-rules`
- **Visual Design:** Spacious SLA Matrix table (Response/Resolution times per priority) and clean condition-action rule builder.

---

### Screen `SCR-17`: Audit Trail & Immutable Timeline Explorer
- **Route:** `/admin/audit-logs`
- **User Roles:** `ADMINISTRATOR`, `TEAM_LEAD`
- **Backend Endpoints:** `GET /api/v1/audit-logs`, `GET /api/v1/audit-logs/case/{id}/timeline`
- **Visual Design:** Filter toolbar (Actor, Action, Date), audit log table with expandable diff inspector (`Old Value ➔ New Value`) and cryptographic tamper-proof verification badge.

---

### Screen `SCR-18`: Global Notification Center
- **Route:** `/notifications`
- **User Roles:** All
- **Backend Endpoints:** `GET /api/v1/notifications`, `PUT /api/v1/notifications/{id}/read`, `PUT /api/v1/notifications/read-all`
- **Visual Design:** Clean notification cards with category dots, relative timestamps, direct case links, and "Mark All as Read" button.

---

## 🚀 4. How to Prompt Stitch MCP (Best Practices)

When invoking Stitch to generate screens:
1. **Specify Theme:** State whether you are generating for **☀️ Light Mode** or **🌙 Dark Mode**.
2. **Specify Device Target:** Specify **Desktop Web (1440px)**, **Tablet (834px)**, or **Mobile (390px)**.
3. **Use the sRGB Pastel Palette:** Pass the color tokens defined in §1.1.
4. **Follow the Advancement Spacing Rules:** Ensure 24px/32px paddings, generous whitespace, clean line heights, and no visual clutter as defined in §1.2.
5. **Discrete AI Micro-Cards:** Ensure AI recommendations use the subtle lilac tint (`#FAF5FF` / `#251833`) with ghost tri-action buttons (`Accept/Modify/Reject`) instead of heavy slope gimmicks.

---
*End of Master Stitch Screen Specification (v3.0 Spacious Pastel Dual-Theme Edition).*
