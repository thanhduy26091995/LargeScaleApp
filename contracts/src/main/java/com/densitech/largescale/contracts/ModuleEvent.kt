package com.densitech.largescale.contracts

/**
 * Base sealed class for all events in the event bus system.
 * Modules publish and subscribe to events for cross-module communication.
 *
 * Example events:
 * - OrderCreated
 * - UserAuthenticated
 * - TenantSwitched
 * - ThemeChanged
 */
sealed class ModuleEvent {
    /**
     * Timestamp when the event was created (in milliseconds).
     */
    open val timestamp: Long = System.currentTimeMillis()
}

/**
 * Event published when user authentication state changes.
 */
data class UserAuthenticatedEvent(
    val userId: String,
    val role: Role,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()

/**
 * Event published when a module completes initialization.
 */
data class ModuleInitializedEvent(
    val moduleId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : ModuleEvent()
