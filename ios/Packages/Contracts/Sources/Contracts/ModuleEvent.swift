import Foundation

/// Base class for all cross-module events.
///
/// All events must inherit from `ModuleEvent` for type-safe subscription.
/// Mirrors Android sealed class `ModuleEvent`.
///
/// Usage:
/// ```swift
/// eventBus.on(OrderCreatedEvent.self)
///     .sink { event in refresh() }
///     .store(in: &cancellables)
///
/// eventBus.publish(OrderCreatedEvent(orderId: "123", moduleId: "orders"))
/// ```
open class ModuleEvent {
    public let timestamp: Date
    public init() { self.timestamp = Date() }
}

// ── Auth events ──────────────────────────────────────────────────────────────

public final class UserAuthenticatedEvent: ModuleEvent {
    public let userId: String
    public let role: Role
    public init(userId: String, role: Role) {
        self.userId = userId
        self.role = role
        super.init()
    }
}

public final class UserLoggedOutEvent: ModuleEvent {
    public override init() { super.init() }
}

// ── Module lifecycle events ──────────────────────────────────────────────────

public final class ModuleInitializedEvent: ModuleEvent {
    public let moduleId: String
    public init(moduleId: String) {
        self.moduleId = moduleId
        super.init()
    }
}

// ── Domain events ────────────────────────────────────────────────────────────

public final class OrderCreatedEvent: ModuleEvent {
    public let orderId: String
    public let moduleId: String
    public init(orderId: String, moduleId: String) {
        self.orderId = orderId
        self.moduleId = moduleId
        super.init()
    }
}

// ── Tenant events ────────────────────────────────────────────────────────────

public final class TenantSwitchedEvent: ModuleEvent {
    public let oldTenantId: String
    public let newTenantId: String
    public init(oldTenantId: String, newTenantId: String) {
        self.oldTenantId = oldTenantId
        self.newTenantId = newTenantId
        super.init()
    }
}

public final class TenantConfigUpdatedEvent: ModuleEvent {
    public let tenantId: String
    public init(tenantId: String) {
        self.tenantId = tenantId
        super.init()
    }
}

// ── Feature flag events ──────────────────────────────────────────────────────

public final class FeatureFlagChangedEvent: ModuleEvent {
    public let key: String
    public let enabled: Bool
    public init(key: String, enabled: Bool) {
        self.key = key
        self.enabled = enabled
        super.init()
    }
}

// ── Navigation events ────────────────────────────────────────────────────────

public final class NavigationRequestedEvent: ModuleEvent {
    public let route: String
    public let args: [String: String]
    public init(route: String, args: [String: String] = [:]) {
        self.route = route
        self.args = args
        super.init()
    }
}
