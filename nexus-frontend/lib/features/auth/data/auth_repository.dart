import '../../../core/network/api_client.dart';
import '../domain/user_model.dart';
import 'auth_api.dart';

class AuthRepository {
  final AuthApi _api = AuthApi();
  final ApiClient _client = ApiClient();

  Future<({bool success, UserModel? user, String? error})> login({
    required String email,
    required String password,
  }) async {
    final response = await _api.login(email: email, password: password);
    if (response.success && response.data != null) {
      final data = response.data!;
      final accessToken = data['accessToken'] as String? ?? data['token'] as String?;
      final refreshToken = data['refreshToken'] as String? ?? '';

      if (accessToken != null) {
        await _client.saveTokens(accessToken: accessToken, refreshToken: refreshToken);
      }

      final userData = data['user'] != null
          ? UserModel.fromJson(data['user'] as Map<String, dynamic>)
          : UserModel(id: 'usr-1', email: email, name: email.split('@').first, roles: ['OPERATOR']);

      return (success: true, user: userData, error: null);
    }
    return (success: false, user: null, error: response.error ?? 'Authentication failed');
  }

  Future<({bool success, String? error})> register({
    required String name,
    required String email,
    required String password,
    String? organizationId,
    String? role,
  }) async {
    final response = await _api.register(
      name: name,
      email: email,
      password: password,
      organizationId: organizationId,
      role: role,
    );
    if (response.success) {
      return (success: true, error: null);
    }
    return (success: false, error: response.error ?? 'Registration failed');
  }

  Future<void> logout() async {
    await _client.clearTokens();
  }
}
