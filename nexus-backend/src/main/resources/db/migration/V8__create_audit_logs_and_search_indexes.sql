-- ==============================================================================
-- V8__create_audit_logs_and_search_indexes.sql
-- Phase 7: Analytics, Audit, Search & Security Hardening
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. audit_logs table (Immutable append-only governance trail - US-33)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(100) NOT NULL,
    actor_id UUID REFERENCES users(id) ON DELETE SET NULL,
    actor_name VARCHAR(100),
    actor_role VARCHAR(50),
    old_value TEXT,
    new_value TEXT,
    source VARCHAR(30) NOT NULL DEFAULT 'USER' CHECK (source IN ('USER', 'SYSTEM', 'AI')),
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Indexes for efficient audit querying & timeline lookup
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_actor ON audit_logs(actor_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_logs(created_at DESC);

-- ------------------------------------------------------------------------------
-- 2. Multi-column and search performance indexes on cases table (US-34)
-- ------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_cases_search_compound ON cases(category_id, status, priority, severity, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_cases_assigned_user ON cases(assigned_user_id, status);
CREATE INDEX IF NOT EXISTS idx_cases_assigned_team ON cases(assigned_team_id, status);
CREATE INDEX IF NOT EXISTS idx_cases_created_at ON cases(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_cases_resolved_at ON cases(resolved_at DESC);
