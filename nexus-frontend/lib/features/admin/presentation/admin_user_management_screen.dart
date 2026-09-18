import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_typography.dart';
import '../../../core/widgets/nexus_button.dart';
import '../../../core/widgets/responsive_layout.dart';
import '../../../core/widgets/status_badge.dart';

/// SCR-15: Admin Configuration & User Governance Screen
/// User directory management, RBAC clearance assignments, department team rosters,
/// shift scheduling, and enterprise directory sync (SSO/SCIM).
class AdminUserManagementScreen extends ConsumerStatefulWidget {
  const AdminUserManagementScreen({super.key});

  @override
  ConsumerState<AdminUserManagementScreen> createState() => _AdminUserManagementScreenState();
}

class _AdminUserManagementScreenState extends ConsumerState<AdminUserManagementScreen> {
  final TextEditingController _searchController = TextEditingController();
  String _selectedTab = 'Personnel'; // Personnel, Teams, Routing, SSO
  int _currentNavIndex = 4; // Admin active

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
                colors: [Color(0xFF4648D4), Color(0xFF6366F1)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.admin_panel_settings_outlined, color: Colors.white, size: 18),
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
                'User Governance',
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
          icon: const Icon(Icons.policy_outlined, color: AppColors.accentPrimary, size: 22),
          tooltip: 'SLA Policy Builder',
          onPressed: () => context.go('/admin/policies'),
        ),
        IconButton(
          icon: const Icon(Icons.history_edu_outlined, color: AppColors.textSecondary, size: 22),
          tooltip: 'Audit Trail',
          onPressed: () => context.go('/admin/audit-logs'),
        ),
        Padding(
          padding: const EdgeInsets.only(right: AppSpacing.md),
          child: CircleAvatar(
            radius: 16,
            backgroundColor: const Color(0xFFEEF2FF),
            child: const Text(
              'AD',
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
          _buildAdminHeaderActionRow(context),
          const SizedBox(height: AppSpacing.sm),
          _buildSubTabPills(context),
          const SizedBox(height: AppSpacing.sm),
          _buildGovernanceKpiGrid(context),
          const SizedBox(height: AppSpacing.sm),
          _buildSearchAndFilters(context),
          const SizedBox(height: AppSpacing.sm),
          _buildUserRosterList(context),
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
          _buildAdminHeaderActionRow(context),
          const SizedBox(height: AppSpacing.md),
          _buildSubTabPills(context),
          const SizedBox(height: AppSpacing.md),
          _buildGovernanceKpiGrid(context),
          const SizedBox(height: AppSpacing.md),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                flex: 4,
                child: _buildSearchAndFilters(context),
              ),
              const SizedBox(width: AppSpacing.xl),
              Expanded(
                flex: 8,
                child: _buildUserRosterList(context),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildAdminHeaderActionRow(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Directory & Access',
              style: AppTypography.titleMedium(context).copyWith(fontWeight: FontWeight.bold),
            ),
            const Text(
              'SOC-2 / RBAC Role Governance',
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
          icon: const Icon(Icons.person_add_outlined, size: 14),
          label: const Text('+ Invite User', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
          onPressed: () => _showFeedbackToast('Opening User Invitation Modal', Icons.person_add, AppColors.accentPrimary),
        ),
      ],
    );
  }

  Widget _buildSubTabPills(BuildContext context) {
    final tabs = [
      {'id': 'Personnel', 'label': 'Personnel (48)'},
      {'id': 'Teams', 'label': 'Teams (6)'},
      {'id': 'Routing', 'label': 'Routing Rules'},
      {'id': 'SSO', 'label': 'SSO & SCIM (Active)'},
    ];

    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: tabs.map((t) {
          final isSelected = _selectedTab == t['id'];
          return Padding(
            padding: const EdgeInsets.only(right: 6),
            child: ChoiceChip(
              label: Text(
                t['label']!,
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
                  setState(() => _selectedTab = t['id']!);
                }
              },
            ),
          );
        }).toList(),
      ),
    );
  }

  Widget _buildGovernanceKpiGrid(BuildContext context) {
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
            _buildKpiCard(
              title: 'TOTAL USERS',
              value: '48',
              badgeText: 'All Active',
              badgeColor: const Color(0xFF0D9488),
              badgeBg: const Color(0xFFCCFBF1),
              icon: Icons.group_outlined,
            ),
            _buildKpiCard(
              title: 'ON SHIFT',
              value: '14',
              badgeText: '78% Pool Load',
              badgeColor: const Color(0xFF0284C7),
              badgeBg: const Color(0xFFE0F2FE),
              icon: Icons.timelapse_outlined,
            ),
            _buildKpiCard(
              title: 'PENDING APPROVALS',
              value: '2',
              badgeText: 'Awaiting Lead',
              badgeColor: const Color(0xFFD97706),
              badgeBg: const Color(0xFFFEF3C7),
              icon: Icons.hourglass_top_outlined,
            ),
            _buildKpiCard(
              title: 'MFA STATUS',
              value: '100%',
              badgeText: 'SOC-2 Compliant',
              badgeColor: const Color(0xFF6366F1),
              badgeBg: const Color(0xFFEEF2FF),
              icon: Icons.verified_user_outlined,
            ),
          ],
        );
      },
    );
  }

  Widget _buildKpiCard({
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
                fontWeight: FontWeight.w600,
                color: badgeColor,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSearchAndFilters(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.sm + 2),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.borderLight),
      ),
      child: Column(
        children: [
          TextField(
            controller: _searchController,
            decoration: InputDecoration(
              hintText: 'Filter by name, pod, role...',
              hintStyle: const TextStyle(fontSize: 12, color: AppColors.textMuted),
              prefixIcon: const Icon(Icons.search, size: 18, color: AppColors.textMuted),
              suffixIcon: Container(
                margin: const EdgeInsets.all(8),
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: const Color(0xFFF1F5F9),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text('⌘K', style: TextStyle(fontSize: 10, color: AppColors.textSecondary)),
              ),
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

  Widget _buildUserRosterList(BuildContext context) {
    return Column(
      children: [
        _buildUserCard(
          name: 'Elena Vance',
          email: 'elena.vance@nexus.internal',
          role: 'LEAD OPERATOR',
          team: 'SecOps & Core SRE',
          status: 'Online • Shift Active',
          activeCases: 3,
          maxCases: 6,
          roleBg: const Color(0xFFF3E8FF),
          roleColor: const Color(0xFF7C3AED),
          isOnline: true,
        ),
        const SizedBox(height: AppSpacing.sm),
        _buildUserCard(
          name: 'David Ross',
          email: 'david.ross@nexus.internal',
          role: 'CASE OPERATOR',
          team: 'Core SRE & DevOps',
          status: 'Online • High Load',
          activeCases: 8,
          maxCases: 8,
          roleBg: const Color(0xFFEEF2FF),
          roleColor: AppColors.accentPrimary,
          isOnline: true,
        ),
        const SizedBox(height: AppSpacing.sm),
        _buildUserCard(
          name: 'Sarah Jenkins',
          email: 'sarah.jenkins@nexus.internal',
          role: 'CASE OPERATOR',
          team: 'Identity & Access',
          status: 'Online • Available',
          activeCases: 2,
          maxCases: 6,
          roleBg: const Color(0xFFEEF2FF),
          roleColor: AppColors.accentPrimary,
          isOnline: true,
        ),
        const SizedBox(height: AppSpacing.sm),
        _buildUserCard(
          name: 'Marcus Brody',
          email: 'marcus.brody@nexus.internal',
          role: 'ADMINISTRATOR',
          team: 'Platform Operations',
          status: 'Standby',
          activeCases: 1,
          maxCases: 4,
          roleBg: const Color(0xFFFFE4E6),
          roleColor: const Color(0xFFE11D48),
          isOnline: false,
        ),
      ],
    );
  }

  Widget _buildUserCard({
    required String name,
    required String email,
    required String role,
    required String team,
    required String status,
    required int activeCases,
    required int maxCases,
    required Color roleBg,
    required Color roleColor,
    required bool isOnline,
  }) {
    final progress = (activeCases / maxCases).clamp(0.0, 1.0);

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
                  CircleAvatar(
                    radius: 16,
                    backgroundColor: isOnline ? const Color(0xFFCCFBF1) : const Color(0xFFF1F5F9),
                    child: Text(
                      name.split(' ').map((e) => e[0]).take(2).join(),
                      style: TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.bold,
                        color: isOnline ? const Color(0xFF0D9488) : AppColors.textSecondary,
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: AppColors.textPrimary)),
                      Text(email, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
                    ],
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: roleBg,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  role,
                  style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: roleColor),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text('Team: $team', style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
              Text(status, style: TextStyle(fontSize: 11, fontWeight: FontWeight.w600, color: isOnline ? const Color(0xFF0D9488) : AppColors.textMuted)),
            ],
          ),
          const SizedBox(height: 6),
          ClipRRect(
            borderRadius: BorderRadius.circular(3),
            child: LinearProgressIndicator(
              value: progress,
              backgroundColor: const Color(0xFFE2E8F0),
              valueColor: AlwaysStoppedAnimation<Color>(progress > 0.85 ? const Color(0xFFE11D48) : AppColors.accentPrimary),
              minHeight: 4,
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
            context.go('/admin/policies');
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
          BottomNavigationBarItem(icon: Icon(Icons.admin_panel_settings_outlined), label: 'Users'),
        ],
      ),
    );
  }
}
