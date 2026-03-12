package com.densitech.largescale.contracts

/**
 * Metadata describing a feature module.
 * Provides information for module discovery, filtering, and lifecycle management.
 *
 * @property id Unique identifier for the module (e.g., "orders", "dashboard")
 * @property name Display name for the module
 * @property version Module version for compatibility checks
 * @property requiredRoles Roles that have access to this module
 * @property supportedTenants List of tenant IDs this module supports (empty = all tenants)
 * @property priority Loading/initialization priority (higher = earlier)
 */
data class ModuleMetadata(
    val id: String,
    val name: String,
    val version: String = "1.0.0",
    val requiredRoles: Set<Role> = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER),
    val supportedTenants: List<String> = emptyList(), // Empty = supports all tenants
    val priority: Int = 100
)
