import 'package:flutter/widgets.dart';
import 'role.dart';

/// A UI widget contributed by a module for dynamic slot composition.
///
/// Mirrors Android [UISlot]. Modules return a list of [UISlot] from
/// [AppModule.provideWidgets]. Slot host screens render all slots registered
/// for their [slotId], filtered by [requiredRole], sorted by descending [priority].
///
/// Example:
/// ```dart
/// UISlot(
///   slotId: SlotIds.homeWidgets,
///   widgetId: 'orders_summary',
///   moduleId: 'orders',
///   priority: 80,
///   requiredRole: Role.STAFF,
///   content: (ctx) => const OrdersSummaryWidget(),
/// )
/// ```
class UISlot {
  /// The slot host identifier (use [SlotIds] constants).
  final String slotId;

  /// Unique identifier for this widget (used by [SlotRegistry] for dedup/removal).
  final String widgetId;

  /// ID of the module contributing this widget (used by [SlotRegistry.clearModule]).
  final String moduleId;

  /// Display order — higher priority widgets render first. Default 100.
  final int priority;

  /// Minimum role required to see this widget.
  final Role requiredRole;

  /// Widget factory. Receives [BuildContext] from the slot host.
  final Widget Function(BuildContext context) content;

  const UISlot({
    required this.slotId,
    required this.widgetId,
    required this.moduleId,
    this.priority = 100,
    this.requiredRole = Role.GUEST,
    required this.content,
  });
}
