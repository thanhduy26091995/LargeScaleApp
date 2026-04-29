import Contracts
import WireCore

/// Dashboard feature module — accessible to ADMIN, STAFF, and CUSTOMER.
public final class DashboardModule: AppModule {
    public init() {}

    public let metadata = ModuleMetadata(
        id: "dashboard",
        name: "Dashboard",
        requiredRoles: [.ADMIN, .STAFF, .CUSTOMER],
        priority: 100 // loads first
    )

    public func initialize(context: any ModuleContext) {
        for slot in provideWidgets() {
            context.slotRegistry.register(slot)
        }
    }

    public func provideRoutes() -> [ModuleRoute] {
        [ModuleRoute(route: "/dashboard", requiredRole: .GUEST)]
    }
}

