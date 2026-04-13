package com.densitech.largescale.core.auth

import com.densitech.largescale.contracts.AuthService
import com.densitech.largescale.contracts.EventBus
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.User
import com.densitech.largescale.contracts.UserAuthenticatedEvent
import com.densitech.largescale.contracts.UserLoggedOutEvent
import com.densitech.largescale.core.storage.StorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of [AuthService].
 *
 * Phase 4: Uses an in-memory credential map for local testing.
 * Phase 5+: Replace with real API calls via [ApiClient].
 *
 * Hardcoded test accounts:
 * | username  | password  | role     |
 * |-----------|-----------|----------|
 * | admin     | admin     | ADMIN    |
 * | staff     | staff     | STAFF    |
 * | customer  | customer  | CUSTOMER |
 * | guest     | guest     | GUEST    |
 *
 * On login, publishes [UserAuthenticatedEvent] so [RoleManager] in `:wire`
 * updates [ModuleContext.currentRole] automatically — no direct coupling needed.
 */
@Singleton
class AuthServiceImpl @Inject constructor(
    private val eventBus: EventBus,
    private val storageManager: StorageManager
) : AuthService {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow(Role.GUEST)
    override val currentRole: StateFlow<Role> = _currentRole.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // ── Mock credential store ─────────────────────────────────────────────────

    private val mockAccounts = mapOf(
        "admin" to MockAccount(
            password = "admin",
            user = User(
                id = "usr-001",
                username = "admin",
                displayName = "Admin User",
                email = "admin@example.com",
                role = Role.ADMIN,
                tenantId = "default"
            )
        ),
        "staff" to MockAccount(
            password = "staff",
            user = User(
                id = "usr-002",
                username = "staff",
                displayName = "Staff Member",
                email = "staff@example.com",
                role = Role.STAFF,
                tenantId = "default"
            )
        ),
        "customer" to MockAccount(
            password = "customer",
            user = User(
                id = "usr-003",
                username = "customer",
                displayName = "Customer",
                email = "customer@example.com",
                role = Role.CUSTOMER,
                tenantId = "default"
            )
        ),
        "guest" to MockAccount(
            password = "guest",
            user = User(
                id = "usr-004",
                username = "guest",
                displayName = "Guest",
                email = "guest@example.com",
                role = Role.GUEST,
                tenantId = "default"
            )
        )
    )

    // ── AuthService ───────────────────────────────────────────────────────────

    override suspend fun login(username: String, password: String): Result<User> {
        val account = mockAccounts[username.trim().lowercase()]
            ?: return Result.failure(IllegalArgumentException("User '$username' not found"))

        if (account.password != password) {
            return Result.failure(IllegalArgumentException("Invalid password"))
        }

        val user = account.user
        applySession(user)
        storageManager.putString(KEY_USER_ID, user.id)
        storageManager.putString(KEY_USERNAME, user.username)
        storageManager.putString(KEY_ROLE, user.role.name)

        eventBus.publish(UserAuthenticatedEvent(userId = user.id, role = user.role))
        return Result.success(user)
    }

    override suspend fun logout() {
        clearSession()
        storageManager.remove(KEY_USER_ID)
        storageManager.remove(KEY_USERNAME)
        storageManager.remove(KEY_ROLE)
        eventBus.publish(UserLoggedOutEvent())
    }

    override suspend fun restoreSession(): User? {
        val userId = storageManager.getString(KEY_USER_ID) ?: return null
        val username = storageManager.getString(KEY_USERNAME) ?: return null
        val roleName = storageManager.getString(KEY_ROLE) ?: return null

        val role = runCatching { Role.valueOf(roleName) }.getOrDefault(Role.GUEST)
        val user = mockAccounts[username]?.user?.copy(id = userId, role = role) ?: return null

        applySession(user)
        eventBus.publish(UserAuthenticatedEvent(userId = user.id, role = user.role))
        return user
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private fun applySession(user: User) {
        _currentUser.value = user
        _currentRole.value = user.role
        _isAuthenticated.value = true
    }

    private fun clearSession() {
        _currentUser.value = null
        _currentRole.value = Role.GUEST
        _isAuthenticated.value = false
    }

    private data class MockAccount(val password: String, val user: User)

    companion object {
        private const val KEY_USER_ID = "auth_user_id"
        private const val KEY_USERNAME = "auth_username"
        private const val KEY_ROLE = "auth_role"
    }
}
