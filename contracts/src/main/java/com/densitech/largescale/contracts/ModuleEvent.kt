package com.densitech.largescale.contracts

/**
 * Base sealed class for all events in the event bus system.
 * Modules publish and subscribe to typed events for decoupled cross-module communication.
 *
 * Publishing:
 * ```
 * context.eventBus.publish(OrderCreatedEvent(orderId = "123", moduleId = "orders"))
 * ```
 *
 * Subscribing (in AppModule.initialize):
 * ```
 * context.eventBus.on<UserAuthenticatedEvent> { event ->
 *     loadDataForRole(event.role)
 * }
 * ```
 */
sealed class ModuleEvent {
    /** Timestamp when the event was created (milliseconds since epoch). */
    open val timestamp: Long = System.currentTimeMillis()
}

// ─── Auth events ────────────────────────────────────────────────────────────

/** Published when a user successfully authenticates. */
data class UserAuthenticatedEvent(
    val userId: String,
    val role: Role,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

/** Published when the current user logs out. */
data class UserLoggedOutEvent(
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

// ─── Module lifecycle events ─────────────────────────────────────────────────

/** Published when a module completes its initialization. */
data class ModuleInitializedEvent(
    val moduleId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

// ─── Domain events ───────────────────────────────────────────────────────────

/**
 * Published when an order is created.
 * Other modules (e.g., Dashboard, Wallet) can react to update their state.
 */
data class OrderCreatedEvent(
    val orderId: String,
    val moduleId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

// ─── Tenant events ───────────────────────────────────────────────────────────

/**
 * Published when the active tenant changes at runtime.
 * All modules should re-initialize tenant-specific state upon receiving this.
 */
data class TenantSwitchedEvent(
    val oldTenantId: String,
    val newTenantId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

/**
 * Published when the configuration of the current tenant is updated
 * (e.g., theme, enabled modules) without a full tenant switch.
 */
data class TenantConfigUpdatedEvent(
    val tenantId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

// ─── Feature flag events ─────────────────────────────────────────────────────

/** Published when a feature flag is toggled at runtime. */
data class FeatureFlagChangedEvent(
    val key: String,
    val enabled: Boolean,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

// ─── Navigation events ───────────────────────────────────────────────────────

/**
 * Published when a module requests navigation to a route it does not own.
 * The Wire Core / NavigationAssembler listens and executes the navigation.
 */
data class NavigationRequestedEvent(
    val route: String,
    val args: Map<String, String> = emptyMap(),
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()
