import Combine
import Contracts

/// Concrete implementation of `ModuleContext`.
///
/// Provided to each module during `AppModule.initialize(context:)`. Exposes
/// Wire Core services without leaking DI internals.
public final class ModuleContextImpl: ModuleContext {
    private let _eventBus: AppEventBus
    private let _slotRegistry: SlotRegistry
    private let _tenantPublisher: AnyPublisher<TenantConfig?, Never>
    private let _rolePublisher: AnyPublisher<Role, Never>
    private let _navigateFn: (String) -> Void

    public init(
        eventBus: AppEventBus,
        slotRegistry: SlotRegistry,
        tenantPublisher: AnyPublisher<TenantConfig?, Never>,
        rolePublisher: AnyPublisher<Role, Never>,
        navigateFn: @escaping (String) -> Void
    ) {
        _eventBus = eventBus
        _slotRegistry = slotRegistry
        _tenantPublisher = tenantPublisher
        _rolePublisher = rolePublisher
        _navigateFn = navigateFn
    }

    public var tenantConfig: AnyPublisher<TenantConfig?, Never> { _tenantPublisher }
    public var currentRole: AnyPublisher<Role, Never> { _rolePublisher }
    public var eventBus: any EventBusProtocol { _eventBus }
    public var slotRegistry: any SlotRegistryProtocol { _slotRegistry }

    public func navigate(route: String) {
        _navigateFn(route)
    }
}
