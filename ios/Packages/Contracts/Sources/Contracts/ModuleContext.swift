import Combine
import Foundation

/// Forward declarations — concrete implementations are in WireCore.
public protocol EventBusProtocol: AnyObject {
    func publish<T: ModuleEvent>(_ event: T)
}

public protocol SlotRegistryProtocol: AnyObject {
    func register(_ slot: UISlot)
    func getSlotsForHost(slotId: String, userRole: Role) -> [UISlot]
    func unregister(widgetId: String)
    func clearModule(moduleId: String)
}

/// Context provided to each module during `AppModule.initialize(context:)`.
///
/// Acts as the module's gateway to all Wire Core services.
/// Mirrors Android `ModuleContext` and Flutter `ModuleContext`.
/// Modules must NOT hold a strong reference to this beyond their lifecycle.
public protocol ModuleContext: AnyObject {
    /// Publisher delivering tenant configuration changes.
    var tenantConfig: AnyPublisher<TenantConfig?, Never> { get }

    /// Publisher delivering current user role changes.
    var currentRole: AnyPublisher<Role, Never> { get }

    /// Cross-module event bus.
    var eventBus: any EventBusProtocol { get }

    /// UI slot registry.
    var slotRegistry: any SlotRegistryProtocol { get }

    /// Navigate to a route owned by any module.
    func navigate(route: String)
}
