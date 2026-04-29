import Contracts
import WireCore

/// Wallet feature module — accessible to ADMIN, STAFF, and CUSTOMER.
public final class WalletModule: AppModule {
    public init() {}

    public let metadata = ModuleMetadata(
        id: "wallet",
        name: "Wallet",
        requiredRoles: [.ADMIN, .STAFF, .CUSTOMER],
        priority: 80
    )

    public func initialize(context: any ModuleContext) {
        for slot in provideWidgets() {
            context.slotRegistry.register(slot)
        }
    }

    public func provideRoutes() -> [ModuleRoute] {
        [ModuleRoute(route: "/wallet", requiredRole: .CUSTOMER)]
    }
}

