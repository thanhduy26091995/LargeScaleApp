import Combine
import Contracts
import WireCore

/// Orders feature module — accessible to ADMIN and STAFF.
public final class OrdersModule: AppModule {
    private var cancellables = Set<AnyCancellable>()
    private let repository = OrdersRepositoryImpl()

    public init() {}

    public let metadata = ModuleMetadata(
        id: "orders",
        name: "Orders",
        requiredRoles: [.ADMIN, .STAFF],
        priority: 90
    )

    public func initialize(context: any ModuleContext) {
        // React to auth events
        (context.eventBus as? AppEventBus)?
            .on(UserAuthenticatedEvent.self)
            .sink { [weak self] _ in
                // Re-fetch orders for newly authenticated user
                _ = self
            }
            .store(in: &cancellables)

        // Register UI slots
        for slot in provideWidgets() {
            context.slotRegistry.register(slot)
        }
    }

    public func onDestroy() {
        cancellables.removeAll()
    }

    public func provideRoutes() -> [ModuleRoute] {
        [ModuleRoute(route: "/orders", requiredRole: .STAFF)]
    }
}

