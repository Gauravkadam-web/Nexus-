import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../data/auth_repository.dart';
import '../domain/user_model.dart';

class AuthState {
  final bool isLoading;
  final bool isAuthenticated;
  final UserModel? user;
  final String? errorMessage;

  const AuthState({
    this.isLoading = false,
    this.isAuthenticated = false,
    this.user,
    this.errorMessage,
  });

  AuthState copyWith({
    bool? isLoading,
    bool? isAuthenticated,
    UserModel? user,
    String? errorMessage,
  }) {
    return AuthState(
      isLoading: isLoading ?? this.isLoading,
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      user: user ?? this.user,
      errorMessage: errorMessage,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  final AuthRepository _repository;

  AuthNotifier(this._repository) : super(const AuthState());

  Future<bool> login(String email, String password) async {
    state = state.copyWith(isLoading: true, errorMessage: null);
    final result = await _repository.login(email: email, password: password);
    if (result.success && result.user != null) {
      state = state.copyWith(
        isLoading: false,
        isAuthenticated: true,
        user: result.user,
      );
      return true;
    } else {
      state = state.copyWith(
        isLoading: false,
        isAuthenticated: false,
        errorMessage: result.error,
      );
      return false;
    }
  }

  void loginAsDemoRole(String roleName) {
    UserModel demoUser;
    switch (roleName.toUpperCase()) {
      case 'REQUESTER':
        demoUser = const UserModel(
          id: 'demo-req-1',
          email: 'sarah.requester@nexus.enterprise',
          name: 'Sarah Connor (Requester)',
          organizationName: 'Acme Corp',
          roles: ['REQUESTER'],
        );
        break;
      case 'OPERATOR':
        demoUser = const UserModel(
          id: 'demo-op-1',
          email: 'elena.vance@nexus.enterprise',
          name: 'Elena Vance (Lead Operator)',
          organizationName: 'Global Support Center',
          roles: ['CASE_OPERATOR'],
        );
        break;
      case 'TEAM_LEAD':
        demoUser = const UserModel(
          id: 'demo-lead-1',
          email: 'marcus.lead@nexus.enterprise',
          name: 'Marcus Brody (Team Lead)',
          organizationName: 'IT Operations',
          roles: ['TEAM_LEAD'],
        );
        break;
      case 'MANAGER':
        demoUser = const UserModel(
          id: 'demo-mgr-1',
          email: 'rachel.manager@nexus.enterprise',
          name: 'Rachel Sterling (Operations Manager)',
          organizationName: 'Executive Suite',
          roles: ['MANAGER'],
        );
        break;
      case 'ADMIN':
      case 'ADMINISTRATOR':
      default:
        demoUser = const UserModel(
          id: 'demo-adm-1',
          email: 'gaurav.admin@nexus.enterprise',
          name: 'Gaurav Kadam (System Admin)',
          organizationName: 'Nexus Core Platform',
          roles: ['ADMINISTRATOR'],
        );
        break;
    }

    state = state.copyWith(
      isLoading: false,
      isAuthenticated: true,
      user: demoUser,
      errorMessage: null,
    );
  }

  Future<void> logout() async {
    await _repository.logout();
    state = const AuthState();
  }
}

final authRepositoryProvider = Provider<AuthRepository>((ref) => AuthRepository());

final authStateProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  final repo = ref.watch(authRepositoryProvider);
  return AuthNotifier(repo);
});
