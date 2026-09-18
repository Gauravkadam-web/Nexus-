import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import '../../../app.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/kpi_card.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';
import '../../auth/presentation/auth_state_provider.dart';
import '../../case/domain/case_model.dart';
import '../../case/presentation/case_state_provider.dart';

class RequesterDashboardScreen extends ConsumerWidget {
  const RequesterDashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final caseState = ref.watch(caseStateProvider);
    final authState = ref.watch(authStateProvider);
    final user = authState.user;

    return Scaffold(
      backgroundColor: isDark ? AppColors.darkCanvas : AppColors.lightCanvas,
      appBar: _buildAppBar(context, ref, isDark, user?.name ?? 'Requester'),
      floatingActionButton: ResponsiveLayout.isMobile(context)
          ? FloatingActionButton.extended(
              onPressed: () => context.go('/cases/new'),
              backgroundColor: isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary,
              icon: const Icon(Icons.add, color: Colors.white),
              label: const Text('Report Case', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
            )
          : null,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: EdgeInsets.symmetric(
            horizontal: ResponsiveLayout.isMobile(context)
                ? AppSpacing.mobileGutter
                : AppSpacing.desktopGutter,
            vertical: AppSpacing.lg,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Welcome & Action Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Welcome back, ${user?.name.split(' ').first ?? 'Sarah'}',
                        style: AppTypography.headlineMedium(isDark),
                      ),
                      const SizedBox(height: AppSpacing.xs),
                      Text(
                        'Track your active support requests and organization tickets.',
                        style: AppTypography.bodySmall(isDark),
                      ),
                    ],
                  ),
                  if (!ResponsiveLayout.isMobile(context))
                    NexusButton(
                      text: '+ Report a Case',
                      icon: Icons.add,
                      onPressed: () => context.go('/cases/new'),
                    ),
                ],
              ),
              const SizedBox(height: AppSpacing.xl),

              // KPI Row (4 cards on Desktop, 2x2 grid on Mobile)
              _buildKpiGrid(context, isDark, caseState.requesterStats),
              const SizedBox(height: AppSpacing.xl),

              // Action Required Callout Banner (Soft Apricot Tint)
              _buildActionRequiredBanner(context, isDark),
              const SizedBox(height: AppSpacing.xl),

              // Cases List Section Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text('Your Reported Cases', style: AppTypography.titleMedium(isDark)),
                  Row(
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: isDark ? AppColors.darkSurfaceElevated : AppColors.lightSurfaceElevated,
                          borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                          border: Border.all(color: isDark ? AppColors.darkBorder : AppColors.lightBorder),
                        ),
                        child: Row(
                          children: [
                            Icon(Icons.filter_list, size: 16, color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary),
                            const SizedBox(width: 4),
                            Text('All Cases (${caseState.cases.length})', style: AppTypography.labelSmall(isDark)),
                          ],
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              const SizedBox(height: AppSpacing.md),

              // Cases Data List (Responsive Table or Cards)
              _buildCasesList(context, isDark, caseState.cases),
            ],
          ),
        ),
      ),
    );
  }

  PreferredSizeWidget _buildAppBar(BuildContext context, WidgetRef ref, bool isDark, String userName) {
    return AppBar(
      backgroundColor: isDark ? AppColors.darkSurface : AppColors.lightSurface,
      elevation: 0,
      scrolledUnderElevation: 0,
      bottom: PreferredSize(
        preferredSize: const Size.fromHeight(1),
        child: Container(color: isDark ? AppColors.darkBorder : AppColors.lightBorder, height: 1),
      ),
      title: Row(
        children: [
          Container(
            width: 28,
            height: 28,
            decoration: BoxDecoration(
              gradient: const LinearGradient(colors: [AppColors.accentPrimary, AppColors.aiLilac]),
              borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
            ),
            child: const Center(
              child: Text('N', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
            ),
          ),
          const SizedBox(width: AppSpacing.sm),
          Text('Nexus', style: AppTypography.titleMedium(isDark)),
          const SizedBox(width: AppSpacing.sm),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
            decoration: BoxDecoration(
              color: isDark ? AppColors.accentTintDark : AppColors.accentTintLight,
              borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
            ),
            child: Text(
              'Requester Portal',
              style: TextStyle(
                color: isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary,
                fontSize: 11,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
      actions: [
        // Theme Toggle Button
        IconButton(
          icon: Icon(
            isDark ? Icons.light_mode_outlined : Icons.dark_mode_outlined,
            color: isDark ? AppColors.darkTextPrimary : AppColors.lightTextPrimary,
            size: 20,
          ),
          onPressed: () {
            ref.read(themeModeProvider.notifier).state =
                isDark ? ThemeMode.light : ThemeMode.dark;
          },
        ),
        // User Profile Avatar
        Padding(
          padding: const EdgeInsets.only(right: 16),
          child: Row(
            children: [
              CircleAvatar(
                radius: 16,
                backgroundColor: isDark ? AppColors.accentTintDark : AppColors.accentTintLight,
                child: Text(
                  userName.isNotEmpty ? userName[0].toUpperCase() : 'U',
                  style: TextStyle(
                    color: isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary,
                    fontWeight: FontWeight.bold,
                    fontSize: 14,
                  ),
                ),
              ),
              const SizedBox(width: AppSpacing.xs),
              IconButton(
                icon: const Icon(Icons.logout, size: 18),
                onPressed: () {
                  ref.read(authStateProvider.notifier).logout();
                  context.go('/auth/login');
                },
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildKpiGrid(BuildContext context, bool isDark, RequesterDashboardStats? stats) {
    final isMobile = ResponsiveLayout.isMobile(context);

    final kpis = [
      KpiCard(
        title: 'Active Cases',
        value: '${stats?.activeCases ?? 3}',
        subtitle: 'In triage & investigation',
        icon: Icons.pending_actions_outlined,
        accentColor: isDark ? AppColors.statusTriageTextDark : AppColors.statusTriageTextLight,
      ),
      KpiCard(
        title: 'Awaiting Your Reply',
        value: '${stats?.awaitingReply ?? 1}',
        subtitle: 'Operator requested info',
        icon: Icons.mark_chat_unread_outlined,
        accentColor: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
      ),
      KpiCard(
        title: 'Resolved Cases',
        value: '${stats?.resolvedCount ?? 14}',
        subtitle: 'Successfully closed',
        icon: Icons.task_alt_outlined,
        accentColor: isDark ? AppColors.statusClosedTextDark : AppColors.statusClosedTextLight,
      ),
      KpiCard(
        title: 'Avg. Turnaround',
        value: '${stats?.avgTurnaroundHours ?? 4.2}h',
        subtitle: 'Resolution SLA pace',
        icon: Icons.speed_outlined,
        accentColor: isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary,
      ),
    ];

    if (isMobile) {
      return GridView.count(
        crossAxisCount: 2,
        crossAxisSpacing: AppSpacing.sm,
        mainAxisSpacing: AppSpacing.sm,
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        childAspectRatio: 1.15,
        children: kpis,
      );
    }

    return Row(
      children: kpis
          .map((kpi) => Expanded(
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: AppSpacing.xs),
                  child: kpi,
                ),
              ))
          .toList(),
    );
  }

  Widget _buildActionRequiredBanner(BuildContext context, bool isDark) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.cardPaddingMobile),
      decoration: BoxDecoration(
        color: isDark ? AppColors.statusWaitingBgDark : AppColors.statusWaitingBgLight,
        borderRadius: BorderRadius.circular(AppSpacing.radiusLg),
        border: Border.all(
          color: (isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight).withOpacity(0.3),
        ),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
              shape: BoxShape.circle,
            ),
            child: Icon(
              Icons.priority_high,
              size: 20,
              color: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
            ),
          ),
          const SizedBox(width: AppSpacing.md),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      'Action Required: NEX-2026-0042',
                      style: TextStyle(
                        color: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                    const SizedBox(width: AppSpacing.sm),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 1),
                      decoration: BoxDecoration(
                        color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
                        borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                      ),
                      child: Text('Awaiting Logs', style: AppTypography.labelSmall(isDark)),
                    ),
                  ],
                ),
                const SizedBox(height: 2),
                Text(
                  'Lead Operator Elena Vance requested system VPN connection log files to diagnose gateway reset.',
                  style: AppTypography.bodySmall(isDark),
                ),
              ],
            ),
          ),
          const SizedBox(width: AppSpacing.md),
          NexusButton(
            text: 'Reply Now',
            icon: Icons.reply,
            height: 36,
            onPressed: () {
              // Navigate to track screen
            },
          ),
        ],
      ),
    );
  }

  Widget _buildCasesList(BuildContext context, bool isDark, List<CaseModel> cases) {
    if (cases.isEmpty) {
      return Container(
        padding: const EdgeInsets.all(AppSpacing.xxl),
        decoration: BoxDecoration(
          color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
          borderRadius: BorderRadius.circular(AppSpacing.radiusLg),
          border: Border.all(color: isDark ? AppColors.darkBorder : AppColors.lightBorder),
        ),
        child: const Center(child: Text('No cases reported yet.')),
      );
    }

    return Column(
      children: cases.map((c) => _buildCaseCard(context, isDark, c)).toList(),
    );
  }

  Widget _buildCaseCard(BuildContext context, bool isDark, CaseModel item) {
    final formattedDate = DateFormat('MMM dd, yyyy • hh:mm a').format(item.createdAt);

    return Container(
      margin: const EdgeInsets.only(bottom: AppSpacing.md),
      padding: const EdgeInsets.all(AppSpacing.cardPaddingMobile),
      decoration: BoxDecoration(
        color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
        borderRadius: BorderRadius.circular(AppSpacing.radiusLg),
        border: Border.all(color: isDark ? AppColors.darkBorder : AppColors.lightBorder),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Top row: ID, Category, Date, Status
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(
                      color: isDark ? AppColors.darkSurfaceElevated : AppColors.lightSurfaceElevated,
                      borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                      border: Border.all(color: isDark ? AppColors.darkBorder : AppColors.lightBorder),
                    ),
                    child: Text(item.id, style: AppTypography.codeSmall(isDark)),
                  ),
                  const SizedBox(width: AppSpacing.sm),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(
                      color: isDark ? AppColors.accentTintDark : AppColors.accentTintLight,
                      borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                    ),
                    child: Text(
                      item.categoryName ?? 'IT Support',
                      style: TextStyle(
                        color: isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary,
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
              StatusBadge(status: item.status),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),

          // Title
          Text(item.title, style: AppTypography.titleMedium(isDark)),
          const SizedBox(height: 4),

          // Description preview
          Text(
            item.description,
            style: AppTypography.bodySmall(isDark),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: AppSpacing.md),

          // Milestone Stepper Bar (4 dots)
          _buildMilestoneStepper(isDark, item.milestoneStep),
          const SizedBox(height: AppSpacing.sm),

          // Footer info
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Reported on $formattedDate',
                style: AppTypography.labelSmall(isDark),
              ),
              if (item.assignedOperatorName != null)
                Text(
                  'Assigned to: ${item.assignedOperatorName}',
                  style: TextStyle(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                    fontSize: 12,
                    fontWeight: FontWeight.w500,
                  ),
                ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildMilestoneStepper(bool isDark, int currentStep) {
    final steps = ['Reported', 'In Progress', 'Resolution Proposed', 'Closed'];

    return Row(
      children: List.generate(steps.length * 2 - 1, (index) {
        if (index.isOdd) {
          final stepIndex = (index ~/ 2) + 1;
          final isCompleted = currentStep > stepIndex;
          return Expanded(
            child: Container(
              height: 2,
              color: isCompleted
                  ? (isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary)
                  : (isDark ? AppColors.darkBorder : AppColors.lightBorder),
            ),
          );
        } else {
          final stepNum = (index ~/ 2) + 1;
          final isDone = currentStep >= stepNum;
          final isCurrent = currentStep == stepNum;

          return Row(
            children: [
              Container(
                width: 16,
                height: 16,
                decoration: BoxDecoration(
                  color: isDone
                      ? (isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary)
                      : (isDark ? AppColors.darkSurfaceElevated : AppColors.lightSurfaceElevated),
                  shape: BoxShape.circle,
                  border: Border.all(
                    color: isDone
                        ? (isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary)
                        : (isDark ? AppColors.darkBorder : AppColors.lightBorder),
                    width: 2,
                  ),
                ),
                child: isDone
                    ? const Icon(Icons.check, size: 10, color: Colors.white)
                    : null,
              ),
            ],
          );
        }
      }),
    );
  }
}
