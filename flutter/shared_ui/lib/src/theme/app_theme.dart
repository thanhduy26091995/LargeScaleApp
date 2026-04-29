import 'package:flutter/material.dart';

/// App-wide Material 3 theme.
class AppTheme {
  static const _seedColor = Color(0xFF1A73E8);

  static ThemeData get light => ThemeData(
        useMaterial3: true,
        colorSchemeSeed: _seedColor,
        brightness: Brightness.light,
      );

  static ThemeData get dark => ThemeData(
        useMaterial3: true,
        colorSchemeSeed: _seedColor,
        brightness: Brightness.dark,
      );
}
