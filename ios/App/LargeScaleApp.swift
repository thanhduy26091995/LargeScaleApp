import Combine
import SwiftUI
import WireCore
import Contracts
import Core
import FeatureDashboard
import FeatureOrders
import FeatureInventory
import FeatureWallet

@main
struct LargeScaleApp: App {
    // Wire Core infrastructure
    private let container = WireContainer()
    private let eventBus = AppEventBus()
    private let slotRegistry = SlotRegistry()

    // Reactive streams driving role/tenant changes
    private let roleSubject = CurrentValueSubject<Role, Never>(.GUEST)
    private let tenantSubject = CurrentValueSubject<TenantConfig?, Never>(nil)

    init() {
        // 1. Register core services
        container.register(StubAuthService() as (any AuthService))
        container.register(StubNetworkService() as (any NetworkService))
        container.register(StubStorageService() as (any StorageService))

        // 2. Seed role from AuthService
        let auth: any AuthService = container.resolve()
        roleSubject.send(auth.currentRole)

        // 3. Build ModuleContext — passed to every module during initialize
        let moduleContext = ModuleContextImpl(
            eventBus: eventBus,
            slotRegistry: slotRegistry,
            tenantPublisher: tenantSubject.eraseToAnyPublisher(),
            rolePublisher: roleSubject.eraseToAnyPublisher(),
            navigateFn: { _ in
                // Navigation handled by ContentView's NavigationStack
            }
        )

        // 4. Register all feature modules
        let registry = ModuleRegistry()
        registry.register(DashboardModule())
        registry.register(OrdersModule())
        registry.register(InventoryModule())
        registry.register(WalletModule())

        // 5. Resolve active modules for the current role + tenant
        let activeModules = registry.resolve(
            role: auth.currentRole,
            tenantConfig: tenantSubject.value
        )

        // 6. Initialize each active module with ModuleContext
        activeModules.forEach { module in
            module.initialize(context: moduleContext)
            eventBus.publish(ModuleInitializedEvent(moduleId: module.metadata.id))
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView(
                container: container,
                slotRegistry: slotRegistry
            )
        }
    }
}

