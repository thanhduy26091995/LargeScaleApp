package com.densitech.largescale.wire

import com.densitech.largescale.contracts.EventBus
import com.densitech.largescale.contracts.ModuleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * Flow-based implementation of [EventBus].
 *
 * Uses [MutableSharedFlow] with a replay buffer of 0 (events are live-only).
 * [extraBufferCapacity] of 64 ensures [tryEmit] never drops events under normal load.
 *
 * Thread safety: [MutableSharedFlow] is thread-safe; [publish] can be called from any thread.
 * Subscribers are dispatched on [Dispatchers.Default] by default and can switch context if needed.
 */
@Singleton
class FlowEventBus @Inject constructor() : EventBus {

    private val _events = MutableSharedFlow<ModuleEvent>(
        replay = 0,
        extraBufferCapacity = 64
    )

    // Long-lived scope tied to the singleton lifetime
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun <T : ModuleEvent> publish(event: T) {
        _events.tryEmit(event)
    }

    override fun <T : ModuleEvent> subscribe(
        eventType: KClass<T>,
        onEvent: suspend (T) -> Unit
    ): Job {
        return _events
            .filterIsInstance(eventType)
            .onEach { event -> onEvent(event) }
            .launchIn(scope)
    }
}
