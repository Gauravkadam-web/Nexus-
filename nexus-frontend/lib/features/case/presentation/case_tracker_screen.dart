import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';
import '../domain/case_model.dart';
import 'case_state_provider.dart';

/// SCR-05: Requester Case Tracker & Confirmation Screen.
/// Provides real-time milestone trajectory, SLA countdown, and operator communication.
class CaseTrackerScreen extends ConsumerStatefulWidget {
  final String caseId;

  const CaseTrackerScreen({
    super.key,
    required this.caseId,
  });

  @override
  ConsumerState<CaseTrackerScreen> createState() => _CaseTrackerScreenState();
}

class _CaseTrackerScreenState extends ConsumerState<CaseTrackerScreen> {
  final TextEditingController _noteController = TextEditingController();
  bool _isLogAttached = false;
  bool _isResolvedConfirmed = false;

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

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark ? AppColors.darkBackground : AppColors.lightBackground,
      appBar: AppBar(
        backgroundColor: isDark ? AppColors.darkSurface : AppColors.lightSurface,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            if (context.canPop()) {
              context.pop();
            } else {
              context.go('/dashboard/requester');
            }
          },
        ),
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Case Tracker',
              style: AppTypography.headlineSmall(context),
            ),
            Text(
              'ID: ${widget.caseId}',
              style: AppTypography.codeSmall(context).copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
              ),
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_active_outlined),
            tooltip: 'Case Subscribed',
            onPressed: () {
              _showFeedbackToast(
                'Live SLA updates enabled for this incident.',
                Icons.notifications_active,
                AppColors.primary,
              );
            },
          ),
          const SizedBox(width: AppSpacing.xs),
        ],
      ),
      body: ResponsiveLayout(
        mobile: _buildContent(context, isDark, isMobile: true),
        tablet: _buildContent(context, isDark, isMobile: false),
        desktop: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 1000),
            child: _buildContent(context, isDark, isMobile: false),
          ),
        ),
      ),
    );
  }

  Widget _buildContent(BuildContext context, bool isDark, {required bool isMobile}) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSpacing.md),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header Summary Card
          _buildHeaderSummaryCard(context, isDark),
          const SizedBox(height: AppSpacing.md),

          // 5-Stage Milestone Progression
          _buildMilestoneProgressionCard(context, isDark),
          const SizedBox(height: AppSpacing.md),

          // AI Diagnostics & Remediation Box
          _buildAiRemediationCard(context, isDark),
          const SizedBox(height: AppSpacing.md),

          // Communication & Diagnostic Note Dispatch
          _buildCommunicationBox(context, isDark),
        ],
      ),
    );
  }

  Widget _buildHeaderSummaryCard(BuildContext context, bool isDark) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isDark ? AppColors.darkBorder : AppColors.lightBorder,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: [
                  StatusBadge(status: CaseStatus.investigating),
                  const SizedBox(width: AppSpacing.xs),
                  StatusBadge(severity: CaseSeverity.critical),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: isDark ? AppColors.statusWaitingBgDark : AppColors.statusWaitingBgLight,
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      Icons.timer_outlined,
                      size: 14,
                      color: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'SLA: 1h 15m left',
                      style: AppTypography.codeSmall(context).copyWith(
                        fontWeight: FontWeight.bold,
                        color: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          Text(
            'Authentication Gateway Timeout during SSO federation',
            style: AppTypography.headlineSmall(context),
          ),
          const SizedBox(height: AppSpacing.xs),
          Text(
            'Kubernetes ingress controller intermittent 502 Bad Gateway under concurrent SSO callbacks.',
            style: AppTypography.bodySmall(context).copyWith(
              color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
            ),
          ),
          const SizedBox(height: AppSpacing.sm),
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: isDark ? AppColors.darkSurfaceElevated : AppColors.lightSurfaceElevated,
              borderRadius: BorderRadius.circular(8),
            ),
            child: Row(
              children: [
                const Icon(Icons.domain_verification, size: 18, color: AppColors.primary),
                const SizedBox(width: AppSpacing.xs),
                Text(
                  'Affected Service: ',
                  style: AppTypography.bodySmall(context).copyWith(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                  ),
                ),
                Text(
                  'Okta Enterprise SSO (us-east-prod-04)',
                  style: AppTypography.titleSmall(context),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMilestoneProgressionCard(BuildContext context, bool isDark) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isDark ? AppColors.darkBorder : AppColors.lightBorder,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: [
                  const Icon(Icons.timeline, color: AppColors.primary, size: 20),
                  const SizedBox(width: AppSpacing.xs),
                  Text('Resolution Trajectory', style: AppTypography.titleSmall(context)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(
                  color: isDark ? AppColors.statusAssignedBgDark : AppColors.statusAssignedBgLight,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  'ETA ~12:30 PM',
                  style: AppTypography.codeSmall(context).copyWith(
                    color: isDark ? AppColors.statusAssignedTextDark : AppColors.statusAssignedTextLight,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.md),
          _buildMilestoneStep(
            context,
            stepNum: '1',
            title: 'Incident Reported',
            time: '10:14 AM',
            isCompleted: true,
            isDark: isDark,
          ),
          _buildMilestoneStep(
            context,
            stepNum: '2',
            title: 'Triaged & P1 Assigned',
            time: '10:22 AM',
            isCompleted: true,
            isDark: isDark,
          ),
          _buildMilestoneStep(
            context,
            stepNum: '3',
            title: 'Diagnostic Investigation (Elena Vance)',
            time: 'In Progress',
            isCompleted: false,
            isActive: true,
            isDark: isDark,
          ),
          _buildMilestoneStep(
            context,
            stepNum: '4',
            title: 'Resolution Proposal',
            time: 'Pending',
            isCompleted: false,
            isDark: isDark,
          ),
          _buildMilestoneStep(
            context,
            stepNum: '5',
            title: 'Requester Verification & Close',
            time: 'Pending',
            isCompleted: false,
            isLast: true,
            isDark: isDark,
          ),
        ],
      ),
    );
  }

  Widget _buildMilestoneStep(
    BuildContext context, {
    required String stepNum,
    required String title,
    required String time,
    required bool isCompleted,
    bool isActive = false,
    bool isLast = false,
    required bool isDark,
  }) {
    Color nodeBg = isDark ? AppColors.darkBorder : AppColors.lightBorder;
    Color nodeFg = isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary;

    if (isCompleted) {
      nodeBg = isDark ? AppColors.statusClosedBgDark : AppColors.statusClosedBgLight;
      nodeFg = isDark ? AppColors.statusClosedTextDark : AppColors.statusClosedTextLight;
    } else if (isActive) {
      nodeBg = isDark ? AppColors.statusInvestigatingBgDark : AppColors.statusInvestigatingBgLight;
      nodeFg = isDark ? AppColors.statusInvestigatingTextDark : AppColors.statusInvestigatingTextLight;
    }

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Column(
          children: [
            Container(
              width: 28,
              height: 28,
              decoration: BoxDecoration(
                color: nodeBg,
                shape: BoxShape.circle,
              ),
              alignment: Alignment.center,
              child: isCompleted
                  ? Icon(Icons.check, size: 16, color: nodeFg)
                  : Text(
                      stepNum,
                      style: TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                        color: nodeFg,
                      ),
                    ),
            ),
            if (!isLast)
              Container(
                width: 2,
                height: 24,
                color: isCompleted
                    ? (isDark ? AppColors.statusClosedBgDark : AppColors.statusClosedBgLight)
                    : (isDark ? AppColors.darkBorder : AppColors.lightBorder),
              ),
          ],
        ),
        const SizedBox(width: AppSpacing.sm),
        Expanded(
          child: Padding(
            padding: const EdgeInsets.only(top: 4),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.between,
              children: [
                Expanded(
                  child: Text(
                    title,
                    style: AppTypography.bodyMedium(context).copyWith(
                      fontWeight: (isCompleted || isActive) ? FontWeight.w600 : FontWeight.normal,
                      color: isActive ? nodeFg : null,
                    ),
                  ),
                ),
                Text(
                  time,
                  style: AppTypography.codeSmall(context).copyWith(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildAiRemediationCard(BuildContext context, bool isDark) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: isDark ? AppColors.aiBgDark : AppColors.aiBgLight,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isDark ? AppColors.aiBorderDark : AppColors.aiBorderLight,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.auto_awesome, color: AppColors.aiAccent, size: 20),
              const SizedBox(width: AppSpacing.xs),
              Text(
                'AI Triage Diagnostic Summary',
                style: AppTypography.titleSmall(context).copyWith(color: AppColors.aiAccent),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.xs),
          Text(
            'Ingress envoy logs indicate connection pool saturation during burst traffic. Suggested playbook #ENV-884 applied to standby pods.',
            style: AppTypography.bodySmall(context),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              Expanded(
                child: NexusButton(
                  text: _isResolvedConfirmed ? 'Resolution Verified' : 'Confirm Resolution',
                  icon: Icons.check_circle_outline,
                  variant: NexusButtonVariant.secondary,
                  onPressed: _isResolvedConfirmed
                      ? null
                      : () {
                          setState(() => _isResolvedConfirmed = true);
                          _showFeedbackToast(
                            'Case NEX-2026-0104 confirmed resolved.',
                            Icons.task_alt,
                            AppColors.statusClosedTextLight,
                          );
                        },
                ),
              ),
              const SizedBox(width: AppSpacing.sm),
              NexusButton(
                text: 'Reopen',
                icon: Icons.refresh,
                variant: NexusButtonVariant.ghost,
                onPressed: () {
                  _showFeedbackToast(
                    'Reopen signal dispatched to triage lead.',
                    Icons.report_problem,
                    AppColors.statusBreachedTextLight,
                  );
                },
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCommunicationBox(BuildContext context, bool isDark) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isDark ? AppColors.darkBorder : AppColors.lightBorder,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('Diagnostic Notes & Logs', style: AppTypography.titleSmall(context)),
          const SizedBox(height: AppSpacing.xs),
          TextField(
            controller: _noteController,
            maxLines: 3,
            decoration: InputDecoration(
              hintText: 'Type an operational note or paste log trace for Elena Vance...',
              hintStyle: AppTypography.bodySmall(context).copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
              ),
              filled: true,
              fillColor: isDark ? AppColors.darkSurfaceElevated : AppColors.lightSurfaceElevated,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide.none,
              ),
            ),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              OutlinedButton.icon(
                icon: Icon(
                  _isLogAttached ? Icons.check : Icons.attach_file,
                  size: 16,
                  color: _isLogAttached ? AppColors.statusClosedTextLight : null,
                ),
                label: Text(_isLogAttached ? 'Log Attached' : 'Attach Log'),
                onPressed: () {
                  setState(() => _isLogAttached = !_isLogAttached);
                  _showFeedbackToast(
                    _isLogAttached ? 'Attached envoy-debug.log (1.8MB)' : 'Attachment removed',
                    Icons.attachment,
                    AppColors.primary,
                  );
                },
              ),
              NexusButton(
                text: 'Send Note',
                icon: Icons.send,
                onPressed: () {
                  final text = _noteController.text.trim();
                  if (text.isEmpty) {
                    _showFeedbackToast('Please enter a note before sending.', Icons.info_outline, Colors.orange);
                    return;
                  }
                  _noteController.clear();
                  setState(() => _isLogAttached = false);
                  _showFeedbackToast('Note sent to Operator Elena Vance.', Icons.send, AppColors.primary);
                },
              ),
            ],
          ),
        ],
      ),
    );
  }
}
