# Nexus — Environment Configuration & Deployment
### *AI-Powered Case Management Platform*

> **Source:** Extracted from `Nexus_SRS.md` §10 & §11 (SRS v1.1)

---

## 10. Environment Configuration

All secrets and provider selection are environment-driven — never hardcoded.

**Backend `.env` (example keys):**
```
SPRING_PROFILES_ACTIVE=local|prod

# Database
SUPABASE_DB_URL=
SUPABASE_DB_USERNAME=
SUPABASE_DB_PASSWORD=

# Storage
SUPABASE_STORAGE_URL=
SUPABASE_STORAGE_KEY=
SUPABASE_STORAGE_BUCKET=

# Auth
JWT_SECRET=
JWT_ACCESS_EXPIRY_MINUTES=
JWT_REFRESH_EXPIRY_DAYS=

# AI Provider (Spring AI)
AI_PROVIDER=openai|anthropic|gemini
OPENAI_API_KEY=
ANTHROPIC_API_KEY=
GEMINI_API_KEY=

# Email Provider
EMAIL_PROVIDER=brevo|smtp
BREVO_API_KEY=
BREVO_SENDER_EMAIL=
SMTP_HOST=
SMTP_PORT=
SMTP_USERNAME=
SMTP_PASSWORD=

# Monitoring
SENTRY_DSN=
```

**Frontend `.env` (example keys):**
```
API_BASE_URL=
ENVIRONMENT=local|production
```

---

## 11. Deployment

| Concern | Approach |
|---|---|
| Backend hosting | Render — auto-deploy on push to `main` |
| Frontend hosting | Vercel — Flutter Web build (`flutter build web`, output `build/web`), auto-deploy on push to `main` |
| CI/CD | None separate — relies on Render's and Vercel's native Git integration for build and deploy |
| Database | Supabase (managed PostgreSQL, production tier, automatic backups) |
| Storage | Supabase Storage |
| Secrets | Set directly in Render and Vercel dashboards — never committed to the repository |
| Health checks | Spring Boot Actuator `/actuator/health`, used by Render's uptime monitoring |
| Error tracking | Sentry (backend exceptions, AI/email failures) |
| Local development | Docker Compose for backend (optionally local Postgres, or point directly at a Supabase dev project); Flutter SDK with Chrome/emulator targets; `AI_PROVIDER=mock` for offline/no-cost development |
