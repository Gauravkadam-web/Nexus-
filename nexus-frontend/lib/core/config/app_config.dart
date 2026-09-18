class AppConfig {
  AppConfig._();

  /// Default API base URL for Nexus Spring Boot 3.3.4 Backend
  static const String defaultApiBaseUrl = 'http://localhost:8080/api/v1';

  /// Environment-driven API base URL
  static String get apiBaseUrl {
    const fromEnv = String.fromEnvironment('API_BASE_URL');
    if (fromEnv.isNotEmpty) {
      return fromEnv;
    }
    return defaultApiBaseUrl;
  }

  static const String appName = 'Nexus Case Management';
  static const String appVersion = 'v3.0';
}
