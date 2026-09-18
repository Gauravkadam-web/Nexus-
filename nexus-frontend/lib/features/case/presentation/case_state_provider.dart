import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../data/case_repository.dart';
import '../domain/case_model.dart';

class CaseState {
  final bool isLoading;
  final RequesterDashboardStats? requesterStats;
  final List<CaseModel> cases;
  final String? errorMessage;

  const CaseState({
    this.isLoading = false,
    this.requesterStats,
    this.cases = const [],
    this.errorMessage,
  });

  CaseState copyWith({
    bool? isLoading,
    RequesterDashboardStats? requesterStats,
    List<CaseModel>? cases,
    String? errorMessage,
  }) {
    return CaseState(
      isLoading: isLoading ?? this.isLoading,
      requesterStats: requesterStats ?? this.requesterStats,
      cases: cases ?? this.cases,
      errorMessage: errorMessage,
    );
  }
}

class CaseNotifier extends StateNotifier<CaseState> {
  final CaseRepository _repo;

  CaseNotifier(this._repo) : super(const CaseState()) {
    loadRequesterData();
  }

  Future<void> loadRequesterData() async {
    state = state.copyWith(isLoading: true, errorMessage: null);

    final statsRes = await _repo.getRequesterDashboard();
    final casesRes = await _repo.getMyCases();

    state = state.copyWith(
      isLoading: false,
      requesterStats: statsRes.stats,
      cases: casesRes.cases,
      errorMessage: statsRes.error ?? casesRes.error,
    );
  }

  Future<bool> createCase({
    required String title,
    required String description,
    required String categoryId,
    required String severity,
  }) async {
    state = state.copyWith(isLoading: true, errorMessage: null);
    final result = await _repo.createCase(
      title: title,
      description: description,
      categoryId: categoryId,
      severity: severity,
    );

    if (result.success && result.newCase != null) {
      state = state.copyWith(
        isLoading: false,
        cases: [result.newCase!, ...state.cases],
      );
      return true;
    } else {
      state = state.copyWith(
        isLoading: false,
        errorMessage: result.error,
      );
      return false;
    }
  }
}

final caseRepositoryProvider = Provider<CaseRepository>((ref) => CaseRepository());

final caseStateProvider = StateNotifierProvider<CaseNotifier, CaseState>((ref) {
  final repo = ref.watch(caseRepositoryProvider);
  return CaseNotifier(repo);
});
