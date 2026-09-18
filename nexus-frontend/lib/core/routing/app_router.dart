import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../features/auth/presentation/auth_state_provider.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/auth/presentation/register_screen.dart';

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

      // Temporary placeholder shell for dashboard redirection during Batch 1
      GoRoute(
        path: '/dashboard',
        builder: (context, state) {
          final user = authState.user;
          final role = user?.primaryRole ?? 'REQUESTER';
          return Scaffold(
            appBar: AppBar(
              title: Text('Nexus Workspace — $role'),
              actions: [
                IconButton(
                  icon: const Icon(Icons.logout),
                  onPressed: () {
                    ref.read(authStateProvider.notifier).logout();
                    context.go('/auth/login');
                  },
                ),
              ],
            ),
            body: Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Icon(Icons.check_circle_outline, size: 64, color: Colors.green),
                  const SizedBox(height: 16),
                  Text(
                    'Authenticated as: ${user?.name ?? 'User'}',
                    style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text('Active Role: $role', style: const TextStyle(color: Colors.grey)),
                  const SizedBox(height: 24),
                  ElevatedButton(
                    onPressed: () => context.go('/auth/login'),
                    child: const Text('Back to Login Portal (SCR-01)'),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    ],
  );
});
