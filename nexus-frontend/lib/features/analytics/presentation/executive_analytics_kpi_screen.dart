import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-14: Executive Analytics & KPI Command Center Screen
/// Executive governance dashboard providing multi-dimensional telemetry,
/// rolling inflow velocity trends, SLA compliance distributions, and automated Copilot strategic digests.
class ExecutiveAnalyticsKpiScreen extends ConsumerStatefulWidget {
  const ExecutiveAnalyticsKpiScreen({super.key});

  @override
  ConsumerState<ExecutiveAnalyticsKpiScreen> createState() => _ExecutiveAnalyticsKpiScreenState();
}

class _ExecutiveAnalyticsKpiScreenState extends ConsumerState<ExecutiveAnalyticsKpiScreen> {
  String _selectedRange = '30D'; // 7D, 30D, QTD, 2026
  int _currentNavIndex = 0; // Analytics active

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
                colors: [Color(0xFF0284C7), Color(0xFF38BDF8)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.analytics_outlined, color: Colors.white, size: 18),
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
                'KPI Command',
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
          icon: const Icon(Icons.picture_as_pdf_outlined, color: AppColors.accentPrimary, size: 22),
          onPressed: () => _showFeedbackToast('Generating Executive KPI Report PDF', Icons.downloading, AppColors.accentPrimary),
        ),
        IconButton(
          icon: const Icon(Icons.share_outlined, color: AppColors.textSecondary, size: 22),
          onPressed: () => _showFeedbackToast('Digest link copied to clipboard', Icons.link, const Color(0xFF0D9488)),
        ),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 16,
            backgroundColor: const Color(0xFFE0F2FE),
            child: const Text(
              'EX',
              style: TextStyle(
                color: Color(0xFF0284C7),
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
          _buildRangeSelectorStrip(context),
          const SizedBox(height: AppSpacing.sm),
          _buildExecutiveMetricTilesGrid(context),
          const SizedBox(height: AppSpacing.sm),
          _buildAiExecutiveDigestCard(context),
          const SizedBox(height: AppSpacing.sm),
          _buildVolumeDynamicsCard(context),
          const SizedBox(height: AppSpacing.sm),
          _buildDepartmentSlaRankingsCard(context),
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
          _buildRangeSelectorStrip(context),
          const SizedBox(height: AppSpacing.md),
          _buildExecutiveMetricTilesGrid(context),
          const SizedBox(height: AppSpacing.md),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 6,
                child: Column(
                  children: [
                    _buildAiExecutiveDigestCard(context),
                    const SizedBox(height: AppSpacing.md),
                    _buildVolumeDynamicsCard(context),
                  ],
                ),
              ),
              const SizedBox(width: AppSpacing.xl),
              Expanded(
                flex: 6,
                child: Column(
                  children: [
                    _buildDepartmentSlaRankingsCard(context),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildRangeSelectorStrip(BuildContext context) {
    final ranges = [
      {'id': '7D', 'label': 'Last 7D'},
      {'id': '30D', 'label': 'Last 30D'},
      {'id': 'QTD', 'label': 'QTD'},
      {'id': '2026', 'label': '2026 YTD'},
    ];

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Row(
          children: ranges.map((r) {
            final isSelected = _selectedRange == r['id'];
            return Padding(
              padding: const EdgeInsets.only(right: 6),
              child: ChoiceChip(
                label: Text(
                  r['label']!,
                  style: TextStyle(
                    fontSize: 11,
                    fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                    color: isSelected ? AppColors.accentPrimary : AppColors.textSecondary,
                  ),
                ),
                selected: isSelected,
                selectedColor: const Color(0xFFEEF2FF),
                backgroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(20),
                  side: BorderSide(
                    color: isSelected ? AppColors.accentPrimary : AppColors.borderLight,
                  ),
                ),
                onSelected: (selected) {
                  if (selected) {
                    setState(() => _selectedRange = r['id']!);
                    _showFeedbackToast('Metrics filtered by ${r['label']}', Icons.filter_alt, AppColors.accentPrimary);
                  }
                },
              ),
            );
          }).toList(),
        ),
        Row(
          children: [
            Container(width: 6, height: 6, decoration: const BoxDecoration(color: Color(0xFF0D9488), shape: BoxShape.circle)),
            const SizedBox(width: 4),
            const Text('Sync: 1m ago', style: TextStyle(fontSize: 10, color: AppColors.textMuted)),
          ],
        ),
      ],
    );
  }

  Widget _buildExecutiveMetricTilesGrid(BuildContext context) {
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
            _buildMetricTile(
              title: 'Total Inflow',
              value: '1,248',
              badgeText: '+8.4% vs prev',
              badgeColor: const Color(0xFF0284C7),
              badgeBg: const Color(0xFFE0F2FE),
              icon: Icons.inbox_outlined,
            ),
            _buildMetricTile(
              title: 'MTTR Mean',
              value: '3.4 hrs',
              badgeText: '-42m accelerated',
              badgeColor: const Color(0xFF0D9488),
              badgeBg: const Color(0xFFCCFBF1),
              icon: Icons.speed_outlined,
            ),
            _buildMetricTile(
              title: 'SLA Compliance',
              value: '96.8%',
              badgeText: 'Target 95.0%',
              badgeColor: const Color(0xFF0D9488),
              badgeBg: const Color(0xFFCCFBF1),
              icon: Icons.verified_outlined,
            ),
            _buildMetricTile(
              title: 'First-Contact Res',
              value: '72.4%',
              badgeText: '+4.1% AI flow',
              badgeColor: const Color(0xFF9333EA),
              badgeBg: const Color(0xFFFAF5FF),
              icon: Icons.auto_awesome,
            ),
          ],
        );
      },
    );
  }

  Widget _buildMetricTile({
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
                  fontSize: 11,
                  fontWeight: FontWeight.w600,
                  color: AppColors.textSecondary,
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
                fontWeight: FontWeight.bold,
                color: badgeColor,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAiExecutiveDigestCard(BuildContext context) {
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
                    'Copilot 3.0 Executive Briefing',
                    style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Color(0xFF9333EA)),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text('Realtime', style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Color(0xFF9333EA))),
              ),
            ],
          ),
          const SizedBox(height: 8),
          _buildDigestBullet(
            color: const Color(0xFFE11D48),
            text: 'SSO Gateway timeouts accounted for 34% of high-severity spikes this week.',
          ),
          const SizedBox(height: 6),
          _buildDigestBullet(
            color: const Color(0xFF0D9488),
            text: 'Operator auto-rebalancing prevented 12 potential SLA breaches in APAC handoff.',
          ),
          const SizedBox(height: 6),
          _buildDigestBullet(
            color: const Color(0xFF9333EA),
            text: '78% of resolution proposals were accepted without modification by lead responders.',
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF9333EA),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
                  elevation: 0,
                ),
                icon: const Icon(Icons.mark_email_read_outlined, size: 14),
                label: const Text('Broadcast Digest', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
                onPressed: () => _showFeedbackToast('Executive digest broadcasted to Slack & Leadership email', Icons.send, const Color(0xFF0D9488)),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildDigestBullet({required Color color, required String text}) {
    return Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.7),
        borderRadius: BorderRadius.circular(6),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: 6,
            height: 6,
            margin: const EdgeInsets.only(top: 5, right: 8),
            decoration: BoxDecoration(color: color, shape: BoxShape.circle),
          ),
          Expanded(
            child: Text(
              text,
              style: const TextStyle(fontSize: 12, color: AppColors.textPrimary, height: 1.3),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildVolumeDynamicsCard(BuildContext context) {
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
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('Volume Dynamics', style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold)),
                  const Text('Inflow vs Closed Velocity', style: TextStyle(fontSize: 11, color: AppColors.textSecondary)),
                ],
              ),
              Row(
                children: [
                  _buildLegendIndicator('Inflow', AppColors.accentPrimary),
                  const SizedBox(width: 8),
                  _buildLegendIndicator('Resolved', const Color(0xFF0D9488)),
                ],
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          // Clean weekly bar indicators
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              _buildBarColumn('W1', 0.6, 0.5),
              _buildBarColumn('W2', 0.8, 0.75),
              _buildBarColumn('W3', 0.95, 0.9),
              _buildBarColumn('W4', 0.7, 0.72),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildLegendIndicator(String label, Color color) {
    return Row(
      children: [
        Container(width: 8, height: 8, decoration: BoxDecoration(color: color, shape: BoxShape.circle)),
        const SizedBox(width: 4),
        Text(label, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
      ],
    );
  }

  Widget _buildBarColumn(String week, double inflowRatio, double resolvedRatio) {
    return Column(
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Container(
              width: 14,
              height: 70 * inflowRatio,
              decoration: BoxDecoration(
                color: AppColors.accentPrimary,
                borderRadius: BorderRadius.circular(3),
              ),
            ),
            const SizedBox(width: 4),
            Container(
              width: 14,
              height: 70 * resolvedRatio,
              decoration: BoxDecoration(
                color: const Color(0xFF0D9488),
                borderRadius: BorderRadius.circular(3),
              ),
            ),
          ],
        ),
        const SizedBox(height: 6),
        Text(week, style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: AppColors.textSecondary)),
      ],
    );
  }

  Widget _buildDepartmentSlaRankingsCard(BuildContext context) {
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
              Text('Department SLA Compliance', style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold)),
              const Text('Ranked by Speed', style: TextStyle(fontSize: 11, color: AppColors.textSecondary)),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildDeptSlaRow('Core Infrastructure (SRE)', 98.4, const Color(0xFF0D9488)),
          const SizedBox(height: 8),
          _buildDeptSlaRow('Identity & Security', 96.2, const Color(0xFF0284C7)),
          const SizedBox(height: 8),
          _buildDeptSlaRow('Billing & Payments', 92.5, const Color(0xFFD97706)),
          const SizedBox(height: 8),
          _buildDeptSlaRow('Corporate IT Services', 95.8, const Color(0xFF0D9488)),
        ],
      ),
    );
  }

  Widget _buildDeptSlaRow(String department, double compliance, Color color) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(department, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: AppColors.textPrimary)),
            Text('$compliance%', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: color)),
          ],
        ),
        const SizedBox(height: 4),
        ClipRRect(
          borderRadius: BorderRadius.circular(3),
          child: LinearProgressIndicator(
            value: compliance / 100,
            backgroundColor: const Color(0xFFE2E8F0),
            valueColor: AlwaysStoppedAnimation<Color>(color),
            minHeight: 5,
          ),
        ),
      ],
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
            // Already on analytics
          } else if (index == 1) {
            context.go('/cases');
          } else if (index == 2) {
            context.go('/problems');
          } else if (index == 3) {
            context.go('/dashboard/team-lead');
          }
        },
        selectedItemColor: AppColors.accentPrimary,
        unselectedItemColor: AppColors.textSecondary,
        type: BottomNavigationBarType.fixed,
        selectedFontSize: 11,
        unselectedFontSize: 11,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.analytics_outlined), label: 'KPIs'),
          BottomNavigationBarItem(icon: Icon(Icons.inbox_outlined), label: 'Feed'),
          BottomNavigationBarItem(icon: Icon(Icons.psychology_outlined), label: 'Problems'),
          BottomNavigationBarItem(icon: Icon(Icons.dashboard_outlined), label: 'Lead'),
        ],
      ),
    );
  }
}
