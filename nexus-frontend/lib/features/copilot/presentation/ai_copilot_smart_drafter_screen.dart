import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-09: AI Copilot Smart Drafter Screen
/// Context-aware multi-modal AI operator assistant with root-cause hypothesis cards,
/// source citations, customer response smart drafter, fast action chips, and query composer.
class AiCopilotSmartDrafterScreen extends ConsumerStatefulWidget {
  final String caseId;

  const AiCopilotSmartDrafterScreen({
    super.key,
    required this.caseId,
  });

  @override
  ConsumerState<AiCopilotSmartDrafterScreen> createState() => _AiCopilotSmartDrafterScreenState();
}

class _AiCopilotSmartDrafterScreenState extends ConsumerState<AiCopilotSmartDrafterScreen> {
  final TextEditingController _queryController = TextEditingController();
  final String _draftText =
      "We have identified intermittent packet drop during Okta token validation on ingress pod-04. Traffic has been successfully rerouted to standby pod-02 while the active cache configuration is safely remediated.";
  int _currentNavIndex = 1; // Studio / Copilot active

  @override
  void dispose() {
    _queryController.dispose();
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
            context.go('/cases/${widget.caseId}/investigation');
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
                colors: [Color(0xFF831ADA), Color(0xFF4648D4)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Center(
              child: Text(
                '✨',
                style: TextStyle(fontSize: 16),
              ),
            ),
          ),
          const SizedBox(width: AppSpacing.xs),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Nexus Copilot',
                style: AppTypography.headlineSmall(context).copyWith(
                  fontWeight: FontWeight.bold,
                  fontSize: 15,
                ),
              ),
              Text(
                'OpRAG • Sonnet 3.5',
                style: AppTypography.labelSmall(context).copyWith(
                  color: AppColors.textMuted,
                  letterSpacing: 0.8,
                  fontSize: 8,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ],
      ),
      actions: [
        Container(
          margin: const EdgeInsets.symmetric(vertical: 12),
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
          decoration: BoxDecoration(
            color: const Color(0xFFFFE4E6),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Row(
            children: [
              const Icon(Icons.lock, size: 10, color: Color(0xFFE11D48)),
              const SizedBox(width: 3),
              Text(
                widget.caseId.replaceAll('NEX-2026-', ''),
                style: const TextStyle(color: Color(0xFFE11D48), fontSize: 10, fontWeight: FontWeight.bold, fontFamily: 'JetBrains Mono'),
              ),
            ],
          ),
        ),
        const SizedBox(width: AppSpacing.xs),
        IconButton(
          onPressed: () {
            context.push('/cases/${widget.caseId}/resolve');
          },
          icon: const Icon(Icons.task_alt, color: Color(0xFF0D9488), size: 22),
          tooltip: 'Propose Resolution',
        ),
        const SizedBox(width: AppSpacing.xs),
      ],
    );
  }

  Widget _buildMobileBody(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.md),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildContextUnderlay(),
          const SizedBox(height: AppSpacing.sm),
          _buildSyncPulseBanner(),
          const SizedBox(height: AppSpacing.sm),
          _buildFastActionChips(),
          const SizedBox(height: AppSpacing.md),
          _buildOperatorQueryBubble(),
          const SizedBox(height: AppSpacing.sm),
          _buildAiAnalysisCard(),
          const SizedBox(height: AppSpacing.md),
          _buildSmartDrafterCard(),
          const SizedBox(height: AppSpacing.md),
          _buildBottomInputBar(),
          const SizedBox(height: AppSpacing.xl),
        ],
      ),
    );
  }

  Widget _buildDesktopBody(BuildContext context) {
    return Center(
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 1080),
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(AppSpacing.xl),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildContextUnderlay(),
              const SizedBox(height: AppSpacing.md),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    flex: 6,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildSyncPulseBanner(),
                        const SizedBox(height: AppSpacing.sm),
                        _buildFastActionChips(),
                        const SizedBox(height: AppSpacing.md),
                        _buildOperatorQueryBubble(),
                        const SizedBox(height: AppSpacing.sm),
                        _buildAiAnalysisCard(),
                      ],
                    ),
                  ),
                  const SizedBox(width: AppSpacing.lg),
                  Expanded(
                    flex: 6,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildSmartDrafterCard(),
                        const SizedBox(height: AppSpacing.md),
                        _buildBottomInputBar(),
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

  Widget _buildContextUnderlay() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFFF2F3FF),
        borderRadius: BorderRadius.circular(16),
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
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                    decoration: BoxDecoration(color: const Color(0xFFFFE4E6), borderRadius: BorderRadius.circular(8)),
                    child: const Text('P1 Critical', style: TextStyle(color: Color(0xFFE11D48), fontSize: 9, fontWeight: FontWeight.bold)),
                  ),
                  const SizedBox(width: 6),
                  Text(widget.caseId, style: AppTypography.codeSmall(context).copyWith(fontWeight: FontWeight.bold)),
                ],
              ),
              const Text('SLA: 18m Left', style: TextStyle(color: Color(0xFFE11D48), fontSize: 10, fontWeight: FontWeight.bold, fontFamily: 'JetBrains Mono')),
            ],
          ),
          const SizedBox(height: 4),
          const Text('Envoy 504 Gateway Spike across us-east-prod', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
          const Text('Cluster telemetry alerts downstream timeouts reaching ingress edge layer. Error budget burn rate 14.8x.',
              style: TextStyle(color: AppColors.textSecondary, fontSize: 11)),
        ],
      ),
    );
  }

  Widget _buildSyncPulseBanner() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: AppSpacing.sm, vertical: 6),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.between,
        children: [
          Row(
            children: const [
              Icon(Icons.sync_saved_locally, size: 14, color: Color(0xFF4648D4)),
              SizedBox(width: 6),
              Text('CONTEXT SYNCED', style: TextStyle(color: AppColors.textSecondary, fontSize: 9, fontWeight: FontWeight.bold, letterSpacing: 0.8)),
            ],
          ),
          const Text('14:26:01 UTC', style: TextStyle(color: AppColors.textMuted, fontSize: 9, fontFamily: 'JetBrains Mono')),
        ],
      ),
    );
  }

  Widget _buildFastActionChips() {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: [
          _buildActionChip(Icons.summarize, 'Summarize Blockers', const Color(0xFF6366F1)),
          const SizedBox(width: AppSpacing.xs),
          _buildActionChip(Icons.edit_note, 'Draft Customer Update', const Color(0xFF7C3AED), isPurple: true),
          const SizedBox(width: AppSpacing.xs),
          _buildActionChip(Icons.troubleshoot, 'Analyze Root-Cause', const Color(0xFF006194)),
          const SizedBox(width: AppSpacing.xs),
          _buildActionChip(Icons.timer, 'Check SLA Risk', const Color(0xFFEA580C)),
        ],
      ),
    );
  }

  Widget _buildActionChip(IconData icon, String label, Color iconColor, {bool isPurple = false}) {
    return InkWell(
      onTap: () => _showFeedbackToast('Copilot triggered: $label', icon, isPurple ? const Color(0xFF7C3AED) : AppColors.primary),
      borderRadius: BorderRadius.circular(20),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          color: isPurple ? const Color(0xFFFAF5FF) : Colors.white,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: isPurple ? const Color(0xFFC084FC).withOpacity(0.4) : const Color(0xFFE2E8F0)),
        ),
        child: Row(
          children: [
            Icon(icon, size: 13, color: iconColor),
            const SizedBox(width: 4),
            Text(
              label,
              style: TextStyle(
                color: isPurple ? const Color(0xFF7C3AED) : AppColors.textPrimary,
                fontSize: 11,
                fontWeight: isPurple ? FontWeight.bold : FontWeight.w500,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildOperatorQueryBubble() {
    return Align(
      alignment: Alignment.centerRight,
      child: Container(
        margin: const EdgeInsets.only(left: 32),
        padding: const EdgeInsets.all(AppSpacing.md),
        decoration: const BoxDecoration(
          color: Color(0xFF4648D4),
          borderRadius: BorderRadius.only(
            topLeft: Radius.circular(16),
            bottomLeft: Radius.circular(16),
            bottomRight: Radius.circular(16),
            topRight: Radius.circular(4),
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Row(
              mainAxisSize: MainAxisSize.min,
              children: const [
                Text('Elena Vance (Ops Lead)', style: TextStyle(color: Color(0xFFE1E0FF), fontSize: 10, fontWeight: FontWeight.bold)),
                SizedBox(width: 4),
                Text('14:25', style: TextStyle(color: Color(0xFFC0C1FF), fontSize: 9, fontFamily: 'JetBrains Mono')),
              ],
            ),
            const SizedBox(height: 4),
            const Text(
              'What is the current hypothesis on the Envoy 504 gateway spike, and what should we tell the customer?',
              style: TextStyle(color: Colors.white, fontSize: 12, height: 1.35),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAiAnalysisCard() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFFFAF5FF),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFC084FC).withOpacity(0.3)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: const [
                  Icon(Icons.tune, size: 16, color: Color(0xFF7C3AED)),
                  SizedBox(width: 4),
                  Text('NEXUS ANALYSIS', style: TextStyle(color: Color(0xFF7C3AED), fontWeight: FontWeight.bold, fontSize: 10, letterSpacing: 0.8)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(color: const Color(0xFFF3E8FF), borderRadius: BorderRadius.circular(8)),
                child: const Text('96% Match', style: TextStyle(color: Color(0xFF7C3AED), fontSize: 9, fontWeight: FontWeight.bold)),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          _buildHypothesisTile(
            icon: Icons.memory,
            iconColor: const Color(0xFFD97706),
            title: 'Redis Token Cache Exhaustion',
            desc: 'Cluster node us-east-prod-04 hit memory ceiling at 98.4%, causing evictions of active user sessions.',
          ),
          const SizedBox(height: AppSpacing.xs),
          _buildHypothesisTile(
            icon: Icons.hourglass_bottom,
            iconColor: const Color(0xFFE11D48),
            title: 'Cascading Handshake Delays',
            desc: 'Downstream Okta SSO token verification timing out at the hard 30,000ms ceiling, saturating connection pools.',
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              const Text('SOURCES:', style: TextStyle(color: AppColors.textMuted, fontSize: 9, fontWeight: FontWeight.bold)),
              const SizedBox(width: 6),
              _buildSourceChip('Note #2 (D. Ross)'),
              const SizedBox(width: 4),
              _buildSourceChip('Envoy Pod #4028'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildHypothesisTile({
    required IconData icon,
    required Color iconColor,
    required String title,
    required String desc,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.9),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, size: 14, color: iconColor),
              const SizedBox(width: 4),
              Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 11)),
            ],
          ),
          const SizedBox(height: 2),
          Text(desc, style: const TextStyle(color: AppColors.textSecondary, fontSize: 11, height: 1.3)),
        ],
      ),
    );
  }

  Widget _buildSourceChip(String label) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(color: const Color(0xFFE2E7FF), borderRadius: BorderRadius.circular(6)),
      child: Text(label, style: const TextStyle(color: Color(0xFF131B2E), fontSize: 9, fontWeight: FontWeight.bold, fontFamily: 'JetBrains Mono')),
    );
  }

  Widget _buildSmartDrafterCard() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE2E8F0)),
        boxShadow: const [
          BoxShadow(
            color: Color(0x06000000),
            blurRadius: 10,
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
                children: const [
                  Icon(Icons.mark_chat_read, size: 16, color: Color(0xFF4648D4)),
                  SizedBox(width: 6),
                  Text('Smart Drafter: Customer Status', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(color: const Color(0xFFE0F2FE), borderRadius: BorderRadius.circular(8)),
                child: const Text('Polite & Technical', style: TextStyle(color: Color(0xFF0284C7), fontSize: 9, fontWeight: FontWeight.bold)),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Container(
            padding: const EdgeInsets.all(AppSpacing.sm),
            decoration: BoxDecoration(
              color: const Color(0xFFF2F3FF),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '“$_draftText”',
                  style: const TextStyle(fontSize: 12, height: 1.4, color: AppColors.textPrimary),
                ),
                const SizedBox(height: 6),
                const Align(
                  alignment: Alignment.centerRight,
                  child: Text('234 chars • Ready', style: TextStyle(color: AppColors.textMuted, fontSize: 9, fontFamily: 'JetBrains Mono')),
                ),
              ],
            ),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              Expanded(
                flex: 6,
                child: ElevatedButton.icon(
                  onPressed: () {
                    _showFeedbackToast('Draft inserted into response thread', Icons.arrow_forward, const Color(0xFF0D9488));
                    context.push('/cases/${widget.caseId}/investigation');
                  },
                  icon: const Icon(Icons.arrow_forward, size: 14, color: Colors.white),
                  label: const Text('Insert in Reply', style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold)),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF4648D4),
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    elevation: 0,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                ),
              ),
              const SizedBox(width: AppSpacing.xs),
              Expanded(
                flex: 3,
                child: OutlinedButton.icon(
                  onPressed: () => _showFeedbackToast('Regenerating alternative tone', Icons.refresh, AppColors.primary),
                  icon: const Icon(Icons.refresh, size: 14),
                  label: const Text('Retry', style: TextStyle(fontSize: 11)),
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: Color(0xFFE2E8F0)),
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                ),
              ),
              const SizedBox(width: AppSpacing.xs),
              Expanded(
                flex: 3,
                child: OutlinedButton.icon(
                  onPressed: () => _showFeedbackToast('Copied draft to clipboard', Icons.content_copy, AppColors.primary),
                  icon: const Icon(Icons.content_copy, size: 14),
                  label: const Text('Copy', style: TextStyle(fontSize: 11)),
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: Color(0xFFE2E8F0)),
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildBottomInputBar() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: AppSpacing.sm, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: Row(
        children: [
          IconButton(
            onPressed: () => _showFeedbackToast('Telemetry file attach dialog', Icons.attach_file, AppColors.primary),
            icon: const Icon(Icons.attach_file, size: 18, color: AppColors.textSecondary),
          ),
          Expanded(
            child: TextField(
              controller: _queryController,
              style: AppTypography.bodySmall(context),
              decoration: const InputDecoration(
                hintText: 'Ask Copilot or request action...',
                hintStyle: TextStyle(fontSize: 11, color: AppColors.textMuted),
                border: InputBorder.none,
              ),
            ),
          ),
          IconButton(
            onPressed: () => _showFeedbackToast('Voice dictation active', Icons.mic, const Color(0xFF0D9488)),
            icon: const Icon(Icons.mic, size: 18, color: AppColors.textSecondary),
          ),
          InkWell(
            onTap: () {
              if (_queryController.text.trim().isNotEmpty) {
                _showFeedbackToast('Copilot analyzing query...', Icons.auto_awesome, const Color(0xFF831ADA));
                setState(() {
                  _queryController.clear();
                });
              }
            },
            borderRadius: BorderRadius.circular(8),
            child: Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(color: const Color(0xFF4648D4), borderRadius: BorderRadius.circular(8)),
              child: const Icon(Icons.arrow_upward, size: 16, color: Colors.white),
            ),
          ),
        ],
      ),
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
          } else if (index == 1) {
            context.push('/cases/${widget.caseId}/investigation');
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
          BottomNavigationBarItem(icon: Icon(Icons.auto_awesome), activeIcon: Icon(Icons.auto_awesome), label: 'Copilot'),
          BottomNavigationBarItem(icon: Icon(Icons.radar_outlined), activeIcon: Icon(Icons.radar), label: 'Radar'),
          BottomNavigationBarItem(icon: Icon(Icons.group_outlined), activeIcon: Icon(Icons.group), label: 'Lead'),
          BottomNavigationBarItem(icon: Icon(Icons.admin_panel_settings_outlined), activeIcon: Icon(Icons.admin_panel_settings), label: 'Portal'),
        ],
      ),
    );
  }
}
