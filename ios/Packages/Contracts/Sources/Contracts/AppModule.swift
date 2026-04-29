import Foundation

/// Contract that all feature modules must conform to.
///
/// Mirrors Android `AppModule` interface and Flutter `AppModule` abstract class:
///   - `metadata` describes the module (id, roles, tenant filter, priority)
///   - `initialize(context:)` is called with a `ModuleContext` for lifecycle setup
///   - `onDestroy()` allows clean teardown (cancel Combine subscriptions, clear slots)
///   - `provideRoutes()` declares navigation routes contributed by this module
///   - `provideWidgets()` declares UI slots contributed by this module
///
/// Example:
/// ```swift
/// final class OrdersModule: AppModule {
///     let metadata = ModuleMetadata(
///         id: "orders", name: "Orders",
///         requiredRoles: [.ADMIN, .STAFF]
///     )
///
///     func initialize(context: any ModuleContext) {
///         context.eventBus.on(UserAuthenticatedEvent.self)
///             .sink { [weak self] e in self?.loadData(for: e.role) }
///             .store(in: &cancellables)
///     }
/// }
/// ```
public protocol AppModule: AnyObject {
    /// Metadata describing this module: id, name, roles, priority, etc.
    var metadata: ModuleMetadata { get }

    /// Called once when the module is activated for the current role/tenant.
    /// Use `context` to subscribe to events, register widgets, and access services.
    func initialize(context: any ModuleContext)

    /// Called when the module is deactivated (role/tenant change or app termination).
    /// Cancel Combine subscriptions and clear slot registrations here.
    func onDestroy()

    /// Navigation routes this module contributes to the app's navigation graph.
    func provideRoutes() -> [ModuleRoute]

    /// UI slots / widgets this module contributes to shared slot hosts.
    func provideWidgets() -> [UISlot]
}

// Default implementations
public extension AppModule {
    func onDestroy() {}
    func provideRoutes() -> [ModuleRoute] { [] }
    func provideWidgets() -> [UISlot] { [] }
}

