import 'package:flutter/widgets.dart';
import 'package:contracts/contracts.dart';

/// Concrete implementation of [SlotRegistryBase].
///
/// Feature modules contribute [UISlot]s during [AppModule.initialize]
/// (via [AppModule.provideWidgets]). Host screens call [getSlotsForHost]
/// to render all widgets registered for their slot.
///
/// Mirrors Android [SlotRegistry] implementation.
class WireSlotRegistry implements SlotRegistryBase {
  final Map<String, List<UISlot>> _slots = {};

  /// Register a [UISlot]. If a widget with the same [UISlot.widgetId] already
  /// exists it is replaced.
  @override
  void register(UISlot slot) {
    final existing = _slots.putIfAbsent(slot.slotId, () => []);
    existing.removeWhere((s) => s.widgetId == slot.widgetId);
    existing.add(slot);
    existing.sort((a, b) => b.priority.compareTo(a.priority));
  }

  /// Returns all widgets for [slotId] visible to [userRole],
  /// sorted by descending priority.
  @override
  List<UISlot> getSlotsForHost(String slotId, Role userRole) {
    return (_slots[slotId] ?? [])
        .where((s) => _roleAllowed(userRole, s.requiredRole))
        .toList();
  }

  /// Builds and returns [Widget]s for [slotId] visible to [userRole].
  List<Widget> buildSlotsForHost(
      BuildContext context, String slotId, Role userRole) {
    return getSlotsForHost(slotId, userRole)
        .map((s) => s.content(context))
        .toList();
  }

  /// Remove a specific widget by [widgetId].
  @override
  void unregister(String widgetId) {
    for (final slots in _slots.values) {
      slots.removeWhere((s) => s.widgetId == widgetId);
    }
  }

  /// Remove all widgets contributed by [moduleId]. Call in [AppModule.onDestroy].
  @override
  void clearModule(String moduleId) {
    for (final slots in _slots.values) {
      slots.removeWhere((s) => s.moduleId == moduleId);
    }
  }

  bool _roleAllowed(Role userRole, Role requiredRole) {
    const order = [Role.GUEST, Role.CUSTOMER, Role.STAFF, Role.ADMIN];
    return order.indexOf(userRole) >= order.indexOf(requiredRole);
  }
}


