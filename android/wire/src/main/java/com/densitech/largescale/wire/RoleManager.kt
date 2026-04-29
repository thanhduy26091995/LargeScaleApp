package com.densitech.largescale.wire

import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.UserAuthenticatedEvent
import com.densitech.largescale.contracts.UserLoggedOutEvent
import com.densitech.largescale.contracts.on
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks the current authenticated user's [Role].
 *
 * Defaults to [Role.GUEST] (unauthenticated).
 * Updates reactively when [UserAuthenticatedEvent] or [UserLoggedOutEvent] are published
 * to the [FlowEventBus] by the auth module.
 *
 * Phase 4: [AuthServiceImpl] will drive role changes via these same events, so this
 * class requires no changes when Core module is implemented.
 */
@Singleton
class RoleManager @Inject constructor(eventBus: FlowEventBus) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _currentRole = MutableStateFlow(Role.GUEST)
    val currentRole: StateFlow<Role> = _currentRole.asStateFlow()

    init {
        eventBus.on<UserAuthenticatedEvent> { event ->
            _currentRole.value = event.role
        }.also { /* job held by scope — lives as long as the singleton */ }

        eventBus.on<UserLoggedOutEvent> {
            _currentRole.value = Role.GUEST
        }
    }

    /** Directly set the role (useful for testing or dev-mode role switching). */
    fun setRole(role: Role) {
        _currentRole.value = role
    }
}
