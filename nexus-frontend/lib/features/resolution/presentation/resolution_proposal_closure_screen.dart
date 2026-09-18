import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-10: Resolution Proposal & Closure Modal Screen
/// Dual-signoff resolution workspace with AI Root Cause Digest, remediation classification pills,
/// customer-facing closure notes, KEDB publication toggle, and stabilization verification gates.
class ResolutionProposalClosureScreen extends ConsumerStatefulWidget {
  final String caseId;

  const ResolutionProposalClosureScreen({
    super.key,
    required this.caseId,
  });

  @override
  ConsumerState<ResolutionProposalClosureScreen> createState() => _ResolutionProposalClosureScreenState();
}

class _ResolutionProposalClosureScreenState extends ConsumerState<ResolutionProposalClosureScreen> {
  final TextEditingController _noteController = TextEditingController(
    text: "Ingress Envoy connection limits have been re-calibrated. All affected regional pods have returned to nominal latency (<12ms).",
  );
  bool _includeRcaSummary = true;
  bool _publishToKedb = true;
  String _selectedRemediation = 'Permanent Patch';
  bool _isDispatching = false;
  int _currentNavIndex = 1;

  @override
  void dispose() {
    _noteController.dispose();
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

  void _triggerHandshake() {
    setState(() {
      _isDispatching = true;
    });
    Future.delayed(const Duration(milliseconds: 700), () {
      if (mounted) {
        setState(() {
          _isDispatching = false;
        });
        _showFeedbackToast('Resolution proposal dispatched to requester', Icons.verified, const Color(0xFF0D9488));
        context.push('/cases/${widget.caseId}/track');
      }
    });
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
                colors: [Color(0xFF0D9488), Color(0xFF4648D4)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Center(
              child: Icon(Icons.verified, color: Colors.white, size: 18),
            ),
          ),
          const SizedBox(width: AppSpacing.xs),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Propose Resolution',
                style: AppTypography.headlineSmall(context).copyWith(
                  fontWeight: FontWeight.bold,
                  fontSize: 15,
                ),
              ),
              Text(
                'CLOSURE & HANDSHAKE',
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
          margin: const EdgeInsets.symmetric(vertical: 12, horizontal: 8),
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
          decoration: BoxDecoration(
            color: const Color(0xFFCCFBF1),
            borderRadius: BorderRadius.circular(12),
          ),
          child: const Text(
            'Level 3 Fix',
            style: TextStyle(color: Color(0xFF0D9488), fontSize: 9, fontWeight: FontWeight.bold),
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
          _buildMetaHeaderCard(),
          const SizedBox(height: AppSpacing.md),
          _buildAiRcaDigestCard(),
          const SizedBox(height: AppSpacing.md),
          _buildClassificationForm(),
          const SizedBox(height: AppSpacing.md),
          _buildCustomerNoteInput(),
          const SizedBox(height: AppSpacing.md),
          _buildKedbToggle(),
          const SizedBox(height: AppSpacing.md),
          _buildStabilizationGates(),
          const SizedBox(height: AppSpacing.lg),
          _buildActionFooter(),
          const SizedBox(height: AppSpacing.xl),
        ],
      ),
    );
  }

  Widget _buildDesktopBody(BuildContext context) {
    return Center(
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 960),
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(AppSpacing.xl),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildMetaHeaderCard(),
              const SizedBox(height: AppSpacing.md),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    flex: 6,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildAiRcaDigestCard(),
                        const SizedBox(height: AppSpacing.md),
                        _buildClassificationForm(),
                        const SizedBox(height: AppSpacing.md),
                        _buildCustomerNoteInput(),
                      ],
                    ),
                  ),
                  const SizedBox(width: AppSpacing.lg),
                  Expanded(
                    flex: 5,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildKedbToggle(),
                        const SizedBox(height: AppSpacing.md),
                        _buildStabilizationGates(),
                        const SizedBox(height: AppSpacing.lg),
                        _buildActionFooter(),
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

  Widget _buildMetaHeaderCard() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
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
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(color: const Color(0xFFF2F3FF), borderRadius: BorderRadius.circular(6)),
                child: Text(widget.caseId, style: AppTypography.codeSmall(context).copyWith(fontWeight: FontWeight.bold)),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(color: const Color(0xFFEEF2FF), borderRadius: BorderRadius.circular(10)),
                child: const Text('Ready for Handshake', style: TextStyle(color: Color(0xFF6366F1), fontSize: 9, fontWeight: FontWeight.bold)),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.xs),
          const Text(
            'Propose Resolution & Close Case',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
          ),
          const SizedBox(height: 2),
          const Text(
            '“Authentication Gateway Timeout during SSO federation” — verified system stabilization.',
            style: TextStyle(color: AppColors.textSecondary, fontSize: 12),
          ),
        ],
      ),
    );
  }

  Widget _buildAiRcaDigestCard() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFFFAF5FF),
        borderRadius: BorderRadius.circular(14),
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
                  Icon(Icons.auto_awesome, size: 16, color: Color(0xFF831ADA)),
                  SizedBox(width: 4),
                  Text('NEXUS COPILOT ROOT-CAUSE', style: TextStyle(color: Color(0xFF831ADA), fontWeight: FontWeight.bold, fontSize: 10, letterSpacing: 0.8)),
                ],
              ),
              const Text('Model v4.2', style: TextStyle(color: AppColors.textMuted, fontSize: 9, fontFamily: 'JetBrains Mono')),
            ],
          ),
          const SizedBox(height: AppSpacing.xs),
          const Text(
            'Redis token cache exhaustion mitigated by hotpatch #4099. Connection pool limits expanded from 1024 to 4096. Error budget recovered to 99.98%.',
            style: TextStyle(fontSize: 12, height: 1.35, color: AppColors.textSecondary),
          ),
          const SizedBox(height: AppSpacing.sm),
          InkWell(
            onTap: () {
              setState(() {
                _includeRcaSummary = !_includeRcaSummary;
              });
            },
            child: Row(
              children: [
                Checkbox(
                  value: _includeRcaSummary,
                  onChanged: (val) {
                    setState(() {
                      _includeRcaSummary = val ?? true;
                    });
                  },
                  activeColor: const Color(0xFF4648D4),
                  materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                ),
                const Expanded(
                  child: Text('Include AI automated RCA summary in customer closure notice', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w500)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildClassificationForm() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('ROOT CAUSE CATEGORY', style: TextStyle(color: AppColors.textSecondary, fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 0.8)),
        const SizedBox(height: 4),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: AppSpacing.md, vertical: 10),
          decoration: BoxDecoration(
            color: const Color(0xFFF2F3FF),
            borderRadius: BorderRadius.circular(10),
            border: Border.all(color: const Color(0xFFE2E8F0)),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: const [
              Text('Infrastructure > Connection Pool Exhaustion', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              Icon(Icons.expand_more, size: 18, color: AppColors.textSecondary),
            ],
          ),
        ),
        const SizedBox(height: AppSpacing.sm),
        const Text('REMEDIATION CLASSIFICATION', style: TextStyle(color: AppColors.textSecondary, fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 0.8)),
        const SizedBox(height: 4),
        Container(
          padding: const EdgeInsets.all(3),
          decoration: BoxDecoration(color: const Color(0xFFF2F3FF), borderRadius: BorderRadius.circular(10)),
          child: Row(
            children: [
              _buildPill('Workaround'),
              _buildPill('Permanent Patch'),
              _buildPill('Config Rollback'),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildPill(String title) {
    final isSelected = _selectedRemediation == title;
    return Expanded(
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedRemediation = title;
          });
        },
        borderRadius: BorderRadius.circular(8),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: isSelected ? Colors.white : Colors.transparent,
            borderRadius: BorderRadius.circular(8),
            boxShadow: isSelected ? const [BoxShadow(color: Color(0x0A000000), blurRadius: 4)] : null,
          ),
          child: Center(
            child: Text(
              title,
              style: TextStyle(
                color: isSelected ? const Color(0xFF4648D4) : AppColors.textSecondary,
                fontSize: 10,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildCustomerNoteInput() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.between,
          children: const [
            Text('CUSTOMER-FACING NOTE', style: TextStyle(color: AppColors.textSecondary, fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 0.8)),
            Text('Dual Audited', style: TextStyle(color: AppColors.textMuted, fontSize: 10)),
          ],
        ),
        const SizedBox(height: 4),
        Container(
          decoration: BoxDecoration(
            color: const Color(0xFFF2F3FF),
            borderRadius: BorderRadius.circular(10),
            border: Border.all(color: const Color(0xFFE2E8F0)),
          ),
          child: TextField(
            controller: _noteController,
            maxLines: 3,
            style: AppTypography.bodySmall(context),
            decoration: const InputDecoration(
              border: InputBorder.none,
              contentPadding: EdgeInsets.all(AppSpacing.sm),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildKedbToggle() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: InkWell(
        onTap: () {
          setState(() {
            _publishToKedb = !_publishToKedb;
          });
        },
        child: Row(
          children: [
            Checkbox(
              value: _publishToKedb,
              onChanged: (val) {
                setState(() {
                  _publishToKedb = val ?? true;
                });
              },
              activeColor: const Color(0xFF4648D4),
            ),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: const [
                  Text('Publish to KEDB (Known Error Database)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                  Text('Enables autonomous triage matching for incident recurrence', style: TextStyle(color: AppColors.textSecondary, fontSize: 10)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStabilizationGates() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('STABILIZATION GATES', style: TextStyle(color: AppColors.textSecondary, fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 0.8)),
        const SizedBox(height: 6),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
          decoration: BoxDecoration(color: const Color(0xFFF2F3FF), borderRadius: BorderRadius.circular(8)),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: const [
              Row(
                children: [
                  Icon(Icons.check_circle, size: 16, color: Color(0xFF0D9488)),
                  SizedBox(width: 6),
                  Text('Synthetic probe telemetry passing', style: TextStyle(fontSize: 11)),
                ],
              ),
              Text('100/100', style: TextStyle(color: Color(0xFF0D9488), fontSize: 10, fontWeight: FontWeight.bold, fontFamily: 'JetBrains Mono')),
            ],
          ),
        ),
        const SizedBox(height: 4),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
          decoration: BoxDecoration(color: const Color(0xFFF2F3FF), borderRadius: BorderRadius.circular(8)),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: const [
              Row(
                children: [
                  Icon(Icons.pending, size: 16, color: Color(0xFF6366F1)),
                  SizedBox(width: 6),
                  Text('Requester dual sign-off requested', style: TextStyle(fontSize: 11)),
                ],
              ),
              Text('PENDING', style: TextStyle(color: Color(0xFF6366F1), fontSize: 9, fontWeight: FontWeight.bold, fontFamily: 'JetBrains Mono')),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildActionFooter() {
    return Column(
      children: [
        SizedBox(
          width: double.infinity,
          height: 44,
          child: ElevatedButton.icon(
            onPressed: _isDispatching ? null : _triggerHandshake,
            icon: _isDispatching
                ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                : const Icon(Icons.verified, size: 16, color: Colors.white),
            label: Text(
              _isDispatching ? 'Dispatching Handshake...' : 'Submit Resolution to Requester',
              style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
            ),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF6366F1),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              elevation: 0,
            ),
          ),
        ),
        const SizedBox(height: AppSpacing.xs),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            TextButton(
              onPressed: () => _showFeedbackToast('Case flagged: 24h Telemetry Monitoring', Icons.monitor_heart, AppColors.primary),
              child: const Text('Keep Open in Monitoring Mode', style: TextStyle(color: AppColors.textSecondary, fontSize: 11)),
            ),
            TextButton(
              onPressed: () => context.pop(),
              child: const Text('Cancel', style: TextStyle(color: Color(0xFFE11D48), fontSize: 11)),
            ),
          ],
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
          BottomNavigationBarItem(icon: Icon(Icons.dataset_outlined), activeIcon: Icon(Icons.dataset), label: 'Studio'),
          BottomNavigationBarItem(icon: Icon(Icons.radar_outlined), activeIcon: Icon(Icons.radar), label: 'Radar'),
          BottomNavigationBarItem(icon: Icon(Icons.group_outlined), activeIcon: Icon(Icons.group), label: 'Lead'),
          BottomNavigationBarItem(icon: Icon(Icons.admin_panel_settings_outlined), activeIcon: Icon(Icons.admin_panel_settings), label: 'Portal'),
        ],
      ),
    );
  }
}
