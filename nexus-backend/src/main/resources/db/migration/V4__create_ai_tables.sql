-- =========================================================
-- V4__create_ai_tables.sql
-- Nexus Database Migration: AI Case Intelligence (Phase 3)
-- =========================================================

-- 1. AI Analysis Table (per-case analysis snapshot)
CREATE TABLE IF NOT EXISTS ai_analysis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    suggested_category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    suggested_subcategory_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    suggested_priority VARCHAR(20),
    suggested_severity VARCHAR(20),
    suggested_team_id UUID REFERENCES teams(id) ON DELETE SET NULL,
    missing_information JSONB DEFAULT '[]',
    recommended_next_action TEXT,
    related_cases JSONB DEFAULT '[]',
    risk_information JSONB DEFAULT '{}',
    confidence DECIMAL(4,2),
    model_information VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_ai_priority CHECK (suggested_priority IN ('LOW','MEDIUM','HIGH','URGENT') OR suggested_priority IS NULL),
    CONSTRAINT chk_ai_severity CHECK (suggested_severity IN ('LOW','MEDIUM','HIGH','CRITICAL') OR suggested_severity IS NULL),
    CONSTRAINT chk_ai_confidence CHECK (confidence IS NULL OR (confidence >= 0.00 AND confidence <= 1.00))
);

-- 2. AI Suggestions Table (individual Accept/Modify/Reject decisions)
CREATE TABLE IF NOT EXISTS ai_suggestions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    suggestion_type VARCHAR(30) NOT NULL,
    suggested_value JSONB NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    decided_by UUID REFERENCES users(id) ON DELETE SET NULL,
    override_reason TEXT,
    modified_value JSONB,
    decided_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_ai_suggestion_type CHECK (suggestion_type IN (
        'CATEGORY','PRIORITY','SEVERITY','ASSIGNMENT','DUPLICATE','ROOT_CAUSE'
    )),
    CONSTRAINT chk_ai_suggestion_status CHECK (status IN (
        'PENDING','ACCEPTED','MODIFIED','REJECTED'
    ))
);

-- 3. AI Summaries Table (versioned case narrative summaries)
CREATE TABLE IF NOT EXISTS ai_summaries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    summary_text TEXT NOT NULL,
    version INT DEFAULT 1 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 4. Automation Events Table (idempotency tracking for all automated jobs)
CREATE TABLE IF NOT EXISTS automation_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    trigger_type VARCHAR(20) NOT NULL DEFAULT 'EVENT',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    result JSONB,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_automation_event_type CHECK (event_type IN (
        'AI_ANALYSIS','SUMMARY_UPDATE','DUPLICATE_DETECTION',
        'ASSIGNMENT_RECOMMENDATION','SLA_CHECK','RISK_DETECTION',
        'ESCALATION_TRIGGER','NOTIFICATION','AUDIT_EVENT'
    )),
    CONSTRAINT chk_automation_trigger_type CHECK (trigger_type IN ('EVENT','SCHEDULED','USER','AI')),
    CONSTRAINT chk_automation_status CHECK (status IN ('PENDING','SUCCESS','FAILED'))
);

-- Indexes for efficient lookups
CREATE INDEX IF NOT EXISTS idx_ai_analysis_case_id ON ai_analysis(case_id);
CREATE INDEX IF NOT EXISTS idx_ai_analysis_created_at ON ai_analysis(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_ai_suggestions_case_id ON ai_suggestions(case_id);
CREATE INDEX IF NOT EXISTS idx_ai_suggestions_status ON ai_suggestions(status);
CREATE INDEX IF NOT EXISTS idx_ai_suggestions_type ON ai_suggestions(suggestion_type);

CREATE INDEX IF NOT EXISTS idx_ai_summaries_case_id ON ai_summaries(case_id);
CREATE INDEX IF NOT EXISTS idx_ai_summaries_version ON ai_summaries(version DESC);

CREATE INDEX IF NOT EXISTS idx_automation_events_case_id ON automation_events(case_id);
CREATE INDEX IF NOT EXISTS idx_automation_events_type ON automation_events(event_type);
CREATE INDEX IF NOT EXISTS idx_automation_events_status ON automation_events(status);
