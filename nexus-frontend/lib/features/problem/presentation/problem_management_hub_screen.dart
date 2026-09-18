import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-13: Problem Management & Root Cause Hub Screen
/// ITIL v4 KEDB knowledge engine featuring autonomous AI vector anomaly clustering,
/// master problem records, linked incident graphs, and 5-Whys root-cause tracking.
class ProblemManagementHubScreen extends ConsumerStatefulWidget {
  const ProblemManagementHubScreen({super.key});

  @override
  ConsumerState<ProblemManagementHubScreen> createState() => _ProblemManagementHubScreenState();
}

class _ProblemManagementHubScreenState extends ConsumerState<ProblemManagementHubScreen> {
  final TextEditingController _searchController = TextEditingController();
  int _currentNavIndex = 3; // Problems / Admin tab

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _showFeedbackToast(String message, IconData icon, Color color) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        backgroundColor: const Color(0xFF131B2E),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        content: Row(
          children: [
            Icon(icon, color: color, size: 20),
            const SizedBox(width: AppSpacing.sm),
            Expanded(
              child: Text(
                message,
                style: AppTypography.bodySmall(context).copyWith(color: Colors.white),
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFAFAFC),
      appBar: _buildAppBar(context),
      body: SafeArea(
        child: ResponsiveLayout(
          mobileBody: _buildMobileBody(context),
          desktopBody: _buildDesktopBody(context),
        ),
      ),
      bottomNavigationBar: _buildBottomNav(context),
    );
  }

  PreferredSizeWidget _buildAppBar(BuildContext context) {
    return AppBar(
      backgroundColor: Colors.white.withOpacity(0.9),
      elevation: 0,
      scrolledUnderElevation: 1,
      titleSpacing: AppSpacing.md,
      title: Row(
        children: [
          Container(
            width: 32,
            height: 32,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF9333EA), Color(0xFFC084FC)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.psychology_outlined, color: Colors.white, size: 18),
          ),
          const SizedBox(width: AppSpacing.sm),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Nexus AI',
                style: AppTypography.bodySmall(context).copyWith(
                  color: AppColors.textMuted,
                  fontSize: 11,
                  fontWeight: FontWeight.w600,
                  letterSpacing: 0.5,
                ),
              ),
              Text(
                'Problem Hub',
                style: AppTypography.headlineSmall(context).copyWith(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: AppColors.textPrimary,
                ),
              ),
            ],
          ),
        ],
      ),
      actions: [
        IconButton(
          icon: const Icon(Icons.add_box_outlined, color: AppColors.accentPrimary, size: 22),
          onPressed: () => _showFeedbackToast('Opening Create Problem Record wizard', Icons.add_circle, AppColors.accentPrimary),
        ),
        IconButton(
          icon: const Icon(Icons.notifications_none, color: AppColors.textSecondary, size: 22),
          onPressed: () => _showFeedbackToast('No unacknowledged recurring anomaly alerts', Icons.notifications_active, const Color(0xFF0D9488)),
        ),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 16,
            backgroundColor: const Color(0xFFFAF5FF),
            child: const Text(
              'PM',
              style: TextStyle(
                color: Color(0xFF9333EA),
                fontWeight: FontWeight.bold,
                fontSize: 11,
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildMobileBody(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: AppSpacing.md, vertical: AppSpacing.sm),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildKedbHeaderStrip(context),
          const SizedBox(height: AppSpacing.sm),
          _buildKpiMetricsGrid(context),
          const SizedBox(height: AppSpacing.sm),
          _buildAiVectorAnomalyCard(context),
          const SizedBox(height: AppSpacing.sm),
          _buildMasterProblemsSection(context),
          const SizedBox(height: AppSpacing.xl),
        ],
      ),
    );
  }

  Widget _buildDesktopBody(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.xl),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildKedbHeaderStrip(context),
          const SizedBox(height: AppSpacing.md),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 5,
                child: Column(
                  children: [
                    _buildKpiMetricsGrid(context),
                    const SizedBox(height: AppSpacing.md),
                    _buildAiVectorAnomalyCard(context),
                  ],
                ),
              ),
              const SizedBox(width: AppSpacing.xl),
              Expanded(
                flex: 7,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildMasterProblemsSection(context),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildKedbHeaderStrip(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: AppSpacing.md, vertical: AppSpacing.sm),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                decoration: BoxDecoration(
                  color: const Color(0xFFF3E8FF),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: const [
                    Icon(Icons.sync, size: 12, color: Color(0xFF7C3AED)),
                    SizedBox(width: 4),
                    Text(
                      'ITIL v4 KEDB Synced',
                      style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Color(0xFF7C3AED)),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 8),
              const Text(
                'Vector Space Active',
                style: TextStyle(fontSize: 11, color: AppColors.textSecondary),
              ),
            ],
          ),
          Container(
            width: 8,
            height: 8,
            decoration: const BoxDecoration(
              color: Color(0xFF0D9488),
              shape: BoxShape.circle,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildKpiMetricsGrid(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final isWide = constraints.maxWidth > 500;
        return GridView.count(
          crossAxisCount: isWide ? 4 : 2,
          crossAxisSpacing: AppSpacing.sm,
          mainAxisSpacing: AppSpacing.sm,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          childAspectRatio: isWide ? 1.5 : 1.35,
          children: [
            _buildKpiCard(
              title: 'ACTIVE PROBLEMS',
              value: '18',
              badgeText: '4 in 5-Whys',
              badgeColor: const Color(0xFFD97706),
              badgeBg: const Color(0xFFFEF3C7),
              icon: Icons.psychology_outlined,
            ),
            _buildKpiCard(
              title: 'LINKED INCIDENTS',
              value: '142',
              badgeText: '-12% MoM',
              badgeColor: const Color(0xFF0D9488),
              badgeBg: const Color(0xFFCCFBF1),
              icon: Icons.hub_outlined,
            ),
            _buildKpiCard(
              title: 'PATTERN DEFENSE',
              value: '84%',
              badgeText: 'Mitigated',
              badgeColor: const Color(0xFF0D9488),
              badgeBg: const Color(0xFFCCFBF1),
              icon: Icons.security_outlined,
            ),
            _buildKpiCard(
              title: 'AVG RCA TIME',
              value: '1.8d',
              badgeText: 'Target <3.0d',
              badgeColor: const Color(0xFF6366F1),
              badgeBg: const Color(0xFFEEF2FF),
              icon: Icons.timer_outlined,
            ),
          ],
        );
      },
    );
  }

  Widget _buildKpiCard({
    required String title,
    required String value,
    required String badgeText,
    required Color badgeColor,
    required Color badgeBg,
    required IconData icon,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm + 2),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                title,
                style: const TextStyle(
                  fontSize: 10,
                  fontWeight: FontWeight.w700,
                  color: AppColors.textSecondary,
                  letterSpacing: 0.5,
                ),
              ),
              Icon(icon, size: 16, color: badgeColor),
            ],
          ),
          Text(
            value,
            style: const TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.bold,
              color: AppColors.textPrimary,
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: BoxDecoration(
              color: badgeBg,
              borderRadius: BorderRadius.circular(4),
            ),
            child: Text(
              badgeText,
              style: TextStyle(
                fontSize: 10,
                fontWeight: FontWeight.w600,
                color: badgeColor,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAiVectorAnomalyCard(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFFFAF5FF),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0xFFC084FC).withOpacity(0.3)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: const [
                  Text('✨', style: TextStyle(fontSize: 16)),
                  SizedBox(width: 6),
                  Text(
                    'Cluster #VEC-098 Anomaly',
                    style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Color(0xFF9333EA)),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: const Color(0xFFF3E8FF),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text('96.4% Similarity', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Color(0xFF9333EA))),
              ),
            ],
          ),
          const SizedBox(height: 6),
          Text(
            '3 incidents in the last 48h share identical Redis cache token pool exhaustion signatures during Okta SSO handshake.',
            style: AppTypography.bodySmall(context).copyWith(color: AppColors.textSecondary),
          ),
          const SizedBox(height: 8),
          Wrap(
            spacing: 6,
            children: [
              _buildCasePill('NEX-2026-0104'),
              _buildCasePill('NEX-2026-0098'),
              _buildCasePill('NEX-2026-0087'),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              OutlinedButton.icon(
                style: OutlinedButton.styleFrom(
                  foregroundColor: const Color(0xFF9333EA),
                  side: const BorderSide(color: Color(0xFFC084FC)),
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
                ),
                icon: const Icon(Icons.bubble_chart_outlined, size: 14),
                label: const Text('Inspect Vector', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
                onPressed: () => _showFeedbackToast('Vector distance graph rendered in 3D canvas', Icons.scatter_plot, const Color(0xFF9333EA)),
              ),
              const SizedBox(width: 8),
              ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.accentPrimary,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
                  elevation: 0,
                ),
                icon: const Icon(Icons.add_circle_outline, size: 14),
                label: const Text('Create Problem', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
                onPressed: () => _showFeedbackToast('Master Problem Record PRB-2026-0034 created', Icons.check_circle, const Color(0xFF0D9488)),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCasePill(String caseId) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Text(
        caseId,
        style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: AppColors.accentPrimary, fontFamily: 'monospace'),
      ),
    );
  }

  Widget _buildMasterProblemsSection(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Master Problem Records',
                style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold),
              ),
              const Text(
                '3 of 18 Active',
                style: TextStyle(fontSize: 11, color: AppColors.textSecondary),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildProblemRecordCard(
            problemId: 'PRB-2026-0031',
            title: 'Redis Token Pool Exhaustion on Okta SSO',
            category: 'Infrastructure',
            severity: 'CRITICAL',
            linkedIncidentsCount: 8,
            kedbStatus: 'WORKAROUND PUBLISHED',
            rootCause: 'Connection pool limit default set to 1024 rather than 4096 in Kubernetes pod config.',
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildProblemRecordCard(
            problemId: 'PRB-2026-0029',
            title: 'Stripe Webhook Exponential Backoff Hang',
            category: 'Billing Core',
            severity: 'HIGH',
            linkedIncidentsCount: 5,
            kedbStatus: 'ROOT CAUSE CONFIRMED',
            rootCause: 'TLS handshake timeout in webhook ingress gateway drops retry payload headers.',
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildProblemRecordCard(
            problemId: 'PRB-2026-0024',
            title: 'Okta SCIM Sync User Deprovision Delay',
            category: 'Identity & Access',
            severity: 'MEDIUM',
            linkedIncidentsCount: 3,
            kedbStatus: 'UNDER INVESTIGATION',
            rootCause: 'Batch cron query lock contention on user entity table during peak hours.',
          ),
        ],
      ),
    );
  }

  Widget _buildProblemRecordCard({
    required String problemId,
    required String title,
    required String category,
    required String severity,
    required int linkedIncidentsCount,
    required String kedbStatus,
    required String rootCause,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Text(
                    problemId,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: AppColors.accentPrimary),
                  ),
                  const SizedBox(width: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                    decoration: BoxDecoration(
                      color: const Color(0xFFEEF2FF),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: Text(
                      category,
                      style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: AppColors.accentPrimary),
                    ),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: const Color(0xFFCCFBF1),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  kedbStatus,
                  style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Color(0xFF0D9488)),
                ),
              ),
            ],
          ),
          const SizedBox(height: 6),
          Text(
            title,
            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: AppColors.textPrimary),
          ),
          const SizedBox(height: 4),
          Text(
            'Root Cause: $rootCause',
            style: const TextStyle(fontSize: 12, color: AppColors.textSecondary, height: 1.3),
          ),
          const SizedBox(height: 8),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  const Icon(Icons.hub_outlined, size: 14, color: AppColors.textSecondary),
                  const SizedBox(width: 4),
                  Text(
                    '$linkedIncidentsCount Linked Incidents',
                    style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w600, color: AppColors.textSecondary),
                  ),
                ],
              ),
              InkWell(
                onTap: () => _showFeedbackToast('Opening RCA Investigation Studio for $problemId', Icons.open_in_new, AppColors.accentPrimary),
                child: const Text(
                  'View RCA Studio ↗',
                  style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: AppColors.accentPrimary),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildBottomNav(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(top: BorderSide(color: AppColors.borderLight)),
      ),
      child: BottomNavigationBar(
        currentIndex: _currentNavIndex,
        onTap: (index) {
          setState(() => _currentNavIndex = index);
          if (index == 0) {
            context.go('/dashboard/team-lead');
          } else if (index == 1) {
            context.go('/cases');
          } else if (index == 2) {
            context.go('/sla/risk-console');
          } else if (index == 3) {
            // Already on problems
          }
        },
        selectedItemColor: AppColors.accentPrimary,
        unselectedItemColor: AppColors.textSecondary,
        type: BottomNavigationBarType.fixed,
        selectedFontSize: 11,
        unselectedFontSize: 11,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.dashboard_outlined), label: 'Lead'),
          BottomNavigationBarItem(icon: Icon(Icons.inbox_outlined), label: 'Feed'),
          BottomNavigationBarItem(icon: Icon(Icons.radar_outlined), label: 'Radar'),
          BottomNavigationBarItem(icon: Icon(Icons.psychology_outlined), label: 'Problems'),
        ],
      ),
    );
  }
}
