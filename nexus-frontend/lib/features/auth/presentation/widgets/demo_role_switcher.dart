import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_spacing.dart';
import '../auth_state_provider.dart';

class DemoRoleSwitcher extends ConsumerWidget {
  final ValueChanged<String>? onRoleSelected;

  const DemoRoleSwitcher({super.key, this.onRoleSelected});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final currentRole = ref.watch(authStateProvider).user?.primaryRole;

    final roles = [
      ('Requester', 'REQUESTER'),
      ('Operator', 'CASE_OPERATOR'),
      ('Team Lead', 'TEAM_LEAD'),
      ('Manager', 'MANAGER'),
      ('Admin', 'ADMINISTRATOR'),
    ];

    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: isDark ? AppColors.darkSurfaceElevated : AppColors.lightSurfaceElevated,
        borderRadius: BorderRadius.circular(AppSpacing.radiusMd),
        border: Border.all(color: isDark ? AppColors.darkBorder : AppColors.lightBorder),
      ),
      child: Wrap(
        spacing: 4,
        runSpacing: 4,
        alignment: WrapAlignment.center,
        children: roles.map((role) {
          final isSelected = currentRole == role.$2;
          return InkWell(
            onTap: () {
              ref.read(authStateProvider.notifier).loginAsDemoRole(role.$2);
              onRoleSelected?.call(role.$2);
            },
            borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              decoration: BoxDecoration(
                color: isSelected
                    ? (isDark ? AppColors.accentTintDark : AppColors.accentTintLight)
                    : Colors.transparent,
                borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                border: isSelected
                    ? Border.all(
                        color: isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary,
                        width: 1,
                      )
                    : Border.all(color: Colors.transparent),
              ),
              child: Text(
                role.$1,
                style: TextStyle(
                  color: isSelected
                      ? (isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary)
                      : (isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary),
                  fontSize: 12,
                  fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
                ),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}
