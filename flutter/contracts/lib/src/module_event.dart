/// Base class for all cross-module events.
///
/// All events must extend [ModuleEvent] for type-safe subscription.
/// Mirrors Android sealed class [ModuleEvent].
///
/// Usage:
/// ```dart
/// context.eventBus.on<OrderCreatedEvent>((e) => refresh());
/// context.eventBus.publish(OrderCreatedEvent(orderId: '123', moduleId: 'orders'));
/// ```
abstract class ModuleEvent {
  final DateTime timestamp;
  ModuleEvent() : timestamp = DateTime.now();
}

// ── Auth events ─────────────────────────────────────────────────────────────

class UserAuthenticatedEvent extends ModuleEvent {
  final String userId;
  final String role; // Role enum value name
  UserAuthenticatedEvent({required this.userId, required this.role});
}

class UserLoggedOutEvent extends ModuleEvent {}

// ── Module lifecycle events ──────────────────────────────────────────────────

class ModuleInitializedEvent extends ModuleEvent {
  final String moduleId;
  ModuleInitializedEvent({required this.moduleId});
}

// ── Domain events ────────────────────────────────────────────────────────────

class OrderCreatedEvent extends ModuleEvent {
  final String orderId;
  final String moduleId;
  OrderCreatedEvent({required this.orderId, required this.moduleId});
}

// ── Tenant events ────────────────────────────────────────────────────────────

class TenantSwitchedEvent extends ModuleEvent {
  final String oldTenantId;
  final String newTenantId;
  TenantSwitchedEvent({required this.oldTenantId, required this.newTenantId});
}

class TenantConfigUpdatedEvent extends ModuleEvent {
  final String tenantId;
  TenantConfigUpdatedEvent({required this.tenantId});
}

// ── Feature flag events ──────────────────────────────────────────────────────

class FeatureFlagChangedEvent extends ModuleEvent {
  final String key;
  final bool enabled;
  FeatureFlagChangedEvent({required this.key, required this.enabled});
}

// ── Navigation events ────────────────────────────────────────────────────────

class NavigationRequestedEvent extends ModuleEvent {
  final String route;
  final Map<String, String> args;
  NavigationRequestedEvent({required this.route, this.args = const {}});
}
