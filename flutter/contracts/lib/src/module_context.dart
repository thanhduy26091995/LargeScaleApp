import 'role.dart';
import 'tenant_config.dart';
import 'module_event.dart';
import 'ui_slot.dart';

/// Abstract event bus interface (implemented by `AppEventBus` in the wire package).
///
/// Mirrors Android `EventBus` interface in contracts.
abstract class EventBus {
  /// Publish a [ModuleEvent] to all active subscribers of that type.
  void publish(ModuleEvent event);

  /// Returns a stream filtered to events of type [T].
  Stream<T> on<T extends ModuleEvent>();
}

/// Abstract slot registry interface (implemented by `WireSlotRegistry` in the wire package).
///
/// Mirrors Android `SlotRegistry` interface in contracts.
abstract class SlotRegistryBase {
  /// Register a [UISlot]. Replaces any existing slot with the same widgetId.
  void register(UISlot slot);

  /// Returns all slots for [slotId] visible to [userRole], sorted by descending priority.
  List<UISlot> getSlotsForHost(String slotId, Role userRole);

  /// Remove a specific widget by [widgetId].
  void unregister(String widgetId);

  /// Remove all widgets contributed by [moduleId]. Call in [AppModule.onDestroy].
  void clearModule(String moduleId);
}

/// Context provided to each module during [AppModule.initialize].
///
/// Acts as the module's gateway to all Wire Core services.
/// Mirrors Android [ModuleContext] — modules must NOT store this beyond their lifecycle.
abstract class ModuleContext {
  /// Reactive stream of the current tenant configuration.
  Stream<TenantConfig?> get tenantConfig;

  /// Reactive stream of the current user role.
  Stream<Role> get currentRole;

  /// Cross-module event bus.
  EventBus get eventBus;

  /// UI slot registry.
  SlotRegistryBase get slotRegistry;

  /// Navigate to a route owned by any module.
  void navigate(String route);
}

