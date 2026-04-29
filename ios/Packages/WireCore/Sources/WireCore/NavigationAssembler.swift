import Contracts
import SwiftUI

/// Builds the SwiftUI navigation routes from all active `AppModule`s.
///
/// Calls `AppModule.provideRoutes()` on each active module and aggregates
/// the results. Mirrors Android `NavigationAssembler` and Flutter `NavigationAssembler`.
public final class NavigationAssembler {
    public init() {}

    /// Returns all `ModuleRoute`s contributed by active `modules`,
    /// filtered to routes accessible by `userRole`.
    public func assembleRoutes(
        from modules: [any AppModule],
        userRole: Role = .GUEST
    ) -> [ModuleRoute] {
        modules
            .flatMap { $0.provideRoutes() }
            .filter { isRoleAllowed(user: userRole, required: $0.requiredRole) }
    }

    // MARK: - Private

    private let roleOrder: [Role] = [.GUEST, .CUSTOMER, .STAFF, .ADMIN]

    private func isRoleAllowed(user: Role, required: Role) -> Bool {
        let userIdx = roleOrder.firstIndex(of: user) ?? 0
        let reqIdx = roleOrder.firstIndex(of: required) ?? 0
        return userIdx >= reqIdx
    }
}

