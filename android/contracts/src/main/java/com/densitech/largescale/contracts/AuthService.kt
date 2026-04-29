package com.densitech.largescale.contracts

import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication service — single source of truth for user identity.
 *
 * Lives in `:contracts` so feature modules can inject it directly
 * without depending on `:core`.
 *
 * Wire Core reacts to [UserAuthenticatedEvent] / [UserLoggedOutEvent] published
 * here to update [ModuleContext.currentRole] automatically.
 *
 * Usage in a ViewModel:
 * ```
 * @HiltViewModel
 * class ProfileViewModel @Inject constructor(
 *     private val authService: AuthService
 * ) : ViewModel() {
 *     val user = authService.currentUser
 * }
 * ```
 */
interface AuthService {

    /** Currently authenticated user, or null when logged out. */
    val currentUser: StateFlow<User?>

    /** Current role — mirrors [currentUser]?.role, defaults to [Role.GUEST]. */
    val currentRole: StateFlow<Role>

    /** Convenience flag: true when a user is signed in. */
    val isAuthenticated: StateFlow<Boolean>

    /**
     * Attempt to authenticate with [username] and [password].
     *
     * On success: persists the session, publishes [UserAuthenticatedEvent], returns [Result.success].
     * On failure: returns [Result.failure] with a descriptive exception — caller shows error UI.
     */
    suspend fun login(username: String, password: String): Result<User>

    /**
     * Sign out the current user.
     * Clears the session, resets role to [Role.GUEST], publishes [UserLoggedOutEvent].
     */
    suspend fun logout()

    /**
     * Restore a previously persisted session (called on app start).
     * Returns the restored [User] or null if no valid session exists.
     */
    suspend fun restoreSession(): User?
}
