import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-17: Audit Trail & Immutable Timeline Explorer Screen
/// Cryptographically sealed chronological event ledger for SOC2 Type II / ISO 27001 compliance,
/// complete with Merkle proof validation and state diff viewers.
class AuditTrailTimelineScreen extends ConsumerStatefulWidget {
  const AuditTrailTimelineScreen({super.key});

  @override
  ConsumerState<AuditTrailTimelineScreen> createState() => _AuditTrailTimelineScreenState();
}

class _AuditTrailTimelineScreenState extends ConsumerState<AuditTrailTimelineScreen> {
  String _selectedRange = '7D';
  int _currentNavIndex = 3; // Audit active

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
                colors: [Color(0xFF0D9488), Color(0xFF2DD4BF)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.history_edu, color: Colors.white, size: 18),
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
                'Audit Ledger',
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
          icon: const Icon(Icons.download_outlined, color: AppColors.accentPrimary, size: 22),
          tooltip: 'Export Ledger',
          onPressed: () => _showFeedbackToast('Exporting cryptographically sealed JSON ledger', Icons.download, AppColors.accentPrimary),
        ),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 16,
            backgroundColor: const Color(0xFFCCFBF1),
            child: const Text(
              'AU',
              style: TextStyle(
                color: Color(0xFF0D9488),
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
          _buildAnchoringStatusStrip(context),
          const SizedBox(height: AppSpacing.sm),
          _buildCryptographicHealthMatrix(context),
          const SizedBox(height: AppSpacing.sm),
          _buildRangeAndQueryFilter(context),
          const SizedBox(height: AppSpacing.sm),
          _buildTimelineEventsList(context),
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
          _buildAnchoringStatusStrip(context),
          const SizedBox(height: AppSpacing.md),
          _buildCryptographicHealthMatrix(context),
          const SizedBox(height: AppSpacing.md),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 4,
                child: _buildRangeAndQueryFilter(context),
              ),
              const SizedBox(width: AppSpacing.xl),
              Expanded(
                flex: 8,
                child: _buildTimelineEventsList(context),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildAnchoringStatusStrip(BuildContext context) {
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
                width: 8,
                height: 8,
                decoration: const BoxDecoration(
                  color: Color(0xFF0D9488),
                  shape: BoxShape.circle,
                ),
              ),
              const SizedBox(width: 8),
              const Text(
                'Continuous Ledger Anchoring Active',
                style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF0D9488)),
              ),
            ],
          ),
          const Text(
            'SOC2 / ISO 27001',
            style: TextStyle(fontSize: 10, color: AppColors.textSecondary, fontFamily: 'monospace'),
          ),
        ],
      ),
    );
  }

  Widget _buildCryptographicHealthMatrix(BuildContext context) {
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
            _buildHealthTile(
              title: 'MERKLE STATE',
              value: '32/32',
              subtext: 'Peers Verified',
              color: const Color(0xFF0D9488),
              bg: const Color(0xFFCCFBF1),
              icon: Icons.hub_outlined,
            ),
            _buildHealthTile(
              title: 'PROOF VALIDITY',
              value: '99.999%',
              subtext: 'Tamper-Evident',
              color: const Color(0xFF0D9488),
              bg: const Color(0xFFCCFBF1),
              icon: Icons.verified_user_outlined,
            ),
            _buildHealthTile(
              title: 'AI DECISION TRAILS',
              value: '18,409',
              subtext: 'Explainable Events',
              color: const Color(0xFF9333EA),
              bg: const Color(0xFFFAF5FF),
              icon: Icons.auto_awesome,
            ),
            _buildHealthTile(
              title: 'COMPLIANCE',
              value: 'SOC2 Type II',
              subtext: 'WORM Compliant',
              color: const Color(0xFF6366F1),
              bg: const Color(0xFFEEF2FF),
              icon: Icons.lock_outlined,
            ),
          ],
        );
      },
    );
  }

  Widget _buildHealthTile({
    required String title,
    required String value,
    required String subtext,
    required Color color,
    required Color bg,
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
              Icon(icon, size: 16, color: color),
            ],
          ),
          Text(
            value,
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: AppColors.textPrimary,
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: BoxDecoration(
              color: bg,
              borderRadius: BorderRadius.circular(4),
            ),
            child: Text(
              subtext,
              style: TextStyle(
                fontSize: 10,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRangeAndQueryFilter(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm + 2),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Column(
        children: [
          Row(
            children: [
              _buildFilterChip('24h', '24h'),
              const SizedBox(width: 6),
              _buildFilterChip('7D', '7 Days'),
              const SizedBox(width: 6),
              _buildFilterChip('30D', '30 Days'),
            ],
          ),
          const SizedBox(height: 8),
          TextField(
            decoration: InputDecoration(
              hintText: 'Search by case ID or entity (e.g. NEX-0104)...',
              hintStyle: const TextStyle(fontSize: 12, color: AppColors.textMuted),
              prefixIcon: const Icon(Icons.search, size: 18, color: AppColors.textMuted),
              isDense: true,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: BorderSide(color: AppColors.borderLight),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterChip(String id, String label) {
    final isSelected = _selectedRange == id;
    return ChoiceChip(
      label: Text(
        label,
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
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: isSelected ? AppColors.accentPrimary : AppColors.borderLight),
      ),
      onSelected: (selected) {
        if (selected) {
          setState(() => _selectedRange = id);
        }
      },
    );
  }

  Widget _buildTimelineEventsList(BuildContext context) {
    return Column(
      children: [
        _buildEventCard(
          height: '#89211',
          eventType: 'SLA OVERRIDE',
          title: 'SLA Priority Escalated: MEDIUM → P1 CRITICAL',
          timestamp: '2026-03-30 14:22:01 UTC',
          actor: 'Nexus AI Triage (Accepted by Elena Vance)',
          diffText: '{"path": "/severity", "value": "P1_CRITICAL", "confidence": 0.984}',
          eventColor: const Color(0xFFE11D48),
          eventBg: const Color(0xFFFFE4E6),
        ),
        const SizedBox(height: AppSpacing.sm),
        _buildEventCard(
          height: '#89204',
          eventType: 'REBALANCE',
          title: 'Auto-Routing Delegation: 2 Cases to Sarah Jenkins',
          timestamp: '2026-03-30 13:45:10 UTC',
          actor: 'Nexus AI Workload Engine (Confirmed by Elena Vance)',
          diffText: '{"action": "REBALANCE", "assignedTo": "sarah.jenkins", "recoveredMins": 18}',
          eventColor: const Color(0xFF9333EA),
          eventBg: const Color(0xFFFAF5FF),
        ),
        const SizedBox(height: AppSpacing.sm),
        _buildEventCard(
          height: '#89198',
          eventType: 'RESOLUTION',
          title: 'Case Resolution Proposed with KEDB Link',
          timestamp: '2026-03-30 11:12:30 UTC',
          actor: 'David Ross (Senior SRE)',
          diffText: '{"status": "RESOLUTION_PROPOSED", "kedbId": "PRB-2026-0031"}',
          eventColor: const Color(0xFF0D9488),
          eventBg: const Color(0xFFCCFBF1),
        ),
      ],
    );
  }

  Widget _buildEventCard({
    required String height,
    required String eventType,
    required String title,
    required String timestamp,
    required String actor,
    required String diffText,
    required Color eventColor,
    required Color eventBg,
  }) {
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
                  Text(height, style: const TextStyle(fontFamily: 'monospace', fontWeight: FontWeight.bold, fontSize: 11, color: AppColors.textSecondary)),
                  const SizedBox(width: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                    decoration: BoxDecoration(color: eventBg, borderRadius: BorderRadius.circular(4)),
                    child: Text(eventType, style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: eventColor)),
                  ),
                ],
              ),
              Row(
                children: const [
                  Icon(Icons.lock, size: 12, color: Color(0xFF0D9488)),
                  SizedBox(width: 4),
                  Text('SEALED', style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Color(0xFF0D9488))),
                ],
              ),
            ],
          ),
          const SizedBox(height: 6),
          Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: AppColors.textPrimary)),
          const SizedBox(height: 2),
          Text('$timestamp • $actor', style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
          const SizedBox(height: 8),
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(color: const Color(0xFFF8FAFC), borderRadius: BorderRadius.circular(6)),
            child: Text(diffText, style: const TextStyle(fontFamily: 'monospace', fontSize: 10, color: AppColors.textPrimary)),
          ),
          const SizedBox(height: 6),
          Row(
            children: const [
              Icon(Icons.fingerprint, size: 12, color: Color(0xFF0D9488)),
              SizedBox(width: 4),
              Text('SHA-256 Valid • ED25519 HSM Signed', style: TextStyle(fontSize: 9, color: AppColors.textMuted)),
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
            context.go('/admin/policies');
          } else if (index == 3) {
            // Already on audit
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
