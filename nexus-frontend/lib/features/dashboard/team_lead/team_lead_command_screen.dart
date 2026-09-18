import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-11: Team Lead Command & Workload Monitor Screen
/// Operations console for shift leaders featuring live capacity indicators,
/// dynamic operator workload cards, proactive AI rebalancing, and at-risk queues.
class TeamLeadCommandScreen extends ConsumerStatefulWidget {
  const TeamLeadCommandScreen({super.key});

  @override
  ConsumerState<TeamLeadCommandScreen> createState() => _TeamLeadCommandScreenState();
}

class _TeamLeadCommandScreenState extends ConsumerState<TeamLeadCommandScreen> {
  bool _isRebalanceDismissed = false;
  int _currentNavIndex = 0; // Lead / Triage active

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
                colors: [Color(0xFF6366F1), Color(0xFF818CF8)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.hub_outlined, color: Colors.white, size: 18),
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
                'Lead Command',
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
          icon: const Icon(Icons.search, color: AppColors.textSecondary, size: 22),
          onPressed: () => _showFeedbackToast('Global search activated', Icons.search, AppColors.accentPrimary),
        ),
        Stack(
          children: [
            IconButton(
              icon: const Icon(Icons.notifications_none, color: AppColors.textSecondary, size: 22),
              onPressed: () => _showFeedbackToast('3 alerts in escalation queue', Icons.notifications, const Color(0xFFE11D48)),
            ),
            Positioned(
              top: 10,
              right: 10,
              child: Container(
                width: 8,
                height: 8,
                decoration: const BoxDecoration(
                  color: Color(0xFFE11D48),
                  shape: BoxShape.circle,
                ),
              ),
            ),
          ],
        ),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 16,
            backgroundColor: const Color(0xFFEEF2FF),
            child: Text(
              'EV',
              style: AppTypography.bodySmall(context).copyWith(
                color: AppColors.accentPrimary,
                fontWeight: FontWeight.bold,
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
          _buildShiftControlHeader(context),
          const SizedBox(height: AppSpacing.sm),
          _buildShiftVitalMetricsGrid(context),
          const SizedBox(height: AppSpacing.sm),
          if (!_isRebalanceDismissed) ...[
            _buildAiWorkloadAlertBanner(context),
            const SizedBox(height: AppSpacing.sm),
          ],
          _buildOperatorsMonitorSection(context),
          const SizedBox(height: AppSpacing.sm),
          _buildAtRiskQueueSection(context),
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
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 7,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildShiftControlHeader(context),
                    const SizedBox(height: AppSpacing.md),
                    _buildShiftVitalMetricsGrid(context),
                    const SizedBox(height: AppSpacing.md),
                    if (!_isRebalanceDismissed) ...[
                      _buildAiWorkloadAlertBanner(context),
                      const SizedBox(height: AppSpacing.md),
                    ],
                    _buildOperatorsMonitorSection(context),
                  ],
                ),
              ),
              const SizedBox(width: AppSpacing.xl),
              Expanded(
                flex: 5,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildAtRiskQueueSection(context),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildShiftControlHeader(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.borderLight),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.02),
            blurRadius: 6,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Container(
                      width: 8,
                      height: 8,
                      decoration: const BoxDecoration(
                        color: Color(0xFF0D9488),
                        shape: BoxShape.circle,
                      ),
                    ),
                    const SizedBox(width: 6),
                    Text(
                      'LIVE SHIFT OPERATIONS',
                      style: AppTypography.bodySmall(context).copyWith(
                        color: const Color(0xFF0D9488),
                        fontSize: 10,
                        fontWeight: FontWeight.w700,
                        letterSpacing: 0.8,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  'SecOps & Core SRE Team',
                  style: AppTypography.headlineSmall(context).copyWith(
                    fontWeight: FontWeight.bold,
                    color: AppColors.textPrimary,
                    fontSize: 18,
                  ),
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    const Icon(Icons.schedule, size: 14, color: AppColors.textMuted),
                    const SizedBox(width: 4),
                    Text(
                      'EMEA Core (08:00 - 16:00 UTC) • 8 Operators',
                      style: AppTypography.bodySmall(context).copyWith(color: AppColors.textSecondary),
                    ),
                  ],
                ),
              ],
            ),
          ),
          InkWell(
            onTap: () => _showFeedbackToast('AI Auto-Rebalancing executed across all queues', Icons.auto_awesome, const Color(0xFF9333EA)),
            borderRadius: BorderRadius.circular(8),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFFFAF5FF),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFFC084FC).withOpacity(0.3)),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Icon(Icons.auto_awesome, size: 14, color: Color(0xFF9333EA)),
                  const SizedBox(width: 4),
                  Text(
                    'Auto-Rebalance',
                    style: AppTypography.bodySmall(context).copyWith(
                      color: const Color(0xFF9333EA),
                      fontWeight: FontWeight.w600,
                      fontSize: 11,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildShiftVitalMetricsGrid(BuildContext context) {
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
            _buildVitalMetricCard(
              title: 'OPERATORS',
              value: '8',
              badgeText: '100% Online',
              badgeColor: const Color(0xFF0D9488),
              badgeBg: const Color(0xFFCCFBF1),
              icon: Icons.groups_outlined,
            ),
            _buildVitalMetricCard(
              title: 'ACTIVE BACKLOG',
              value: '28',
              badgeText: '4 Unassigned',
              badgeColor: const Color(0xFFE11D48),
              badgeBg: const Color(0xFFFFE4E6),
              icon: Icons.inbox_outlined,
            ),
            _buildVitalMetricCard(
              title: 'SHIFT SLA',
              value: '97.6%',
              badgeText: '+2.6% vs Target',
              badgeColor: const Color(0xFF0D9488),
              badgeBg: const Color(0xFFCCFBF1),
              icon: Icons.verified_outlined,
            ),
            _buildVitalMetricCard(
              title: 'WORKLOAD POOL',
              value: 'Optimal',
              badgeText: '1 Critical Outlier',
              badgeColor: const Color(0xFFD97706),
              badgeBg: const Color(0xFFFEF3C7),
              icon: Icons.tune,
            ),
          ],
        );
      },
    );
  }

  Widget _buildVitalMetricCard({
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
              fontSize: 20,
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

  Widget _buildAiWorkloadAlertBanner(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFFFAF5FF),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0xFFC084FC).withOpacity(0.3)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('✨', style: TextStyle(fontSize: 18)),
          const SizedBox(width: AppSpacing.sm),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'AI Imbalance Detected',
                      style: AppTypography.bodyMedium(context).copyWith(
                        fontWeight: FontWeight.bold,
                        color: const Color(0xFF9333EA),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: const Text(
                        'AI Copilot',
                        style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Color(0xFF9333EA)),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  'David Ross is at 95% capacity (8 active cases). Proactive auto-routing recommends delegating 2 unassigned P2 tickets directly to Sarah Jenkins.',
                  style: AppTypography.bodySmall(context).copyWith(
                    color: AppColors.textSecondary,
                    height: 1.35,
                  ),
                ),
                const SizedBox(height: AppSpacing.sm),
                Row(
                  children: [
                    ElevatedButton.icon(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF9333EA),
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                        elevation: 0,
                      ),
                      icon: const Icon(Icons.check_circle_outline, size: 14),
                      label: const Text('Accept Plan', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                      onPressed: () {
                        setState(() => _isRebalanceDismissed = true);
                        _showFeedbackToast('Workload rebalance applied successfully', Icons.check_circle, const Color(0xFF0D9488));
                      },
                    ),
                    const SizedBox(width: AppSpacing.sm),
                    TextButton(
                      style: TextButton.styleFrom(
                        foregroundColor: AppColors.textSecondary,
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
                      ),
                      onPressed: () => _showFeedbackToast('Opening Rebalance Reviewer Studio', Icons.rate_review, AppColors.accentPrimary),
                      child: const Text('Review', style: TextStyle(fontSize: 12)),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOperatorsMonitorSection(BuildContext context) {
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
                'Active Operators Workload',
                style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold),
              ),
              TextButton(
                onPressed: () => _showFeedbackToast('Viewing all 8 rostered operators', Icons.groups, AppColors.accentPrimary),
                child: const Text('View All 8', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: AppColors.accentPrimary)),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildOperatorCard(
            name: 'Elena Vance',
            role: 'Lead Operator',
            activeCases: 3,
            maxCases: 6,
            statusLabel: 'AVAILABLE',
            statusColor: const Color(0xFF0D9488),
            statusBg: const Color(0xFFCCFBF1),
            avatarLetter: 'EV',
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildOperatorCard(
            name: 'David Ross',
            role: 'Senior SRE',
            activeCases: 8,
            maxCases: 8,
            statusLabel: 'OVERLOADED',
            statusColor: const Color(0xFFE11D48),
            statusBg: const Color(0xFFFFE4E6),
            avatarLetter: 'DR',
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildOperatorCard(
            name: 'Sarah Jenkins',
            role: 'SecOps Analyst',
            activeCases: 2,
            maxCases: 6,
            statusLabel: 'AVAILABLE',
            statusColor: const Color(0xFF0D9488),
            statusBg: const Color(0xFFCCFBF1),
            avatarLetter: 'SJ',
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildOperatorCard(
            name: 'Marcus Brody',
            role: 'Infra Specialist',
            activeCases: 5,
            maxCases: 6,
            statusLabel: 'OPTIMAL',
            statusColor: const Color(0xFF0284C7),
            statusBg: const Color(0xFFE0F2FE),
            avatarLetter: 'MB',
          ),
        ],
      ),
    );
  }

  Widget _buildOperatorCard({
    required String name,
    required String role,
    required int activeCases,
    required int maxCases,
    required String statusLabel,
    required Color statusColor,
    required Color statusBg,
    required String avatarLetter,
  }) {
    final progress = activeCases / maxCases;
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm + 2),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Column(
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 16,
                backgroundColor: statusBg,
                child: Text(
                  avatarLetter,
                  style: TextStyle(color: statusColor, fontWeight: FontWeight.bold, fontSize: 11),
                ),
              ),
              const SizedBox(width: AppSpacing.sm),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      name,
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: AppColors.textPrimary),
                    ),
                    Text(
                      role,
                      style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                    ),
                  ],
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: statusBg,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  statusLabel,
                  style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: statusColor),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              Expanded(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(4),
                  child: LinearProgressIndicator(
                    value: progress.clamp(0.0, 1.0),
                    backgroundColor: const Color(0xFFE2E8F0),
                    valueColor: AlwaysStoppedAnimation<Color>(statusColor),
                    minHeight: 5,
                  ),
                ),
              ),
              const SizedBox(width: AppSpacing.sm),
              Text(
                '$activeCases/$maxCases Cases',
                style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w600, color: AppColors.textSecondary),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildAtRiskQueueSection(BuildContext context) {
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
              Row(
                children: [
                  const Icon(Icons.warning_amber_rounded, color: Color(0xFFE11D48), size: 18),
                  const SizedBox(width: 6),
                  Text(
                    'At-Risk & Escalation Queue',
                    style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold),
                  ),
                ],
              ),
              InkWell(
                onTap: () => context.go('/sla/risk-console'),
                child: const Text(
                  'Open Radar ↗',
                  style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.accentPrimary),
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildAtRiskCaseItem(
            caseId: 'NEX-2026-0104',
            title: 'SSO Auth Gateway Failure in EU-West',
            severity: 'CRITICAL',
            assignee: 'David Ross',
            timeLeft: '⏳ 28m Left',
            isUrgent: true,
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildAtRiskCaseItem(
            caseId: 'NEX-2026-0102',
            title: 'Okta SCIM Sync Latency Spike',
            severity: 'HIGH',
            assignee: 'Elena Vance',
            timeLeft: '⏳ 1h 14m Left',
            isUrgent: false,
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildAtRiskCaseItem(
            caseId: 'NEX-2026-0099',
            title: 'Stripe Webhook Delivery Stalled',
            severity: 'HIGH',
            assignee: 'Unassigned',
            timeLeft: '⏳ 1h 45m Left',
            isUrgent: false,
          ),
        ],
      ),
    );
  }

  Widget _buildAtRiskCaseItem({
    required String caseId,
    required String title,
    required String severity,
    required String assignee,
    required String timeLeft,
    required bool isUrgent,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm + 2),
      decoration: BoxDecoration(
        color: isUrgent ? const Color(0xFFFFF1F2) : const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: isUrgent ? const Color(0xFFFDA4AF) : AppColors.borderLight),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                caseId,
                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 11, color: AppColors.accentPrimary),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: isUrgent ? const Color(0xFFFFE4E6) : const Color(0xFFFEF3C7),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  timeLeft,
                  style: TextStyle(
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                    color: isUrgent ? const Color(0xFFE11D48) : const Color(0xFFD97706),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            title,
            style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13, color: AppColors.textPrimary),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: 6),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Assignee: $assignee',
                style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
              ),
              InkWell(
                onTap: () => context.go('/cases/$caseId'),
                child: const Text(
                  'Triage Now',
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
            // Already on lead command
          } else if (index == 1) {
            context.go('/cases');
          } else if (index == 2) {
            context.go('/sla/risk-console');
          } else if (index == 3) {
            context.go('/dashboard/requester');
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
          BottomNavigationBarItem(icon: Icon(Icons.person_outline), label: 'Portal'),
        ],
      ),
    );
  }
}
