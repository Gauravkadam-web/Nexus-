-- =========================================================
-- V2__create_case_tables.sql
-- Nexus Database Migration: Case Core & Case Assignments
-- =========================================================

-- 1. Cases Table
CREATE TABLE IF NOT EXISTS cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_number VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    subcategory_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    severity VARCHAR(30) NOT NULL,
    priority VARCHAR(30) NOT NULL,
    status VARCHAR(50) DEFAULT 'REPORTED' NOT NULL,
    requester_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    assigned_team_id UUID REFERENCES teams(id) ON DELETE SET NULL,
    assigned_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    location VARCHAR(255),
    resolved_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    CONSTRAINT chk_case_severity CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_case_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    CONSTRAINT chk_case_status CHECK (status IN (
        'REPORTED', 'UNDERSTOOD', 'ASSIGNED', 'INVESTIGATING',
        'WAITING_FOR_INFO', 'AT_RISK', 'ESCALATED',
        'RESOLUTION_PROPOSED', 'CLOSED', 'REOPENED',
        'DUPLICATE', 'RELATED', 'CANCELLED'
    ))
);

-- 2. Case Assignments History Table
CREATE TABLE IF NOT EXISTS case_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    assigned_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    assigned_team_id UUID REFERENCES teams(id) ON DELETE SET NULL,
    assigned_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indices for rapid querying and filtering
CREATE INDEX IF NOT EXISTS idx_cases_case_number ON cases(case_number);
CREATE INDEX IF NOT EXISTS idx_cases_status ON cases(status);
CREATE INDEX IF NOT EXISTS idx_cases_requester_id ON cases(requester_id);
CREATE INDEX IF NOT EXISTS idx_cases_assigned_user_id ON cases(assigned_user_id);
CREATE INDEX IF NOT EXISTS idx_cases_assigned_team_id ON cases(assigned_team_id);
CREATE INDEX IF NOT EXISTS idx_cases_category_id ON cases(category_id);
CREATE INDEX IF NOT EXISTS idx_cases_created_at ON cases(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_case_assignments_case_id ON case_assignments(case_id);
