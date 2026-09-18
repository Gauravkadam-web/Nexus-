import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/presentation/auth_state_provider.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/auth/presentation/register_screen.dart';
import '../../features/case/presentation/case_create_wizard_screen.dart';
import '../../features/case/presentation/case_tracker_screen.dart';
import '../../features/dashboard/operator/operator_triage_feed_screen.dart';
import '../../features/dashboard/requester/requester_dashboard_screen.dart';

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

      // Dashboard routes (SCR-03 & SCR-06)
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

      // Case routes (SCR-04 & SCR-05)
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
    ],
  );
});
