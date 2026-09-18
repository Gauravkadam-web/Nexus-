import 'package:flutter/material.dart';
import '../theme/app_colors.dart';
import '../theme/app_spacing.dart';
import '../theme/app_typography.dart';

class AiSuggestionCard extends StatelessWidget {
  final String title;
  final String description;
  final String? confidenceScore;
  final VoidCallback? onAccept;
  final VoidCallback? onModify;
  final VoidCallback? onReject;
  final bool isPending;

  const AiSuggestionCard({
    super.key,
    required this.title,
    required this.description,
    this.confidenceScore,
    this.onAccept,
    this.onModify,
    this.onReject,
    this.isPending = true,
  });

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Container(
      decoration: BoxDecoration(
        color: isDark ? AppColors.aiBgDark : AppColors.aiBgLight,
        borderRadius: BorderRadius.circular(AppSpacing.radiusLg),
        border: Border.all(color: AppColors.aiBorder, width: 1),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Subtle top gradient line
          Container(
            height: 3,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  AppColors.aiLilac,
                  isDark ? AppColors.accentPrimaryDark : AppColors.accentPrimary,
                  AppColors.aiLilac,
                ],
              ),
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(AppSpacing.radiusLg),
                topRight: Radius.circular(AppSpacing.radiusLg),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(AppSpacing.cardPaddingMobile),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Header badge
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Icon(
                          Icons.psychology_outlined,
                          size: 18,
                          color: isDark ? AppColors.aiLilacDark : AppColors.aiLilac,
                        ),
                        const SizedBox(width: 6),
                        Text(
                          '✨ AI SUGGESTION',
                          style: TextStyle(
                            color: isDark ? AppColors.aiLilacDark : AppColors.aiLilac,
                            fontSize: 11,
                            fontWeight: FontWeight.w700,
                            letterSpacing: 0.04,
                          ),
                        ),
                      ],
                    ),
                    if (isPending)
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                        decoration: BoxDecoration(
                          color: isDark ? AppColors.statusWaitingBgDark : AppColors.statusWaitingBgLight,
                          borderRadius: BorderRadius.circular(AppSpacing.radiusFull),
                        ),
                        child: Text(
                          'PENDING REVIEW',
                          style: TextStyle(
                            color: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
                            fontSize: 10,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                      ),
                  ],
                ),
                const SizedBox(height: AppSpacing.sm),
                // Title & confidence
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: Text(
                        title,
                        style: AppTypography.titleSmall(isDark),
                      ),
                    ),
                    if (confidenceScore != null) ...[
                      const SizedBox(width: AppSpacing.sm),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: isDark ? AppColors.statusClosedBgDark : AppColors.statusClosedBgLight,
                          borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                        ),
                        child: Text(
                          '$confidenceScore Conf.',
                          style: TextStyle(
                            color: isDark ? AppColors.statusClosedTextDark : AppColors.statusClosedTextLight,
                            fontSize: 11,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ],
                  ],
                ),
                const SizedBox(height: AppSpacing.xs),
                Text(
                  description,
                  style: AppTypography.bodySmall(isDark),
                ),
                if (isPending) ...[
                  const SizedBox(height: AppSpacing.md),
                  // Ghost Action Buttons Container
                  Container(
                    padding: const EdgeInsets.all(3),
                    decoration: BoxDecoration(
                      color: isDark ? AppColors.darkSurface : AppColors.lightSurface,
                      borderRadius: BorderRadius.circular(AppSpacing.radiusMd),
                      border: Border.all(color: isDark ? AppColors.darkBorder : AppColors.lightBorder),
                    ),
                    child: Row(
                      children: [
                        Expanded(
                          child: InkWell(
                            onTap: onAccept,
                            borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(vertical: 6),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Icon(
                                    Icons.check,
                                    size: 14,
                                    color: isDark ? AppColors.statusClosedTextDark : AppColors.statusClosedTextLight,
                                  ),
                                  const SizedBox(width: 4),
                                  Text(
                                    'Accept',
                                    style: TextStyle(
                                      color: isDark ? AppColors.statusClosedTextDark : AppColors.statusClosedTextLight,
                                      fontSize: 12,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                        Container(
                          width: 1,
                          height: 16,
                          color: isDark ? AppColors.darkBorder : AppColors.lightBorder,
                        ),
                        Expanded(
                          child: InkWell(
                            onTap: onModify,
                            borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(vertical: 6),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Icon(
                                    Icons.edit_outlined,
                                    size: 14,
                                    color: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
                                  ),
                                  const SizedBox(width: 4),
                                  Text(
                                    'Modify',
                                    style: TextStyle(
                                      color: isDark ? AppColors.statusWaitingTextDark : AppColors.statusWaitingTextLight,
                                      fontSize: 12,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                        Container(
                          width: 1,
                          height: 16,
                          color: isDark ? AppColors.darkBorder : AppColors.lightBorder,
                        ),
                        Expanded(
                          child: InkWell(
                            onTap: onReject,
                            borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(vertical: 6),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Icon(
                                    Icons.close,
                                    size: 14,
                                    color: isDark ? AppColors.statusBreachedTextDark : AppColors.statusBreachedTextLight,
                                  ),
                                  const SizedBox(width: 4),
                                  Text(
                                    'Dismiss',
                                    style: TextStyle(
                                      color: isDark ? AppColors.statusBreachedTextDark : AppColors.statusBreachedTextLight,
                                      fontSize: 12,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}
