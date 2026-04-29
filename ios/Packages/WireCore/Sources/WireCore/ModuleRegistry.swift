import Contracts

/// Resolves which `AppModule`s are active for a given `Role` and `TenantConfig`.
///
/// A module is active when:
/// 1. `role` is in `ModuleMetadata.requiredRoles`
/// 2. `supportedTenants` is empty (all tenants) OR tenant is listed
/// 3. Module ID is in `TenantConfig.enabledModules` (when tenant config is present)
///
/// Mirrors Android `ModuleRegistry.resolve(role:tenantConfig:)`.
public final class ModuleRegistry {
    private var modules: [any AppModule] = []

    public init() {}

    /// Register a feature module. Duplicate module IDs are ignored.
    public func register(_ module: any AppModule) {
        guard !modules.contains(where: { $0.metadata.id == module.metadata.id }) else { return }
        modules.append(module)
    }

    /// Returns modules active for `role` + `tenantConfig`,
    /// sorted by descending `ModuleMetadata.priority`.
    public func resolve(role: Role, tenantConfig: TenantConfig?) -> [any AppModule] {
        modules
            .filter { module in
                // Role check
                guard module.metadata.requiredRoles.contains(role) else { return false }

                // Tenant allowlist check
                if !module.metadata.supportedTenants.isEmpty,
                   let tenant = tenantConfig,
                   !module.metadata.supportedTenants.contains(tenant.tenantId) {
                    return false
                }

                // Tenant enabledModules check
                if let tenant = tenantConfig,
                   !tenant.enabledModules.isEmpty,
                   !tenant.enabledModules.contains(module.metadata.id) {
                    return false
                }

                return true
            }
            .sorted { $0.metadata.priority > $1.metadata.priority }
    }

    /// Returns all registered modules regardless of role/tenant.
    public var all: [any AppModule] { modules }
}

