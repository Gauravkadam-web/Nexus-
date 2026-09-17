-- =========================================================
-- V5__create_case_relations_tables.sql
-- Nexus Database Migration: Case Relations & Smart Operations (Phase 4)
-- =========================================================

CREATE TABLE IF NOT EXISTS case_relations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    related_case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    relation_type VARCHAR(30) NOT NULL,
    linked_by UUID REFERENCES users(id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_relation_type CHECK (relation_type IN ('DUPLICATE', 'RELATED', 'MASTER_INCIDENT')),
    CONSTRAINT chk_no_self_relation CHECK (case_id <> related_case_id),
    CONSTRAINT uq_case_relation UNIQUE (case_id, related_case_id, relation_type)
);

-- Indexes for fast bidirectional relation lookups
CREATE INDEX IF NOT EXISTS idx_case_relations_case_id ON case_relations(case_id);
CREATE INDEX IF NOT EXISTS idx_case_relations_related_id ON case_relations(related_case_id);
CREATE INDEX IF NOT EXISTS idx_case_relations_type ON case_relations(relation_type);
CREATE INDEX IF NOT EXISTS idx_case_relations_created_at ON case_relations(created_at DESC);
