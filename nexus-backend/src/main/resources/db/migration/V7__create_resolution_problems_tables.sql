-- =========================================================
-- V7__create_resolution_problems_tables.sql
-- Nexus Database Migration: Resolutions, Problems & Incident Relations (Phase 6)
-- =========================================================

-- 1. Resolutions Table
CREATE TABLE IF NOT EXISTS resolutions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    submitted_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    what_was_done TEXT NOT NULL,
    findings TEXT NOT NULL,
    evidence_ref VARCHAR(255),
    limitations TEXT,
    resolution_message TEXT NOT NULL,
    requester_decision VARCHAR(30) DEFAULT 'PENDING' NOT NULL,
    feedback TEXT,
    decided_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_requester_decision CHECK (requester_decision IN ('PENDING', 'CONFIRMED', 'REJECTED'))
);

-- 2. Problems Table
CREATE TABLE IF NOT EXISTS problems (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    suspected_root_cause TEXT,
    confirmed_root_cause TEXT,
    investigation_notes TEXT,
    corrective_action TEXT,
    preventive_action TEXT,
    status VARCHAR(30) DEFAULT 'OPEN' NOT NULL,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_problem_status CHECK (status IN ('OPEN', 'INVESTIGATING', 'RESOLVED'))
);

-- 3. Problem Incident Relations Table
CREATE TABLE IF NOT EXISTS problem_incident_relations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT uk_problem_incident UNIQUE (problem_id, case_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_resolutions_case_id ON resolutions(case_id);
CREATE INDEX IF NOT EXISTS idx_resolutions_decision ON resolutions(requester_decision);
CREATE INDEX IF NOT EXISTS idx_problems_org_status ON problems(organization_id, status);
CREATE INDEX IF NOT EXISTS idx_problem_incident_problem_id ON problem_incident_relations(problem_id);
CREATE INDEX IF NOT EXISTS idx_problem_incident_case_id ON problem_incident_relations(case_id);
