import Foundation

/// Metadata describing a feature module.
///
/// Used by `ModuleRegistry` to filter modules by role and tenant.
/// Mirrors Android `ModuleMetadata` and Flutter `ModuleMetadata`.
public struct ModuleMetadata: Equatable {
    /// Unique identifier for the module (e.g., "orders", "dashboard").
    public let id: String

    /// Human-readable display name.
    public let name: String

    /// Semantic version string.
    public let version: String

    /// Roles that have access to this module.
    public let requiredRoles: Set<Role>

    /// Tenant IDs this module supports. Empty = supports all tenants.
    public let supportedTenants: [String]

    /// Loading priority — higher values initialize first.
    public let priority: Int

    public init(
        id: String,
        name: String,
        version: String = "1.0.0",
        requiredRoles: Set<Role> = [.ADMIN, .STAFF, .CUSTOMER],
        supportedTenants: [String] = [],
        priority: Int = 100
    ) {
        self.id = id
        self.name = name
        self.version = version
        self.requiredRoles = requiredRoles
        self.supportedTenants = supportedTenants
        self.priority = priority
    }
}
