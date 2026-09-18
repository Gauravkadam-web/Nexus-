import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-07: Operator Investigation Studio Screen
/// Deep investigation workbench with live activity stream, AI drafter assistant,
/// telemetry latency sparkline, public/internal message composer, and tactical resolution suite.
class OperatorInvestigationStudioScreen extends ConsumerStatefulWidget {
  final String caseId;

  const OperatorInvestigationStudioScreen({
    super.key,
    required this.caseId,
  });

  @override
  ConsumerState<OperatorInvestigationStudioScreen> createState() => _OperatorInvestigationStudioScreenState();
}

class _OperatorInvestigationStudioScreenState extends ConsumerState<OperatorInvestigationStudioScreen> {
  final TextEditingController _composerController = TextEditingController();
  bool _isInternalNote = false;
  bool _isDrafterDismissed = false;
  bool _isDraftApplied = false;
  int _selectedTab = 1; // 0: AI Triage, 1: Activity, 2: Tasks
  int _currentNavIndex = 1; // Studio tab active

  final String _draftText =
      "We have identified packet drop during the Okta token validation stage on ingress pod-04. Traffic has been routed to standby pod-02 while we rollback.";

  @override
  void dispose() {
    _composerController.dispose();
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
      backgroundColor: const Color(0xFFFAF8FF),
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
      leading: IconButton(
        icon: const Icon(Icons.arrow_back, color: AppColors.textPrimary),
        onPressed: () {
          if (context.canPop()) {
            context.pop();
          } else {
            context.go('/dashboard/operator/triage');
          }
        },
      ),
      titleSpacing: 0,
      title: Row(
        children: [
          Container(
            width: 32,
            height: 32,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF6366F1), Color(0xFF9333EA)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Center(
              child: Text(
                'N',
                style: TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                  fontFamily: 'Outfit',
                ),
              ),
            ),
          ),
          const SizedBox(width: AppSpacing.xs),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Nexus AI',
                style: AppTypography.headlineSmall(context).copyWith(
                  fontWeight: FontWeight.bold,
                  fontSize: 15,
                ),
              ),
              Text(
                'STUDIO // INVESTIGATION',
                style: AppTypography.labelSmall(context).copyWith(
                  color: AppColors.textMuted,
                  letterSpacing: 1.0,
                  fontSize: 8,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ],
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: () => _showFeedbackToast('Search case context & telemetry', Icons.search, AppColors.primary),
          icon: const Icon(Icons.search, color: AppColors.textSecondary, size: 20),
        ),
        IconButton(
          onPressed: () {
            context.push('/cases/${widget.caseId}/collaboration');
          },
          icon: const Icon(Icons.hub_outlined, color: Color(0xFF831ADA), size: 20),
          tooltip: 'Evidence & Collaboration Hub',
        ),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 14,
            backgroundColor: AppColors.primary.withOpacity(0.15),
            child: const Text('EV', style: TextStyle(color: AppColors.primary, fontSize: 10, fontWeight: FontWeight.bold)),
          ),
        ),
      ],
    );
  }

  Widget _buildMobileBody(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.md),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildTopContextPanel(),
          const SizedBox(height: AppSpacing.sm),
          _buildSegmentedTabRail(),
          const SizedBox(height: AppSpacing.md),
          _buildLiveActivityStream(),
          const SizedBox(height: AppSpacing.md),
          if (!_isDrafterDismissed) ...[
            _buildAiDrafterCard(),
            const SizedBox(height: AppSpacing.md),
          ],
          _buildTelemetrySparkline(),
          const SizedBox(height: AppSpacing.md),
          _buildMessageComposer(),
          const SizedBox(height: AppSpacing.md),
          _buildBottomActionSuite(),
          const SizedBox(height: AppSpacing.xl),
        ],
      ),
    );
  }

  Widget _buildDesktopBody(BuildContext context) {
    return Center(
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 1100),
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(AppSpacing.xl),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildTopContextPanel(),
              const SizedBox(height: AppSpacing.md),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    flex: 6,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildSegmentedTabRail(),
                        const SizedBox(height: AppSpacing.md),
                        _buildLiveActivityStream(),
                        const SizedBox(height: AppSpacing.md),
                        _buildMessageComposer(),
                      ],
                    ),
                  ),
                  const SizedBox(width: AppSpacing.lg),
                  Expanded(
                    flex: 4,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        if (!_isDrafterDismissed) ...[
                          _buildAiDrafterCard(),
                          const SizedBox(height: AppSpacing.md),
                        ],
                        _buildTelemetrySparkline(),
                        const SizedBox(height: AppSpacing.md),
                        _buildBottomActionSuite(),
                      ],
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTopContextPanel() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE2E8F0)),
        boxShadow: const [
          BoxShadow(
            color: Color(0x04000000),
            blurRadius: 8,
            offset: Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: [
                  const Icon(Icons.terminal, size: 18, color: Color(0xFF4648D4)),
                  const SizedBox(width: 6),
                  Text(
                    widget.caseId,
                    style: AppTypography.codeSmall(context).copyWith(
                      fontWeight: FontWeight.bold,
                      color: AppColors.textPrimary,
                    ),
                  ),
                ],
              ),
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                      color: const Color(0xFFFFE4E6),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Text(
                      'P1 CRITICAL',
                      style: TextStyle(
                        color: Color(0xFFE11D48),
                        fontSize: 9,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 0.5,
                      ),
                    ),
                  ),
                  const SizedBox(width: 6),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                      color: const Color(0xFFFEF3C7),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Text(
                      'INVESTIGATING',
                      style: TextStyle(
                        color: Color(0xFFD97706),
                        fontSize: 9,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 0.5,
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.xs),
          Text(
            'Authentication Gateway Timeout during SSO federation',
            style: AppTypography.titleMedium(context).copyWith(
              fontWeight: FontWeight.bold,
              fontSize: 16,
            ),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: const Color(0xFFFFE4E6).withOpacity(0.5),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.hourglass_top, size: 14, color: Color(0xFFE11D48)),
                    const SizedBox(width: 4),
                    Text(
                      '02:45:10 rem.',
                      style: AppTypography.codeSmall(context).copyWith(
                        color: const Color(0xFFE11D48),
                        fontWeight: FontWeight.bold,
                        fontSize: 11,
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: const Color(0xFFEAEDFF),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.dns, size: 14, color: Color(0xFF006194)),
                    const SizedBox(width: 4),
                    Text(
                      'SSO-Cluster-04',
                      style: AppTypography.labelSmall(context).copyWith(
                        color: AppColors.textSecondary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildSegmentedTabRail() {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: const Color(0xFFF2F3FF),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          _buildRailTab(0, '✨ AI Triage'),
          _buildRailTab(1, 'Forum Activity (3)'),
          _buildRailTab(2, 'Checklist (3)'),
        ],
      ),
    );
  }

  Widget _buildRailTab(int index, String label) {
    final isSelected = _selectedTab == index;
    return Expanded(
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedTab = index;
          });
          if (index == 2) {
            context.push('/cases/${widget.caseId}/collaboration');
          }
        },
        borderRadius: BorderRadius.circular(8),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: isSelected ? Colors.white : Colors.transparent,
            borderRadius: BorderRadius.circular(8),
            boxShadow: isSelected
                ? const [
                    BoxShadow(color: Color(0x0A000000), blurRadius: 4, offset: Offset(0, 1)),
                  ]
                : null,
          ),
          child: Center(
            child: Text(
              label,
              style: TextStyle(
                color: isSelected ? const Color(0xFF4648D4) : AppColors.textSecondary,
                fontSize: 11,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLiveActivityStream() {
    return Column(
      children: [
        // Requester Message
        _buildActivityCard(
          authorName: 'Sarah Jenkins',
          roleText: 'Requester • DevOps Team',
          timeText: '10:14 AM',
          avatarLetter: 'S',
          avatarColor: const Color(0xFF6366F1),
          content:
              'Users on Okta SSO are experiencing 504 Gateway timeouts when authenticating through the regional gateway. Issue surfaced right after the 10:00 AM canary deployment.',
          attachmentName: 'sso_trace_error.log',
          attachmentSize: '1.4 MB',
        ),
        const SizedBox(height: AppSpacing.sm),
        // System Event
        Container(
          padding: const EdgeInsets.all(AppSpacing.sm),
          decoration: BoxDecoration(
            color: const Color(0xFFFFE4E6).withOpacity(0.6),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Icon(Icons.priority_high, size: 18, color: Color(0xFFE11D48)),
              const SizedBox(width: 8),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Priority Escalated • MEDIUM to CRITICAL',
                      style: TextStyle(color: Color(0xFFE11D48), fontWeight: FontWeight.bold, fontSize: 11),
                    ),
                    Text(
                      'System anomaly engine correlated 84 downstream microservice retries.',
                      style: AppTypography.bodySmall(context).copyWith(fontSize: 11),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: AppSpacing.sm),
        // Lead Operator Note
        _buildActivityCard(
          authorName: 'Elena Vance',
          roleText: 'Lead Operator',
          timeText: '10:22 AM',
          avatarLetter: 'E',
          avatarColor: const Color(0xFF831ADA),
          content:
              'Understood Sarah. Triage started. Checking Redis cache latency and Envoy ingress routes right now. Canary routing is being isolated to prevent spillover.',
        ),
      ],
    );
  }

  Widget _buildActivityCard({
    required String authorName,
    required String roleText,
    required String timeText,
    required String avatarLetter,
    required Color avatarColor,
    required String content,
    String? attachmentName,
    String? attachmentSize,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: const Color(0xFFE2E8F0)),
        boxShadow: const [
          BoxShadow(
            color: Color(0x04000000),
            blurRadius: 6,
            offset: Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 14,
                backgroundColor: avatarColor.withOpacity(0.15),
                child: Text(
                  avatarLetter,
                  style: TextStyle(color: avatarColor, fontWeight: FontWeight.bold, fontSize: 11),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.between,
                      children: [
                        Text(authorName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                        Text(timeText, style: AppTypography.codeSmall(context).copyWith(color: AppColors.textMuted, fontSize: 10)),
                      ],
                    ),
                    Text(roleText, style: TextStyle(color: AppColors.textMuted, fontSize: 10)),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Text(
            content,
            style: AppTypography.bodySmall(context).copyWith(
              color: AppColors.textPrimary,
              height: 1.4,
              fontSize: 12,
            ),
          ),
          if (attachmentName != null) ...[
            const SizedBox(height: AppSpacing.sm),
            InkWell(
              onTap: () => _showFeedbackToast('Downloaded $attachmentName', Icons.download, AppColors.primary),
              borderRadius: BorderRadius.circular(8),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                decoration: BoxDecoration(
                  color: const Color(0xFFF2F3FF),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.description, size: 14, color: Color(0xFF006194)),
                    const SizedBox(width: 6),
                    Text(attachmentName, style: AppTypography.codeSmall(context).copyWith(fontSize: 11, fontWeight: FontWeight.w600)),
                    if (attachmentSize != null) ...[
                      const SizedBox(width: 4),
                      Text('• $attachmentSize', style: TextStyle(color: AppColors.textMuted, fontSize: 10)),
                    ],
                    const SizedBox(width: 6),
                    const Icon(Icons.download, size: 14, color: AppColors.textMuted),
                  ],
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildAiDrafterCard() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFFFAF5FF),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFC084FC).withOpacity(0.35)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: [
                  const Text('✨', style: TextStyle(fontSize: 13)),
                  const SizedBox(width: 6),
                  Text(
                    'AI DRAFTER SUGGESTION',
                    style: AppTypography.labelSmall(context).copyWith(
                      color: const Color(0xFF831ADA),
                      fontWeight: FontWeight.bold,
                      letterSpacing: 0.8,
                      fontSize: 10,
                    ),
                  ),
                ],
              ),
              const Text('Just now', style: TextStyle(color: AppColors.textMuted, fontSize: 10, fontFamily: 'JetBrains Mono')),
            ],
          ),
          const SizedBox(height: AppSpacing.xs),
          Container(
            padding: const EdgeInsets.all(AppSpacing.sm),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.8),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Text(
              '“$_draftText”',
              style: AppTypography.bodySmall(context).copyWith(
                fontStyle: FontStyle.italic,
                fontSize: 12,
                color: AppColors.textPrimary,
              ),
            ),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              TextButton(
                onPressed: () {
                  setState(() {
                    _isDrafterDismissed = true;
                  });
                },
                child: const Text('Dismiss', style: TextStyle(color: AppColors.textMuted, fontSize: 11)),
              ),
              const SizedBox(width: AppSpacing.xs),
              ElevatedButton.icon(
                onPressed: () {
                  setState(() {
                    _composerController.text = _draftText;
                    _isDraftApplied = true;
                  });
                  _showFeedbackToast('AI Draft applied to message composer', Icons.auto_awesome, const Color(0xFF831ADA));
                },
                icon: const Text('✨', style: TextStyle(fontSize: 12)),
                label: const Text('Apply Draft', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.white)),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF4648D4),
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  elevation: 0,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildTelemetrySparkline() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: [
                  const Icon(Icons.analytics_outlined, size: 16, color: Color(0xFF006194)),
                  const SizedBox(width: 6),
                  const Text('Ingress Gateway Health', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: const Color(0xFFFFE4E6),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: const Text(
                  '94.2% Latency Spike',
                  style: TextStyle(color: Color(0xFFE11D48), fontSize: 9, fontWeight: FontWeight.bold, fontFamily: 'JetBrains Mono'),
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          // Custom Sparkline Painter Simulation
          Container(
            height: 48,
            width: double.infinity,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [const Color(0xFF4648D4).withOpacity(0.08), Colors.transparent],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: CustomPaint(
              painter: _SparklinePainter(),
            ),
          ),
          const SizedBox(height: 4),
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              const Text('10:00 (Canary)', style: TextStyle(color: AppColors.textMuted, fontSize: 9, fontFamily: 'JetBrains Mono')),
              const Text('10:15 (Degraded)', style: TextStyle(color: AppColors.textMuted, fontSize: 9, fontFamily: 'JetBrains Mono')),
              const Text('10:28 (Now)', style: TextStyle(color: Color(0xFFE11D48), fontSize: 9, fontWeight: FontWeight.bold, fontFamily: 'JetBrains Mono')),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildMessageComposer() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE2E8F0)),
        boxShadow: const [
          BoxShadow(
            color: Color(0x04000000),
            blurRadius: 8,
            offset: Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Container(
                padding: const EdgeInsets.all(2),
                decoration: BoxDecoration(
                  color: const Color(0xFFF2F3FF),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    InkWell(
                      onTap: () {
                        setState(() {
                          _isInternalNote = false;
                        });
                      },
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: !_isInternalNote ? Colors.white : Colors.transparent,
                          borderRadius: BorderRadius.circular(6),
                          boxShadow: !_isInternalNote
                              ? const [BoxShadow(color: Color(0x0A000000), blurRadius: 2)]
                              : null,
                        ),
                        child: const Text('Public Message', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold)),
                      ),
                    ),
                    InkWell(
                      onTap: () {
                        setState(() {
                          _isInternalNote = true;
                        });
                      },
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: _isInternalNote ? Colors.white : Colors.transparent,
                          borderRadius: BorderRadius.circular(6),
                          boxShadow: _isInternalNote
                              ? const [BoxShadow(color: Color(0x0A000000), blurRadius: 2)]
                              : null,
                        ),
                        child: Row(
                          children: const [
                            Text('Internal Note', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold)),
                            SizedBox(width: 3),
                            Icon(Icons.lock, size: 10, color: AppColors.textMuted),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Row(
                children: [
                  Container(
                    width: 6,
                    height: 6,
                    decoration: const BoxDecoration(
                      color: Color(0xFF0D9488),
                      shape: BoxShape.circle,
                    ),
                  ),
                  const SizedBox(width: 4),
                  const Text('TLS 1.3', style: TextStyle(color: AppColors.textMuted, fontSize: 9, fontFamily: 'JetBrains Mono')),
                ],
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Container(
            decoration: BoxDecoration(
              color: const Color(0xFFF2F3FF),
              borderRadius: BorderRadius.circular(10),
            ),
            child: TextField(
              controller: _composerController,
              maxLines: 3,
              style: AppTypography.bodySmall(context),
              decoration: InputDecoration(
                hintText: _isInternalNote
                    ? 'Write internal operator note (Hidden from requester)...'
                    : 'Type an update to Sarah Jenkins...',
                hintStyle: AppTypography.bodySmall(context).copyWith(color: AppColors.textMuted, fontSize: 12),
                border: InputBorder.none,
                contentPadding: const EdgeInsets.all(AppSpacing.sm),
              ),
            ),
          ),
          const SizedBox(height: AppSpacing.xs),
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: [
                  IconButton(
                    onPressed: () => _showFeedbackToast('Attachment dialogue opened', Icons.attach_file, AppColors.primary),
                    icon: const Icon(Icons.attach_file, size: 18, color: AppColors.textSecondary),
                    constraints: const BoxConstraints(minWidth: 32, minHeight: 32),
                    padding: EdgeInsets.zero,
                  ),
                  IconButton(
                    onPressed: () => _showFeedbackToast('Code block template inserted', Icons.code, AppColors.primary),
                    icon: const Icon(Icons.code, size: 18, color: AppColors.textSecondary),
                    constraints: const BoxConstraints(minWidth: 32, minHeight: 32),
                    padding: EdgeInsets.zero,
                  ),
                  IconButton(
                    onPressed: () {
                      setState(() {
                        _composerController.text = _draftText;
                      });
                      _showFeedbackToast('AI Copilot generated completion', Icons.auto_awesome, const Color(0xFF831ADA));
                    },
                    icon: const Icon(Icons.auto_awesome, size: 18, color: Color(0xFF831ADA)),
                    constraints: const BoxConstraints(minWidth: 32, minHeight: 32),
                    padding: EdgeInsets.zero,
                  ),
                ],
              ),
              ElevatedButton.icon(
                onPressed: () {
                  if (_composerController.text.trim().isNotEmpty) {
                    _showFeedbackToast('Update dispatched successfully', Icons.send, const Color(0xFF0D9488));
                    setState(() {
                      _composerController.clear();
                    });
                  }
                },
                icon: const Icon(Icons.send, size: 14, color: Colors.white),
                label: const Text('Send Update', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.white)),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF4648D4),
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                  elevation: 0,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildBottomActionSuite() {
    return Row(
      children: [
        Expanded(
          child: ElevatedButton.icon(
            onPressed: () {
              context.push('/cases/${widget.caseId}/collaboration');
            },
            icon: const Icon(Icons.task_alt, size: 16, color: Color(0xFF0D9488)),
            label: const Text('Evidence & Tasks', style: TextStyle(color: Color(0xFF0D9488), fontSize: 11, fontWeight: FontWeight.bold)),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFCCFBF1),
              elevation: 0,
              padding: const EdgeInsets.symmetric(vertical: 12),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
        ),
        const SizedBox(width: AppSpacing.sm),
        Expanded(
          child: ElevatedButton.icon(
            onPressed: () {
              _showFeedbackToast('Tier-3 Incident Manager alerted', Icons.arrow_upward, const Color(0xFFE11D48));
            },
            icon: const Icon(Icons.arrow_upward, size: 16, color: Color(0xFFE11D48)),
            label: const Text('Escalate Tier-3', style: TextStyle(color: Color(0xFFE11D48), fontSize: 11, fontWeight: FontWeight.bold)),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFFFE4E6),
              elevation: 0,
              padding: const EdgeInsets.symmetric(vertical: 12),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildBottomNav(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.95),
        border: const Border(top: BorderSide(color: Color(0xFFE2E8F0))),
      ),
      child: BottomNavigationBar(
        currentIndex: _currentNavIndex,
        onTap: (index) {
          setState(() {
            _currentNavIndex = index;
          });
          if (index == 0) {
            context.go('/dashboard/operator/triage');
          } else if (index == 2) {
            context.push('/cases/${widget.caseId}/collaboration');
          } else if (index == 4) {
            context.go('/dashboard/requester');
          }
        },
        type: BottomNavigationBarType.fixed,
        backgroundColor: Colors.transparent,
        elevation: 0,
        selectedItemColor: const Color(0xFF4648D4),
        unselectedItemColor: AppColors.textMuted,
        selectedFontSize: 10,
        unselectedFontSize: 10,
        selectedLabelStyle: const TextStyle(fontWeight: FontWeight.bold),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.inbox_outlined), activeIcon: Icon(Icons.inbox), label: 'Triage'),
          BottomNavigationBarItem(icon: Icon(Icons.dataset_outlined), activeIcon: Icon(Icons.dataset), label: 'Studio'),
          BottomNavigationBarItem(icon: Icon(Icons.radar_outlined), activeIcon: Icon(Icons.radar), label: 'Radar'),
          BottomNavigationBarItem(icon: Icon(Icons.group_outlined), activeIcon: Icon(Icons.group), label: 'Lead'),
          BottomNavigationBarItem(icon: Icon(Icons.admin_panel_settings_outlined), activeIcon: Icon(Icons.admin_panel_settings), label: 'Portal'),
        ],
      ),
    );
  }
}

/// Custom Sparkline Painter for telemetry latency spikes
class _SparklinePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = const Color(0xFF4648D4)
      ..strokeWidth = 2.0
      ..style = PaintingStyle.stroke;

    final dotPaint = Paint()
      ..color = const Color(0xFFE11D48)
      ..style = PaintingStyle.fill;

    final path = Path();
    path.moveTo(0, size.height * 0.7);
    path.quadraticBezierTo(size.width * 0.2, size.height * 0.6, size.width * 0.4, size.height * 0.65);
    path.quadraticBezierTo(size.width * 0.6, size.height * 0.7, size.width * 0.75, size.height * 0.15);
    path.quadraticBezierTo(size.width * 0.85, size.height * 0.1, size.width * 0.95, size.height * 0.35);

    canvas.drawPath(path, paint);
    canvas.drawCircle(Offset(size.width * 0.75, size.height * 0.15), 3.5, dotPaint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
