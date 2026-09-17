-- =========================================================
-- V3__create_collaboration_tables.sql
-- Nexus Database Migration: Communication, Evidence & Investigation
-- =========================================================

-- 1. Case Attachments Table
CREATE TABLE IF NOT EXISTS case_attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    uploaded_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    storage_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 2. Case Messages Table (Multi-role communication stream)
CREATE TABLE IF NOT EXISTS case_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    message_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    visible_to_requester BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_case_message_type CHECK (message_type IN (
        'QUESTION', 'ANSWER', 'UPDATE', 'EVIDENCE_REQUEST', 'RESOLUTION_MESSAGE', 'FOLLOW_UP'
    ))
);

-- 3. Internal Notes Table (Private Operator / Lead discussions)
CREATE TABLE IF NOT EXISTS internal_notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 4. Case Tasks Table (Actionable subtasks for investigation)
CREATE TABLE IF NOT EXISTS case_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    assignee_id UUID REFERENCES users(id) ON DELETE SET NULL,
    status VARCHAR(50) DEFAULT 'PENDING' NOT NULL,
    priority VARCHAR(30) DEFAULT 'MEDIUM' NOT NULL,
    due_date TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_case_task_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_case_task_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- 5. Investigations Table (Structured investigation logs)
CREATE TABLE IF NOT EXISTS investigations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    operator_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    observation TEXT NOT NULL,
    action_taken TEXT NOT NULL,
    finding TEXT NOT NULL,
    evidence_ref VARCHAR(500),
    follow_up_needed TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for efficient lookups and relationship traversals
CREATE INDEX IF NOT EXISTS idx_case_attachments_case_id ON case_attachments(case_id);
CREATE INDEX IF NOT EXISTS idx_case_attachments_uploaded_by ON case_attachments(uploaded_by);

CREATE INDEX IF NOT EXISTS idx_case_messages_case_id ON case_messages(case_id);
CREATE INDEX IF NOT EXISTS idx_case_messages_sender_id ON case_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_case_messages_created_at ON case_messages(created_at ASC);

CREATE INDEX IF NOT EXISTS idx_internal_notes_case_id ON internal_notes(case_id);
CREATE INDEX IF NOT EXISTS idx_internal_notes_author_id ON internal_notes(author_id);
CREATE INDEX IF NOT EXISTS idx_internal_notes_created_at ON internal_notes(created_at ASC);

CREATE INDEX IF NOT EXISTS idx_case_tasks_case_id ON case_tasks(case_id);
CREATE INDEX IF NOT EXISTS idx_case_tasks_assignee_id ON case_tasks(assignee_id);
CREATE INDEX IF NOT EXISTS idx_case_tasks_status ON case_tasks(status);
CREATE INDEX IF NOT EXISTS idx_case_tasks_due_date ON case_tasks(due_date);

CREATE INDEX IF NOT EXISTS idx_investigations_case_id ON investigations(case_id);
CREATE INDEX IF NOT EXISTS idx_investigations_operator_id ON investigations(operator_id);
