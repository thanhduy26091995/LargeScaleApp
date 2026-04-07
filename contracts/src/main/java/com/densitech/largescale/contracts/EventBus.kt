package com.densitech.largescale.contracts

import kotlinx.coroutines.Job
import kotlin.reflect.KClass

/**
 * Cross-module event bus for decoupled communication.
 *
 * Modules publish typed events without knowing who subscribes.
 * Modules subscribe to specific event types without knowing who publishes.
 *
 * Usage:
 * ```
 * // Publish
 * eventBus.publish(OrderCreatedEvent(orderId = "123", moduleId = "orders"))
 *
 * // Subscribe (in module initialize())
 * eventBus.subscribe(UserAuthenticatedEvent::class) { event ->
 *     handleUserAuthenticated(event.role)
 * }
 * ```
 */
interface EventBus {
    /**
     * Publish an event to all active subscribers of that event type.
     * Fire-and-forget — does not suspend.
     */
    fun <T : ModuleEvent> publish(event: T)

    /**
     * Subscribe to events of a specific type.
     *
     * The [onEvent] callback is invoked on the main thread for each matching event.
     * Returns a [Job] — cancel it to stop receiving events (e.g., in [AppModule.onDestroy]).
     *
     * @param eventType The KClass of the event to listen for
     * @param onEvent Suspend callback invoked for each matching event
     */
    fun <T : ModuleEvent> subscribe(
        eventType: KClass<T>,
        onEvent: suspend (T) -> Unit
    ): Job
}

/**
 * Convenience inline reified wrapper for [EventBus.subscribe].
 *
 * Usage:
 * ```
 * val job = eventBus.on<OrderCreatedEvent> { event -> ... }
 * ```
 */
inline fun <reified T : ModuleEvent> EventBus.on(noinline onEvent: suspend (T) -> Unit): Job =
    subscribe(T::class, onEvent)
