import 'module_metadata.dart';
import 'module_context.dart';
import 'module_route.dart';
import 'ui_slot.dart';

/// Base class for all feature modules.
///
/// Mirrors the Android [AppModule] contract exactly:
///   - [metadata] describes the module (id, roles, tenant filter, priority)
///   - [initialize] is called with a [ModuleContext] giving access to Wire Core services
///   - [onDestroy] allows clean teardown (cancel event subscriptions, clear slots)
///   - [provideRoutes] declares navigation routes contributed by this module
///   - [provideWidgets] declares UI slots / widgets contributed by this module
///
/// Example:
/// ```dart
/// class OrdersModule extends AppModule {
///   @override
///   ModuleMetadata get metadata => const ModuleMetadata(
///     id: 'orders',
///     name: 'Orders',
///     requiredRoles: {Role.ADMIN, Role.STAFF},
///   );
///
///   @override
///   void initialize(ModuleContext context) {
///     context.eventBus.on<UserAuthenticatedEvent>((e) { /* ... */ });
///   }
/// }
/// ```
abstract class AppModule {
  /// Metadata describing this module: id, name, roles, priority, etc.
  ModuleMetadata get metadata;

  /// Called once when the module is activated for the current role/tenant.
  /// Use [context] to subscribe to events, register widgets, and access services.
  void initialize(ModuleContext context);

  /// Called when the module is deactivated (role/tenant change or app destroy).
  /// Cancel event subscriptions and clear slot registrations here.
  void onDestroy() {}

  /// Navigation routes this module contributes to the app's navigation graph.
  List<ModuleRoute> provideRoutes() => [];

  /// UI slots / widgets this module contributes to shared slot hosts.
  List<UISlot> provideWidgets() => [];
}

