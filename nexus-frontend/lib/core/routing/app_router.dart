import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/presentation/auth_state_provider.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/auth/presentation/register_screen.dart';
import '../../features/case/presentation/case_create_wizard_screen.dart';
import '../../features/case/presentation/case_tracker_screen.dart';
import '../../features/collaboration/presentation/case_collaboration_evidence_hub_screen.dart';
import '../../features/copilot/presentation/ai_copilot_smart_drafter_screen.dart';
import '../../features/dashboard/operator/operator_triage_feed_screen.dart';
import '../../features/dashboard/requester/requester_dashboard_screen.dart';
import '../../features/dashboard/team_lead/team_lead_command_screen.dart';
import '../../features/investigation/presentation/operator_investigation_studio_screen.dart';
import '../../features/resolution/presentation/resolution_proposal_closure_screen.dart';
import '../../features/sla/presentation/sla_risk_radar_console_screen.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: '/auth/login',
    routes: [
      // Auth routes (SCR-01 & SCR-02)
      GoRoute(
        path: '/auth/login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/auth/register',
        builder: (context, state) => const RegisterScreen(),
      ),

      // Dashboard routes (SCR-03 & SCR-06 & SCR-11)
      GoRoute(
        path: '/dashboard',
        builder: (context, state) {
          return const RequesterDashboardScreen();
        },
      ),
      GoRoute(
        path: '/dashboard/requester',
        builder: (context, state) => const RequesterDashboardScreen(),
      ),
      GoRoute(
        path: '/dashboard/operator/triage',
        builder: (context, state) => const OperatorTriageFeedScreen(),
      ),
      GoRoute(
        path: '/cases',
        builder: (context, state) => const OperatorTriageFeedScreen(),
      ),
      GoRoute(
        path: '/dashboard/team-lead',
        builder: (context, state) => const TeamLeadCommandScreen(),
      ),

      // Case & Tracking routes (SCR-04 & SCR-05)
      GoRoute(
        path: '/cases/new',
        builder: (context, state) => const CaseCreateWizardScreen(),
      ),
      GoRoute(
        path: '/cases/:id/track',
        builder: (context, state) {
          final caseId = state.pathParameters['id'] ?? 'NEX-2026-0042';
          return CaseTrackerScreen(caseId: caseId);
        },
      ),

      // Investigation & Collaboration routes (SCR-07 & SCR-08)
      GoRoute(
        path: '/cases/:id',
        builder: (context, state) {
          final caseId = state.pathParameters['id'] ?? 'NEX-2026-0104';
          return OperatorInvestigationStudioScreen(caseId: caseId);
        },
      ),
      GoRoute(
        path: '/cases/:id/investigation',
        builder: (context, state) {
          final caseId = state.pathParameters['id'] ?? 'NEX-2026-0104';
          return OperatorInvestigationStudioScreen(caseId: caseId);
        },
      ),
      GoRoute(
        path: '/cases/:id/collaboration',
        builder: (context, state) {
          final caseId = state.pathParameters['id'] ?? 'NEX-2026-0104';
          return CaseCollaborationEvidenceHubScreen(caseId: caseId);
        },
      ),

      // Copilot & Resolution routes (SCR-09 & SCR-10)
      GoRoute(
        path: '/cases/:id/copilot',
        builder: (context, state) {
          final caseId = state.pathParameters['id'] ?? 'NEX-2026-0104';
          return AiCopilotSmartDrafterScreen(caseId: caseId);
        },
      ),
      GoRoute(
        path: '/cases/:id/resolve',
        builder: (context, state) {
          final caseId = state.pathParameters['id'] ?? 'NEX-2026-0104';
          return ResolutionProposalClosureScreen(caseId: caseId);
        },
      ),

      // SLA Risk Radar & Escalation Console (SCR-12)
      GoRoute(
        path: '/sla/risk-console',
        builder: (context, state) => const SlaRiskRadarConsoleScreen(),
      ),
    ],
  );
});

