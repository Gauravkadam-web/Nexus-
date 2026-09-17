-- =========================================================
-- V6__create_sla_escalation_notifications_tables.sql
-- Nexus Database Migration: SLA, Risk, Escalations & Notifications (Phase 5)
-- =========================================================

-- 1. SLA Policies Table
CREATE TABLE IF NOT EXISTS sla_policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    priority VARCHAR(30) NOT NULL,
    response_time_minutes INT NOT NULL,
    resolution_time_minutes INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_sla_policy_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'))
);

-- 2. Case SLA Tracking Table
CREATE TABLE IF NOT EXISTS case_sla (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    sla_policy_id UUID REFERENCES sla_policies(id) ON DELETE SET NULL,
    response_deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    resolution_deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(30) DEFAULT 'ON_TRACK' NOT NULL,
    responded_at TIMESTAMP WITH TIME ZONE,
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_case_sla_status CHECK (status IN ('ON_TRACK', 'AT_RISK', 'BREACHED', 'MET'))
);

-- 3. Case Risk Table
CREATE TABLE IF NOT EXISTS case_risk (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    risk_level VARCHAR(30) NOT NULL,
    reasons TEXT NOT NULL,
    detected_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_case_risk_level CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- 4. Escalation Rules Table
CREATE TABLE IF NOT EXISTS escalation_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    condition_type VARCHAR(50) NOT NULL,
    condition_config TEXT,
    escalation_level VARCHAR(30) NOT NULL,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_escalation_condition_type CHECK (condition_type IN (
        'SLA_APPROACHING', 'SLA_BREACHED', 'REPEATED_REOPENING',
        'MULTIPLE_FAILED_ATTEMPTS', 'OPERATOR_REQUESTED', 'HIGH_IMPACT_INCIDENT'
    )),
    CONSTRAINT chk_escalation_level CHECK (escalation_level IN ('TEAM_LEAD', 'MANAGER'))
);

-- 5. Escalations Table
CREATE TABLE IF NOT EXISTS escalations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    escalation_rule_id UUID REFERENCES escalation_rules(id) ON DELETE SET NULL,
    escalation_level VARCHAR(30) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(30) DEFAULT 'RECOMMENDED' NOT NULL,
    triggered_by VARCHAR(30) DEFAULT 'SYSTEM' NOT NULL,
    confirmed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_escalation_record_level CHECK (escalation_level IN ('TEAM_LEAD', 'MANAGER')),
    CONSTRAINT chk_escalation_status CHECK (status IN ('RECOMMENDED', 'CONFIRMED')),
    CONSTRAINT chk_escalation_triggered_by CHECK (triggered_by IN ('SYSTEM', 'USER'))
);

-- 6. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    case_id UUID REFERENCES cases(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_notification_type CHECK (type IN (
        'CASE_ASSIGNED', 'REQUESTER_REPLIED', 'TASK_ASSIGNED',
        'SLA_WARNING', 'SLA_BREACH', 'ESCALATION',
        'RESOLUTION', 'CASE_REOPENED'
    ))
);

-- Indexes for high-speed queries and scheduled scans
CREATE INDEX IF NOT EXISTS idx_sla_policies_org_cat ON sla_policies(organization_id, category_id);
CREATE INDEX IF NOT EXISTS idx_case_sla_case_id ON case_sla(case_id);
CREATE INDEX IF NOT EXISTS idx_case_sla_status_resolution ON case_sla(status, resolution_deadline);
CREATE INDEX IF NOT EXISTS idx_case_risk_case_id ON case_risk(case_id);
CREATE INDEX IF NOT EXISTS idx_case_risk_level ON case_risk(risk_level);
CREATE INDEX IF NOT EXISTS idx_escalation_rules_org ON escalation_rules(organization_id, is_active);
CREATE INDEX IF NOT EXISTS idx_escalations_case_id ON escalations(case_id);
CREATE INDEX IF NOT EXISTS idx_escalations_status ON escalations(status);
CREATE INDEX IF NOT EXISTS idx_notifications_user_read ON notifications(user_id, read);
CREATE INDEX IF NOT EXISTS idx_notifications_case_id ON notifications(case_id);
