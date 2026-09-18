import '../domain/case_model.dart';
import 'case_api.dart';

class CaseRepository {
  final CaseApi _api = CaseApi();

  Future<({bool success, RequesterDashboardStats? stats, String? error})> getRequesterDashboard() async {
    final res = await _api.getRequesterDashboard();
    if (res.success && res.data != null) {
      return (success: true, stats: res.data, error: null);
    }
    return (success: false, stats: null, error: res.error);
  }

  Future<({bool success, List<CaseModel> cases, String? error})> getMyCases() async {
    final res = await _api.getMyCases();
    if (res.success && res.data != null) {
      return (success: true, cases: res.data!, error: null);
    }
    return (success: false, cases: <CaseModel>[], error: res.error);
  }

  Future<({bool success, CaseModel? newCase, String? error})> createCase({
    required String title,
    required String description,
    required String categoryId,
    required String severity,
  }) async {
    final res = await _api.createCase(
      title: title,
      description: description,
      categoryId: categoryId,
      severity: severity,
    );
    if (res.success && res.data != null) {
      return (success: true, newCase: res.data, error: null);
    }
    return (success: false, newCase: null, error: res.error ?? 'Creation failed');
  }
}
