import Contracts
import WireCore

/// Inventory feature module — accessible to ADMIN and STAFF.
public final class InventoryModule: AppModule {
    public init() {}

    public let metadata = ModuleMetadata(
        id: "inventory",
        name: "Inventory",
        requiredRoles: [.ADMIN, .STAFF],
        priority: 85
    )

    public func initialize(context: any ModuleContext) {
        for slot in provideWidgets() {
            context.slotRegistry.register(slot)
        }
    }

    public func provideRoutes() -> [ModuleRoute] {
        [ModuleRoute(route: "/inventory", requiredRole: .STAFF)]
    }
}

