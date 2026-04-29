import 'package:flutter/widgets.dart';

/// Base interface for injectable UI widgets (slot injection pattern).
abstract class AppWidget {
  /// The slot identifier this widget targets.
  String get slotId;

  /// Priority — higher priority widgets render first.
  int get priority;

  /// Build the injectable widget.
  Widget build(BuildContext context);
}
