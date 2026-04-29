import 'package:contracts/contracts.dart';

/// Resolves which [AppModule]s are active for a given [Role] and [TenantConfig].
///
/// A module is active when:
/// 1. [role] is in [ModuleMetadata.requiredRoles]
/// 2. [ModuleMetadata.supportedTenants] is empty (all tenants) OR tenant is listed
/// 3. Module ID is in [TenantConfig.enabledModules] (when tenant config is present)
///
/// Mirrors Android [ModuleRegistry.resolve(role, tenantConfig)].
class ModuleRegistry {
  final List<AppModule> _modules = [];

  /// Register a feature module. Duplicate module IDs are ignored.
  void register(AppModule module) {
    if (_modules.any((m) => m.metadata.id == module.metadata.id)) return;
    _modules.add(module);
  }

  /// Returns modules active for [role] + [tenantConfig],
  /// sorted by descending [ModuleMetadata.priority].
  List<AppModule> resolve(Role role, TenantConfig? tenantConfig) {
    return _modules
        .where((module) {
          // Role check
          final roleAllowed = module.metadata.requiredRoles.contains(role);
          if (!roleAllowed) return false;

          // Tenant check
          if (module.metadata.supportedTenants.isNotEmpty &&
              tenantConfig != null &&
              !module.metadata.supportedTenants.contains(tenantConfig.tenantId)) {
            return false;
          }

          // Tenant enabledModules check
          if (tenantConfig != null &&
              tenantConfig.enabledModules.isNotEmpty &&
              !tenantConfig.enabledModules.contains(module.metadata.id)) {
            return false;
          }

          return true;
        })
        .toList()
      ..sort((a, b) => b.metadata.priority.compareTo(a.metadata.priority));
  }

  /// Returns all registered modules regardless of role/tenant.
  List<AppModule> get all => List.unmodifiable(_modules);
}

