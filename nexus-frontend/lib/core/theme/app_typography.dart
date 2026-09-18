import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'app_colors.dart';

class AppTypography {
  AppTypography._();

  // Headings (Outfit)
  static TextStyle displayLarge(bool isDark) => GoogleFonts.outfit(
        fontSize: 32,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.02,
        height: 1.25,
        color: isDark ? AppColors.darkTextPrimary : AppColors.lightTextPrimary,
      );

  static TextStyle headlineMedium(bool isDark) => GoogleFonts.outfit(
        fontSize: 22,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.015,
        height: 1.3,
        color: isDark ? AppColors.darkTextPrimary : AppColors.lightTextPrimary,
      );

  static TextStyle headlineSmall(bool isDark) => GoogleFonts.outfit(
        fontSize: 18,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.01,
        height: 1.35,
        color: isDark ? AppColors.darkTextPrimary : AppColors.lightTextPrimary,
      );

  // Body & Titles (Inter)
  static TextStyle titleMedium(bool isDark) => GoogleFonts.inter(
        fontSize: 16,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.01,
        color: isDark ? AppColors.darkTextPrimary : AppColors.lightTextPrimary,
      );

  static TextStyle titleSmall(bool isDark) => GoogleFonts.inter(
        fontSize: 14,
        fontWeight: FontWeight.w600,
        letterSpacing: 0,
        color: isDark ? AppColors.darkTextPrimary : AppColors.lightTextPrimary,
      );

  static TextStyle bodyMedium(bool isDark) => GoogleFonts.inter(
        fontSize: 14,
        fontWeight: FontWeight.w400,
        letterSpacing: 0,
        height: 1.55,
        color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
      );

  static TextStyle bodySmall(bool isDark) => GoogleFonts.inter(
        fontSize: 13,
        fontWeight: FontWeight.w400,
        letterSpacing: 0.005,
        height: 1.45,
        color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
      );

  static TextStyle labelMedium(bool isDark) => GoogleFonts.inter(
        fontSize: 12,
        fontWeight: FontWeight.w500,
        letterSpacing: 0.02,
        color: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted,
      );

  static TextStyle labelSmall(bool isDark) => GoogleFonts.inter(
        fontSize: 11,
        fontWeight: FontWeight.w600,
        letterSpacing: 0.04,
        color: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted,
      );

  // Code & Monospace (JetBrains Mono)
  static TextStyle codeSmall(bool isDark) => GoogleFonts.jetbrainsMono(
        fontSize: 12,
        fontWeight: FontWeight.w500,
        letterSpacing: 0,
        color: isDark ? AppColors.darkTextPrimary : AppColors.lightTextPrimary,
      );
}
