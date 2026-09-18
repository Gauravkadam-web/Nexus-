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

/// Filter options for Operator Triage Feed
enum TriageFilter {
  assignedToMe,
  unassignedTriage,
  slaBreaching,
  highPriority,
}

/// SCR-06: Operator Triage Workstation Feed Screen
/// Live queue, 2x2 operational KPI metrics, AI Incident Correlation, and fast-action triage cards.
class OperatorTriageFeedScreen extends ConsumerStatefulWidget {
  const OperatorTriageFeedScreen({super.key});

  @override
  ConsumerState<OperatorTriageFeedScreen> createState() => _OperatorTriageFeedScreenState();
}

class _OperatorTriageFeedScreenState extends ConsumerState<OperatorTriageFeedScreen> {
  TriageFilter _selectedFilter = TriageFilter.assignedToMe;
  final TextEditingController _searchController = TextEditingController();
  int _currentNavIndex = 0;

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
      titleSpacing: AppSpacing.md,
      title: Row(
        children: [
          Container(
            width: 36,
            height: 36,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF6366F1), Color(0xFF9333EA)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(10),
            ),
            child: const Center(
              child: Text(
                'N',
                style: TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 18,
                  fontFamily: 'Outfit',
                ),
              ),
            ),
          ),
          const SizedBox(width: AppSpacing.sm),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Nexus AI',
                style: AppTypography.headlineSmall(context).copyWith(
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                ),
              ),
              Text(
                'OPERATIONS',
                style: AppTypography.labelSmall(context).copyWith(
                  color: AppColors.textMuted,
                  letterSpacing: 1.2,
                  fontSize: 9,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ],
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: () {
            _showFeedbackToast('Global search index ready', Icons.search, AppColors.primary);
          },
          icon: const Icon(Icons.search, color: AppColors.textSecondary, size: 22),
          tooltip: 'Search cases',
        ),
        Stack(
          alignment: Alignment.center,
          children: [
            IconButton(
              onPressed: () {
                _showFeedbackToast('2 critical SLA alerts active', Icons.notifications, const Color(0xFFE11D48));
              },
              icon: const Icon(Icons.notifications_outlined, color: AppColors.textSecondary, size: 22),
              tooltip: 'Notifications',
            ),
            Positioned(
              top: 12,
              right: 12,
              child: Container(
                width: 8,
                height: 8,
                decoration: BoxDecoration(
                  color: const Color(0xFFE11D48),
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.white, width: 1.5),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(width: AppSpacing.xs),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 16,
            backgroundColor: AppColors.primary.withOpacity(0.1),
            child: const Icon(Icons.person, size: 18, color: AppColors.primary),
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
          _buildStationHeader(),
          const SizedBox(height: AppSpacing.sm),
          _buildSearchAndShortcutBar(),
          const SizedBox(height: AppSpacing.sm),
          _buildFilterPills(),
          const SizedBox(height: AppSpacing.md),
          _build2x2KpiGrid(),
          const SizedBox(height: AppSpacing.md),
          _buildAiCorrelationBanner(),
          const SizedBox(height: AppSpacing.md),
          _buildFeedSectionHeader(),
          const SizedBox(height: AppSpacing.sm),
          _buildCaseFeedList(),
          const SizedBox(height: AppSpacing.md),
          _buildWarRoomBridgeBanner(),
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
              _buildStationHeader(),
              const SizedBox(height: AppSpacing.md),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    flex: 6,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildSearchAndShortcutBar(),
                        const SizedBox(height: AppSpacing.sm),
                        _buildFilterPills(),
                        const SizedBox(height: AppSpacing.md),
                        _buildAiCorrelationBanner(),
                        const SizedBox(height: AppSpacing.md),
                        _buildFeedSectionHeader(),
                        const SizedBox(height: AppSpacing.sm),
                        _buildCaseFeedList(),
                      ],
                    ),
                  ),
                  const SizedBox(width: AppSpacing.lg),
                  Expanded(
                    flex: 4,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _build2x2KpiGrid(),
                        const SizedBox(height: AppSpacing.lg),
                        _buildWarRoomBridgeBanner(),
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

  Widget _buildStationHeader() {
    final utcTimeStr = DateFormat('HH:mm').format(DateTime.now().toUtc());
    return Row(
      mainAxisAlignment: MainAxisAlignment.between,
      children: [
        Row(
          children: [
            Container(
              width: 10,
              height: 10,
              decoration: const BoxDecoration(
                color: Color(0xFF0D9488),
                shape: BoxShape.circle,
              ),
            ),
            const SizedBox(width: AppSpacing.xs),
            Text(
              'OPS-STATION // ALPHA',
              style: AppTypography.codeSmall(context).copyWith(
                fontWeight: FontWeight.bold,
                letterSpacing: 1.2,
                color: AppColors.textPrimary,
              ),
            ),
          ],
        ),
        Row(
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                color: const Color(0xFFF2F3FF),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                children: [
                  const Icon(Icons.schedule, size: 14, color: AppColors.textSecondary),
                  const SizedBox(width: 4),
                  Text(
                    'UTC $utcTimeStr',
                    style: AppTypography.codeSmall(context).copyWith(
                      fontWeight: FontWeight.w600,
                      color: AppColors.textSecondary,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(width: AppSpacing.xs),
            IconButton(
              onPressed: () {
                _showFeedbackToast('Matrix filters synchronized', Icons.tune, AppColors.primary);
              },
              icon: const Icon(Icons.tune, size: 18, color: AppColors.textSecondary),
              constraints: const BoxConstraints(minWidth: 32, minHeight: 32),
              padding: EdgeInsets.zero,
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildSearchAndShortcutBar() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0xFFE2E8F0)),
        boxShadow: const [
          BoxShadow(
            color: Color(0x05000000),
            blurRadius: 4,
            offset: Offset(0, 2),
          ),
        ],
      ),
      child: TextField(
        controller: _searchController,
        style: AppTypography.bodySmall(context),
        decoration: InputDecoration(
          hintText: 'Filter by ID, service, or keyword...',
          hintStyle: AppTypography.bodySmall(context).copyWith(color: AppColors.textMuted),
          prefixIcon: const Icon(Icons.filter_alt_outlined, color: AppColors.textMuted, size: 20),
          suffixIcon: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
              decoration: BoxDecoration(
                color: const Color(0xFFE2E7FF),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                '⌘K',
                style: AppTypography.codeSmall(context).copyWith(
                  color: AppColors.textSecondary,
                  fontSize: 10,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(horizontal: AppSpacing.md, vertical: 12),
        ),
      ),
    );
  }

  Widget _buildFilterPills() {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: [
          _buildFilterPill(
            filter: TriageFilter.assignedToMe,
            label: 'Assigned to Me',
            count: 8,
            badgeColor: AppColors.primary,
          ),
          const SizedBox(width: AppSpacing.xs),
          _buildFilterPill(
            filter: TriageFilter.unassignedTriage,
            label: 'Unassigned Triage',
            count: 4,
            badgeColor: AppColors.textSecondary,
          ),
          const SizedBox(width: AppSpacing.xs),
          _buildFilterPill(
            filter: TriageFilter.slaBreaching,
            label: 'SLA Breaching',
            count: 2,
            badgeColor: const Color(0xFFE11D48),
            isAlert: true,
          ),
          const SizedBox(width: AppSpacing.xs),
          _buildFilterPill(
            filter: TriageFilter.highPriority,
            label: 'High Priority',
            count: 3,
            badgeColor: const Color(0xFFD97706),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterPill({
    required TriageFilter filter,
    required String label,
    required int count,
    required Color badgeColor,
    bool isAlert = false,
  }) {
    final isSelected = _selectedFilter == filter;
    return InkWell(
      onTap: () {
        setState(() {
          _selectedFilter = filter;
        });
      },
      borderRadius: BorderRadius.circular(20),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 7),
        decoration: BoxDecoration(
          color: isSelected
              ? const Color(0xFFEEF2FF)
              : (isAlert ? const Color(0xFFFFE4E6).withOpacity(0.5) : Colors.white),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: isSelected
                ? const Color(0xFF6366F1)
                : (isAlert ? const Color(0xFFE11D48).withOpacity(0.3) : const Color(0xFFE2E8F0)),
            width: isSelected ? 1.5 : 1,
          ),
        ),
        child: Row(
          children: [
            Text(
              label,
              style: TextStyle(
                color: isSelected
                    ? const Color(0xFF4648D4)
                    : (isAlert ? const Color(0xFFE11D48) : AppColors.textSecondary),
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
                fontSize: 12,
              ),
            ),
            const SizedBox(width: 6),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 1),
              decoration: BoxDecoration(
                color: badgeColor.withOpacity(0.15),
                borderRadius: BorderRadius.circular(10),
              ),
              child: Text(
                '$count',
                style: TextStyle(
                  color: badgeColor,
                  fontWeight: FontWeight.bold,
                  fontSize: 10,
                  fontFamily: 'JetBrains Mono',
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _build2x2KpiGrid() {
    return GridView.count(
      crossAxisCount: 2,
      crossAxisSpacing: AppSpacing.sm,
      mainAxisSpacing: AppSpacing.sm,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      childAspectRatio: 1.35,
      children: [
        _buildKpiCard(
          title: 'Active Queue',
          badgeText: '4 Urgent',
          badgeBg: const Color(0xFFFFE4E6),
          badgeTextCol: const Color(0xFFE11D48),
          mainValue: '8',
          subUnit: 'Cases',
          footnoteIcon: Icons.emergency,
          footnoteText: 'High load distribution',
          footnoteColor: const Color(0xFFD97706),
        ),
        _buildKpiCard(
          title: 'Avg Response',
          badgeIcon: Icons.timer,
          badgeColor: const Color(0xFF0D9488),
          mainValue: '18',
          subUnit: 'min',
          footnoteIcon: Icons.arrow_downward,
          footnoteText: '-4m vs target',
          footnoteColor: const Color(0xFF0D9488),
        ),
        _buildKpiCard(
          title: 'SLA Guard',
          statusDotColor: const Color(0xFF0D9488),
          mainValue: '98.2%',
          subUnit: '',
          footnoteIcon: Icons.verified_user,
          footnoteText: 'Nominal safety band',
          footnoteColor: const Color(0xFF0D9488),
        ),
        _buildKpiCard(
          title: 'Resolved',
          badgeText: '6/shift',
          mainValue: '5',
          subUnit: 'closed',
          showProgressBar: true,
          progressPercent: 0.83,
        ),
      ],
    );
  }

  Widget _buildKpiCard({
    required String title,
    String? badgeText,
    Color? badgeBg,
    Color? badgeTextCol,
    IconData? badgeIcon,
    Color? badgeColor,
    Color? statusDotColor,
    required String mainValue,
    required String subUnit,
    IconData? footnoteIcon,
    String? footnoteText,
    Color? footnoteColor,
    bool showProgressBar = false,
    double progressPercent = 0.0,
  }) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm),
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
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Text(
                title.toUpperCase(),
                style: AppTypography.labelSmall(context).copyWith(
                  color: AppColors.textMuted,
                  letterSpacing: 0.8,
                  fontSize: 10,
                  fontWeight: FontWeight.w700,
                ),
              ),
              if (badgeText != null)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 1.5),
                  decoration: BoxDecoration(
                    color: badgeBg ?? const Color(0xFFF1F5F9),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(
                    badgeText,
                    style: TextStyle(
                      color: badgeTextCol ?? AppColors.textSecondary,
                      fontSize: 9,
                      fontWeight: FontWeight.bold,
                      fontFamily: 'JetBrains Mono',
                    ),
                  ),
                )
              else if (badgeIcon != null)
                Icon(badgeIcon, size: 14, color: badgeColor)
              else if (statusDotColor != null)
                Container(
                  width: 7,
                  height: 7,
                  decoration: BoxDecoration(
                    color: statusDotColor,
                    shape: BoxShape.circle,
                  ),
                ),
            ],
          ),
          Row(
            crossAxisAlignment: CrossAxisAlignment.baseline,
            textBaseline: TextBaseline.alphabetic,
            children: [
              Text(
                mainValue,
                style: AppTypography.headlineMedium(context).copyWith(
                  fontWeight: FontWeight.bold,
                  fontSize: 22,
                ),
              ),
              if (subUnit.isNotEmpty) ...[
                const SizedBox(width: 4),
                Text(
                  subUnit,
                  style: AppTypography.bodySmall(context).copyWith(
                    color: AppColors.textSecondary,
                    fontSize: 11,
                  ),
                ),
              ],
            ],
          ),
          if (showProgressBar)
            Row(
              children: [
                Expanded(
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(4),
                    child: LinearProgressIndicator(
                      value: progressPercent,
                      minHeight: 5,
                      backgroundColor: const Color(0xFFEAEDFF),
                      valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFF4648D4)),
                    ),
                  ),
                ),
                const SizedBox(width: 6),
                Text(
                  '${(progressPercent * 100).toInt()}%',
                  style: AppTypography.codeSmall(context).copyWith(
                    fontSize: 9,
                    color: AppColors.textMuted,
                  ),
                ),
              ],
            )
          else if (footnoteText != null)
            Row(
              children: [
                if (footnoteIcon != null)
                  Icon(footnoteIcon, size: 12, color: footnoteColor ?? AppColors.textSecondary),
                const SizedBox(width: 3),
                Expanded(
                  child: Text(
                    footnoteText,
                    style: TextStyle(
                      fontSize: 10,
                      color: footnoteColor ?? AppColors.textSecondary,
                      fontWeight: FontWeight.w500,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
        ],
      ),
    );
  }

  Widget _buildAiCorrelationBanner() {
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
                children: [
                  const Text('✨', style: TextStyle(fontSize: 14)),
                  const SizedBox(width: 6),
                  Text(
                    'AI Correlation Detected',
                    style: AppTypography.titleSmall(context).copyWith(
                      color: const Color(0xFF831ADA),
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(
                  color: const Color(0xFF831ADA).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Text(
                  '94% Confidence',
                  style: TextStyle(
                    color: Color(0xFF7C3AED),
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                    fontFamily: 'JetBrains Mono',
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.xs),
          RichText(
            text: TextSpan(
              style: AppTypography.bodySmall(context).copyWith(
                color: const Color(0xFF464554),
                height: 1.4,
              ),
              children: const [
                TextSpan(text: 'NEX-2026-0104', style: TextStyle(fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
                TextSpan(text: ' and '),
                TextSpan(text: 'NEX-2026-0087', style: TextStyle(fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
                TextSpan(text: ' share common root cause: '),
                TextSpan(
                  text: 'Redis connection pool exhaustion under SSO federation burst.',
                  style: TextStyle(fontWeight: FontWeight.w600, color: AppColors.textPrimary),
                ),
              ],
            ),
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            children: [
              Expanded(
                child: OutlinedButton.icon(
                  onPressed: () {
                    _showFeedbackToast('Opening telemetry diff comparison', Icons.compare_arrows, AppColors.primary);
                  },
                  icon: const Icon(Icons.compare_arrows, size: 16),
                  label: const Text('Inspect Diff', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                  style: OutlinedButton.styleFrom(
                    backgroundColor: Colors.white,
                    foregroundColor: AppColors.textSecondary,
                    side: const BorderSide(color: Color(0xFFE2E8F0)),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    padding: const EdgeInsets.symmetric(vertical: 8),
                  ),
                ),
              ),
              const SizedBox(width: AppSpacing.sm),
              Expanded(
                child: ElevatedButton.icon(
                  onPressed: () {
                    _showFeedbackToast('Linked cases correlated into cluster', Icons.merge_type, const Color(0xFF831ADA));
                  },
                  icon: const Icon(Icons.merge_type, size: 16, color: Colors.white),
                  label: const Text('Correlate Cases', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Colors.white)),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF831ADA),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    padding: const EdgeInsets.symmetric(vertical: 8),
                    elevation: 0,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildFeedSectionHeader() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.between,
      children: [
        Text(
          'Active Workstream',
          style: AppTypography.headlineSmall(context).copyWith(
            fontWeight: FontWeight.bold,
            fontSize: 16,
          ),
        ),
        Row(
          children: [
            const Icon(Icons.sync, size: 14, color: AppColors.textMuted),
            const SizedBox(width: 4),
            Text(
              'Realtime Stream',
              style: AppTypography.labelSmall(context).copyWith(
                color: AppColors.textMuted,
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildCaseFeedList() {
    return Column(
      children: [
        _buildFeedCard(
          caseNumber: 'NEX-2026-0104',
          priorityLabel: 'P1 Critical',
          priorityBg: const Color(0xFFFFE4E6),
          priorityTextCol: const Color(0xFFE11D48),
          statusLabel: 'Investigating',
          statusBg: const Color(0xFFFEF3C7),
          statusTextCol: const Color(0xFFD97706),
          title: 'Authentication Gateway Timeout during SSO federation',
          description: 'Upstream IDP response latency spiked to 4.8s. 12 enterprise tenants encountering HTTP 504 gateway response errors.',
          assigneeName: 'Sarah Jenkins',
          assigneeRole: 'Cloud Ops Lead',
          slaCountdown: '12m left',
          slaBg: const Color(0xFFFFE4E6),
          slaTextCol: const Color(0xFFE11D48),
          slaPulse: true,
          secondaryActionText: 'Reassign',
          onSecondaryAction: () => _showFeedbackToast('Reassignment matrix opened', Icons.swap_horiz, AppColors.primary),
          primaryActionText: 'Open Studio',
          onPrimaryAction: () {
            context.push('/cases/NEX-2026-0104/track');
          },
        ),
        const SizedBox(height: AppSpacing.sm),
        _buildFeedCard(
          caseNumber: 'NEX-2026-0087',
          priorityLabel: 'P1 Critical',
          priorityBg: const Color(0xFFFFE4E6),
          priorityTextCol: const Color(0xFFE11D48),
          statusLabel: 'Investigating',
          statusBg: const Color(0xFFFEF3C7),
          statusTextCol: const Color(0xFFD97706),
          title: 'Kubernetes ingress controller intermittent 502',
          description: 'Nginx ingress pod OOM restarts detected across us-east-2 cluster 4. Keepalive connection sockets saturated.',
          assigneeName: 'David Ross',
          assigneeRole: 'Principal SRE',
          slaCountdown: '14m left',
          slaBg: const Color(0xFFFFEDD5),
          slaTextCol: const Color(0xFFEA580C),
          secondaryActionText: 'Runbook',
          secondaryIcon: Icons.menu_book,
          onSecondaryAction: () => _showFeedbackToast('Runbook KB-4091 loaded', Icons.menu_book, AppColors.primary),
          primaryActionText: 'Investigate',
          primaryIcon: Icons.query_stats,
          onPrimaryAction: () {
            context.push('/cases/NEX-2026-0087/track');
          },
        ),
        const SizedBox(height: AppSpacing.sm),
        _buildFeedCard(
          caseNumber: 'NEX-2026-0098',
          priorityLabel: 'P2 High',
          priorityBg: const Color(0xFFFFEDD5),
          priorityTextCol: const Color(0xFFEA580C),
          statusLabel: 'Triage',
          statusBg: const Color(0xFFE0F2FE),
          statusTextCol: const Color(0xFF0284C7),
          title: 'Postgres WAL replica lag exceeding 350ms threshold',
          description: 'Analytical read replica cluster showing steady replication backlog during scheduled month-end aggregation pipeline.',
          assigneeName: 'Michael Chang',
          assigneeRole: 'Data Platform Lead',
          slaCountdown: '28m left',
          slaBg: const Color(0xFFF2F3FF),
          slaTextCol: AppColors.textSecondary,
          fullWidthActionText: 'Assign to Me',
          fullWidthActionIcon: Icons.person_add,
          onFullWidthAction: () => _showFeedbackToast('Case assigned to your active queue', Icons.check_circle, const Color(0xFF0D9488)),
        ),
      ],
    );
  }

  Widget _buildFeedCard({
    required String caseNumber,
    required String priorityLabel,
    required Color priorityBg,
    required Color priorityTextCol,
    required String statusLabel,
    required Color statusBg,
    required Color statusTextCol,
    required String title,
    required String description,
    required String assigneeName,
    required String assigneeRole,
    required String slaCountdown,
    required Color slaBg,
    required Color slaTextCol,
    bool slaPulse = false,
    String? secondaryActionText,
    IconData? secondaryIcon,
    VoidCallback? onSecondaryAction,
    String? primaryActionText,
    IconData? primaryIcon,
    VoidCallback? onPrimaryAction,
    String? fullWidthActionText,
    IconData? fullWidthActionIcon,
    VoidCallback? onFullWidthAction,
  }) {
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
                  Text(
                    caseNumber,
                    style: AppTypography.codeSmall(context).copyWith(
                      fontWeight: FontWeight.bold,
                      color: AppColors.textPrimary,
                    ),
                  ),
                  const SizedBox(width: AppSpacing.xs),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 2),
                    decoration: BoxDecoration(
                      color: priorityBg,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      priorityLabel.toUpperCase(),
                      style: TextStyle(
                        color: priorityTextCol,
                        fontSize: 9,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 0.5,
                      ),
                    ),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(
                  color: statusBg,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  statusLabel.toUpperCase(),
                  style: TextStyle(
                    color: statusTextCol,
                    fontSize: 9,
                    fontWeight: FontWeight.w700,
                    letterSpacing: 0.5,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.xs),
          Text(
            title,
            style: AppTypography.titleSmall(context).copyWith(
              fontWeight: FontWeight.bold,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            description,
            style: AppTypography.bodySmall(context).copyWith(
              color: AppColors.textSecondary,
              fontSize: 12,
              height: 1.35,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: AppSpacing.sm),
          Row(
            mainAxisAlignment: MainAxisAlignment.between,
            children: [
              Row(
                children: [
                  CircleAvatar(
                    radius: 12,
                    backgroundColor: AppColors.primary.withOpacity(0.15),
                    child: Text(
                      assigneeName[0],
                      style: const TextStyle(
                        color: AppColors.primary,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(width: 6),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        assigneeName,
                        style: const TextStyle(
                          fontSize: 11,
                          fontWeight: FontWeight.w600,
                          color: AppColors.textPrimary,
                        ),
                      ),
                      Text(
                        assigneeRole,
                        style: const TextStyle(
                          fontSize: 9,
                          color: AppColors.textMuted,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2.5),
                decoration: BoxDecoration(
                  color: slaBg,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    Icon(
                      Icons.hourglass_bottom,
                      size: 12,
                      color: slaTextCol,
                    ),
                    const SizedBox(width: 3),
                    Text(
                      slaCountdown,
                      style: TextStyle(
                        color: slaTextCol,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                        fontFamily: 'JetBrains Mono',
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: AppSpacing.sm),
          if (fullWidthActionText != null)
            SizedBox(
              width: double.infinity,
              height: 36,
              child: ElevatedButton.icon(
                onPressed: onFullWidthAction,
                icon: Icon(fullWidthActionIcon ?? Icons.add, size: 16),
                label: Text(
                  fullWidthActionText,
                  style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFF2F3FF),
                  foregroundColor: AppColors.textPrimary,
                  elevation: 0,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
              ),
            )
          else
            Row(
              children: [
                if (secondaryActionText != null)
                  Expanded(
                    flex: 4,
                    child: SizedBox(
                      height: 36,
                      child: OutlinedButton.icon(
                        onPressed: onSecondaryAction,
                        icon: secondaryIcon != null ? Icon(secondaryIcon, size: 14) : null,
                        label: Text(
                          secondaryActionText,
                          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
                        ),
                        style: OutlinedButton.styleFrom(
                          backgroundColor: const Color(0xFFF2F3FF),
                          foregroundColor: AppColors.textSecondary,
                          side: BorderSide.none,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                          padding: const EdgeInsets.symmetric(horizontal: 8),
                        ),
                      ),
                    ),
                  ),
                if (secondaryActionText != null && primaryActionText != null)
                  const SizedBox(width: AppSpacing.sm),
                if (primaryActionText != null)
                  Expanded(
                    flex: 6,
                    child: SizedBox(
                      height: 36,
                      child: ElevatedButton.icon(
                        onPressed: onPrimaryAction,
                        icon: primaryIcon != null ? Icon(primaryIcon, size: 14) : null,
                        label: Text(
                          primaryActionText,
                          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Colors.white),
                        ),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF4648D4),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                          elevation: 0,
                          padding: const EdgeInsets.symmetric(horizontal: 8),
                        ),
                      ),
                    ),
                  ),
              ],
            ),
        ],
      ),
    );
  }

  Widget _buildWarRoomBridgeBanner() {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: const Color(0xFF283044),
        borderRadius: BorderRadius.circular(16),
        boxShadow: const [
          BoxShadow(
            color: Color(0x20000000),
            blurRadius: 10,
            offset: Offset(0, 4),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.between,
        children: [
          Row(
            children: [
              Container(
                width: 36,
                height: 36,
                decoration: BoxDecoration(
                  color: const Color(0xFFE11D48).withOpacity(0.25),
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.mic, color: Color(0xFFFFE4E6), size: 18),
              ),
              const SizedBox(width: AppSpacing.sm),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        'INCIDENT WAR ROOM',
                        style: AppTypography.codeSmall(context).copyWith(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                          letterSpacing: 0.8,
                          fontSize: 11,
                        ),
                      ),
                      const SizedBox(width: 6),
                      Container(
                        width: 6,
                        height: 6,
                        decoration: const BoxDecoration(
                          color: Color(0xFF0D9488),
                          shape: BoxShape.circle,
                        ),
                      ),
                    ],
                  ),
                  const Text(
                    '3 operators active on bridge',
                    style: TextStyle(color: Color(0xFFDAE2FD), fontSize: 10),
                  ),
                ],
              ),
            ],
          ),
          ElevatedButton.icon(
            onPressed: () {
              _showFeedbackToast('Connecting to live audio bridge', Icons.headset_mic, AppColors.primary);
            },
            icon: const Icon(Icons.headset_mic, size: 14, color: Colors.white),
            label: const Text('Join Audio', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w600, color: Colors.white)),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF4648D4),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              elevation: 0,
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
        boxShadow: const [
          BoxShadow(
            color: Color(0x05000000),
            blurRadius: 10,
            offset: Offset(0, -2),
          ),
        ],
      ),
      child: BottomNavigationBar(
        currentIndex: _currentNavIndex,
        onTap: (index) {
          setState(() {
            _currentNavIndex = index;
          });
          if (index == 0) {
            // Stay on triage
          } else if (index == 1) {
            context.push('/cases/NEX-2026-0104/track');
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
          BottomNavigationBarItem(
            icon: Icon(Icons.inbox_outlined),
            activeIcon: Icon(Icons.inbox),
            label: 'Triage',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.dataset_outlined),
            activeIcon: Icon(Icons.dataset),
            label: 'Studio',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.radar_outlined),
            activeIcon: Icon(Icons.radar),
            label: 'Radar',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.group_outlined),
            activeIcon: Icon(Icons.group),
            label: 'Lead',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.admin_panel_settings_outlined),
            activeIcon: Icon(Icons.admin_panel_settings),
            label: 'Portal',
          ),
        ],
      ),
    );
  }
}
