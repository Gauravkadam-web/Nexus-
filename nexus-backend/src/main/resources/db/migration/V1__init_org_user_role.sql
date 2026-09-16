-- =========================================================
-- V1__init_org_user_role.sql
-- Nexus Database Migration: Organization & Identity
-- =========================================================

-- Enable UUID extension if not present
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Organizations
CREATE TABLE IF NOT EXISTS organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 2. Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

-- 3. Roles
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT chk_role_name CHECK (name IN ('REQUESTER', 'OPERATOR', 'TEAM_LEAD', 'MANAGER', 'ADMIN'))
);

-- 4. User Roles (Junction Table)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- 5. Teams
CREATE TABLE IF NOT EXISTS teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    lead_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 6. Team Members (Junction Table)
CREATE TABLE IF NOT EXISTS team_members (
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (team_id, user_id)
);

-- 7. Categories & Subcategories
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    parent_category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    default_team_id UUID REFERENCES teams(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Seed System Roles
INSERT INTO roles (id, name) VALUES 
    (gen_random_uuid(), 'REQUESTER'),
    (gen_random_uuid(), 'OPERATOR'),
    (gen_random_uuid(), 'TEAM_LEAD'),
    (gen_random_uuid(), 'MANAGER'),
    (gen_random_uuid(), 'ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Indices for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_org ON users(organization_id);
CREATE INDEX IF NOT EXISTS idx_teams_org ON teams(organization_id);
CREATE INDEX IF NOT EXISTS idx_categories_org ON categories(organization_id);
