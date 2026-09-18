import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-16: Admin SLA Policy & Escalation Rule Builder Screen
/// Dynamic SLA policy engine configuration, multi-tier matrix (P1-P4),
/// breach notification rules, and AI automated threshold optimization.
class AdminSlaPolicyBuilderScreen extends ConsumerStatefulWidget {
  const AdminSlaPolicyBuilderScreen({super.key});

  @override
  ConsumerState<AdminSlaPolicyBuilderScreen> createState() => _AdminSlaPolicyBuilderScreenState();
}

class _AdminSlaPolicyBuilderScreenState extends ConsumerState<AdminSlaPolicyBuilderScreen> {
  bool _isBufferApplied = false;
  int _currentNavIndex = 2; // Policies active

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
            child: const Icon(Icons.policy_outlined, color: Colors.white, size: 18),
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
                'SLA Policy Engine',
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
          icon: const Icon(Icons.history_edu_outlined, color: AppColors.textSecondary, size: 22),
          tooltip: 'Audit Ledger',
          onPressed: () => context.go('/admin/audit-logs'),
        ),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 16,
            backgroundColor: const Color(0xFFEEF2FF),
            child: const Text(
              'PL',
              style: TextStyle(
                color: AppColors.accentPrimary,
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
          _buildPolicyHeaderActions(context),
          const SizedBox(height: AppSpacing.sm),
          if (!_isBufferApplied) ...[
            _buildAiPolicyOptimizerBanner(context),
            const SizedBox(height: AppSpacing.sm),
          ],
          _buildSlaMatrixSection(context),
          const SizedBox(height: AppSpacing.sm),
          _buildEscalationChainSection(context),
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
          _buildPolicyHeaderActions(context),
          const SizedBox(height: AppSpacing.md),
          if (!_isBufferApplied) ...[
            _buildAiPolicyOptimizerBanner(context),
            const SizedBox(height: AppSpacing.md),
          ],
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 7,
                child: _buildSlaMatrixSection(context),
              ),
              const SizedBox(width: AppSpacing.xl),
              Expanded(
                flex: 5,
                child: _buildEscalationChainSection(context),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildPolicyHeaderActions(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Admin SLA Policies & Rules',
              style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold),
            ),
            const Text(
              'Configure thresholds & on-call rules',
              style: TextStyle(fontSize: 11, color: AppColors.textSecondary),
            ),
          ],
        ),
        ElevatedButton.icon(
          style: ElevatedButton.styleFrom(
            backgroundColor: AppColors.accentPrimary,
            foregroundColor: Colors.white,
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
            elevation: 0,
          ),
          icon: const Icon(Icons.add, size: 14),
          label: const Text('+ New Policy', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
          onPressed: () => _showFeedbackToast('Opening New SLA Policy Wizard', Icons.add_circle, AppColors.accentPrimary),
        ),
      ],
    );
  }

  Widget _buildAiPolicyOptimizerBanner(BuildContext context) {
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
                    'AI Policy Optimizer',
                    style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Color(0xFF9333EA)),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: const Color(0xFFFFE4E6),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text('92% Breach Spike', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Color(0xFFE11D48))),
              ),
            ],
          ),
          const SizedBox(height: 6),
          Text(
            'Telemetry indicates 92% of P1 SSO cases breach during APAC handoff (03:00-05:30 UTC). Proactive recommendation: Extend standby buffer by +15 mins.',
            style: AppTypography.bodySmall(context).copyWith(color: AppColors.textSecondary),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.accentPrimary,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
                  elevation: 0,
                ),
                icon: const Icon(Icons.check_circle_outline, size: 14),
                label: const Text('Apply Buffer (+15m)', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
                onPressed: () {
                  setState(() => _isBufferApplied = true);
                  _showFeedbackToast('APAC standby buffer extended by +15 mins', Icons.check_circle, const Color(0xFF0D9488));
                },
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildSlaMatrixSection(BuildContext context) {
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
              Text('Operational SLA Matrix', style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold)),
              const Text('3 Tiers Configured', style: TextStyle(fontSize: 11, color: AppColors.textSecondary)),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildSlaMatrixCard(
            priority: 'P1 CRITICAL',
            scope: '24x7 Mission Critical',
            firstTouch: '15 mins',
            resolution: '4 hours',
            score30d: '98.4%',
            priorityColor: const Color(0xFFE11D48),
            priorityBg: const Color(0xFFFFE4E6),
            scoreColor: const Color(0xFF0D9488),
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildSlaMatrixCard(
            priority: 'P2 HIGH',
            scope: 'Standard Ops Follow-up',
            firstTouch: '45 mins',
            resolution: '12 hours',
            score30d: '95.1%',
            priorityColor: const Color(0xFFD97706),
            priorityBg: const Color(0xFFFEF3C7),
            scoreColor: const Color(0xFFD97706),
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildSlaMatrixCard(
            priority: 'P3 MEDIUM',
            scope: 'Normal Service Request',
            firstTouch: '2 hours',
            resolution: '24 hours',
            score30d: '99.2%',
            priorityColor: const Color(0xFF0284C7),
            priorityBg: const Color(0xFFE0F2FE),
            scoreColor: const Color(0xFF0D9488),
          ),
        ],
      ),
    );
  }

  Widget _buildSlaMatrixCard({
    required String priority,
    required String scope,
    required String firstTouch,
    required String resolution,
    required String score30d,
    required Color priorityColor,
    required Color priorityBg,
    required Color scoreColor,
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
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                decoration: BoxDecoration(
                  color: priorityBg,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  priority,
                  style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: priorityColor),
                ),
              ),
              Text(scope, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
            ],
          ),
          const SizedBox(height: 8),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('First Touch', style: TextStyle(fontSize: 10, color: AppColors.textMuted)),
                  Text(firstTouch, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
                ],
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Resolution', style: TextStyle(fontSize: 10, color: AppColors.textMuted)),
                  Text(resolution, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
                ],
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('30d Score', style: TextStyle(fontSize: 10, color: AppColors.textMuted)),
                  Text(score30d, style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: scoreColor)),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildEscalationChainSection(BuildContext context) {
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
          Text('Escalation Chain Flow', style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          _buildChainStep('Tier 1: Shift Lead Notification', 'At 75% SLA threshold (In-App + Slack)', const Color(0xFFD97706)),
          const SizedBox(height: 8),
          _buildChainStep('Tier 2: Incident Commander Bridge', 'At 90% SLA threshold (PagerDuty + Bridge)', const Color(0xFFE11D48)),
          const SizedBox(height: 8),
          _buildChainStep('Tier 3: Executive War Room', 'At 100% Breached (VP On-Call + Bridge)', const Color(0xFF7C3AED)),
        ],
      ),
    );
  }

  Widget _buildChainStep(String title, String condition, Color color) {
    return Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Row(
        children: [
          Container(width: 6, height: 6, decoration: BoxDecoration(color: color, shape: BoxShape.circle)),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
                Text(condition, style: const TextStyle(fontSize: 10, color: AppColors.textSecondary)),
              ],
            ),
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
            // Already on policies
          } else if (index == 3) {
            context.go('/admin/audit-logs');
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
          BottomNavigationBarItem(icon: Icon(Icons.policy_outlined), label: 'Policies'),
          BottomNavigationBarItem(icon: Icon(Icons.history_edu_outlined), label: 'Audit'),
        ],
      ),
    );
  }
}
