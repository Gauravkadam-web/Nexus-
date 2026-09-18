class ApiEndpoints {
  ApiEndpoints._();

  // Auth
  static const String login = '/auth/login';
  static const String register = '/auth/register';
  static const String refresh = '/auth/refresh';
  static const String logout = '/auth/logout';
  static const String me = '/auth/me';

  // Cases
  static const String cases = '/cases';
  static String caseDetail(String id) => '/cases/$id';
  static String caseStatus(String id) => '/cases/$id/status';
  static String caseAssign(String id) => '/cases/$id/assign';
  static String caseAttachments(String id) => '/cases/$id/attachments';
  static String caseTimeline(String id) => '/cases/$id/timeline';
  static String caseRelations(String id) => '/cases/$id/relations';
  static const String caseSearch = '/cases/search';

  // Collaboration
  static String caseMessages(String id) => '/cases/$id/messages';
  static String caseNotes(String id) => '/cases/$id/notes';
  static String caseTasks(String id) => '/cases/$id/tasks';
  static String taskStatus(String id) => '/tasks/$id';
  static String caseInvestigations(String id) => '/cases/$id/investigations';

  // AI
  static String aiAnalyze(String id) => '/cases/$id/ai/analyze';
  static String aiAnalysis(String id) => '/cases/$id/ai/analysis';
  static String aiSummary(String id) => '/cases/$id/ai/summary';
  static String aiSummaryRegenerate(String id) => '/cases/$id/ai/summary/regenerate';
  static String aiSuggestionDecision(String id) => '/ai/suggestions/$id';
  static String aiDuplicates(String id) => '/cases/$id/ai/duplicates';
  static String aiAssignmentRec(String id) => '/cases/$id/ai/assignment-recommendation';
  static String aiDraftComm(String id) => '/cases/$id/ai/draft-communication';
  static String aiCopilot(String id) => '/cases/$id/ai/copilot';

  // SLA & Escalation
  static String caseSla(String id) => '/cases/$id/sla';
  static const String slaAtRisk = '/sla/at-risk';
  static const String slaBreached = '/sla/breached';
  static String caseEscalate(String id) => '/cases/$id/escalate';
  static const String escalations = '/escalations';

  // Resolution
  static String caseResolution(String id) => '/cases/$id/resolution';
  static String caseResolutionConfirm(String id) => '/cases/$id/resolution/confirm';
  static String caseResolutionReject(String id) => '/cases/$id/resolution/reject';

  // Problem Management
  static const String problems = '/problems';
  static String problemDetail(String id) => '/problems/$id';
  static String problemIncidents(String id) => '/problems/$id/incidents';

  // Notifications
  static const String notifications = '/notifications';
  static String notificationRead(String id) => '/notifications/$id/read';
  static const String notificationsReadAll = '/notifications/read-all';

  // Dashboards & Analytics
  static const String dashboardRequester = '/dashboard/requester';
  static const String dashboardOperator = '/dashboard/operator';
  static const String dashboardTeamLead = '/dashboard/team-lead';
  static const String dashboardManager = '/dashboard/manager';
  static const String analyticsTrends = '/analytics/trends';
  static const String analyticsSla = '/analytics/sla-performance';
  static const String analyticsProblems = '/analytics/recurring-problems';

  // Admin
  static const String adminUsers = '/admin/users';
  static String adminUserRole(String id) => '/admin/users/$id/role';
  static const String adminTeams = '/admin/teams';
  static String adminTeamDetail(String id) => '/admin/teams/$id';
  static const String adminCategories = '/admin/categories';
  static String adminCategoryDetail(String id) => '/admin/categories/$id';
  static const String adminSlaPolicies = '/admin/sla-policies';
  static String adminSlaPolicyDetail(String id) => '/admin/sla-policies/$id';
  static const String adminEscalationRules = '/admin/escalation-rules';
  static String adminEscalationRuleDetail(String id) => '/admin/escalation-rules/$id';
  static const String adminAuditLogs = '/audit-logs';
  static String adminAuditLogCase(String id) => '/audit-logs/case/$id';
}
