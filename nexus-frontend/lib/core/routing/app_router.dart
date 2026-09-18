import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/presentation/auth_state_provider.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/auth/presentation/register_screen.dart';
import '../../features/case/presentation/case_create_wizard_screen.dart';
import '../../features/dashboard/requester/requester_dashboard_screen.dart';

final appRouterProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: '/auth/login',
    routes: [
      // Auth routes
      GoRoute(
        path: '/auth/login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/auth/register',
        builder: (context, state) => const RegisterScreen(),
      ),

      // Dashboard routes
      GoRoute(
        path: '/dashboard',
        builder: (context, state) {
          // For requesters, route to RequesterDashboardScreen
          return const RequesterDashboardScreen();
        },
      ),
      GoRoute(
        path: '/dashboard/requester',
        builder: (context, state) => const RequesterDashboardScreen(),
      ),

      // Case creation wizard route
      GoRoute(
        path: '/cases/new',
        builder: (context, state) => const CaseCreateWizardScreen(),
      ),
    ],
  );
});
