import 'role.dart';

/// Metadata describing a feature module.
///
/// Used by [ModuleRegistry] to filter modules by role and tenant.
/// Mirrors Android [ModuleMetadata] exactly.
class ModuleMetadata {
  /// Unique identifier for the module (e.g., 'orders', 'dashboard').
  final String id;

  /// Human-readable display name.
  final String name;

  /// Semantic version string (default '1.0.0').
  final String version;

  /// Roles that have access to this module.
  /// Default: all authenticated roles (ADMIN, STAFF, CUSTOMER).
  final Set<Role> requiredRoles;

  /// Tenant IDs this module supports. Empty = supports all tenants.
  final List<String> supportedTenants;

  /// Loading priority — higher values initialize first. Default 100.
  final int priority;

  const ModuleMetadata({
    required this.id,
    required this.name,
    this.version = '1.0.0',
    this.requiredRoles = const {Role.ADMIN, Role.STAFF, Role.CUSTOMER},
    this.supportedTenants = const [],
    this.priority = 100,
  });
}
