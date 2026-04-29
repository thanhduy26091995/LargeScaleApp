package com.densitech.largescale.core.auth

import com.densitech.largescale.contracts.EventBus
import com.densitech.largescale.contracts.ModuleEvent
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.UserAuthenticatedEvent
import com.densitech.largescale.contracts.UserLoggedOutEvent
import com.densitech.largescale.core.storage.StorageManager
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class AuthServiceImplTest {

    private lateinit var fakeEventBus: FakeEventBus
    private lateinit var storageManager: StorageManager
    private lateinit var authService: AuthServiceImpl

    @Before
    fun setup() {
        fakeEventBus = FakeEventBus()
        storageManager = mockk(relaxed = true)
        authService = AuthServiceImpl(fakeEventBus, storageManager)
    }

    // ── 7.1 Initial state ─────────────────────────────────────────────────────

    @Test
    fun `initial state is unauthenticated GUEST`() {
        assertNull(authService.currentUser.value)
        assertEquals(Role.GUEST, authService.currentRole.value)
        assertFalse(authService.isAuthenticated.value)
    }

    // ── 7.5 login — valid credentials ─────────────────────────────────────────

    @Test
    fun `login with valid admin credentials succeeds`() = runTest {
        val result = authService.login("admin", "admin")

        assertTrue(result.isSuccess)
        val user = result.getOrThrow()
        assertEquals("admin", user.username)
        assertEquals(Role.ADMIN, user.role)
    }

    @Test
    fun `login updates state flows on success`() = runTest {
        authService.login("staff", "staff")

        assertNotNull(authService.currentUser.value)
        assertEquals(Role.STAFF, authService.currentRole.value)
        assertTrue(authService.isAuthenticated.value)
    }

    @Test
    fun `login publishes UserAuthenticatedEvent with correct role`() = runTest {
        authService.login("customer", "customer")

        val events = fakeEventBus.published.filterIsInstance<UserAuthenticatedEvent>()
        assertEquals(1, events.size)
        assertEquals(Role.CUSTOMER, events[0].role)
    }

    @Test
    fun `login persists session to storage`() = runTest {
        authService.login("admin", "admin")

        coVerify { storageManager.putString("auth_user_id", any()) }
        coVerify { storageManager.putString("auth_username", "admin") }
        coVerify { storageManager.putString("auth_role", "ADMIN") }
    }

    @Test
    fun `all four roles can log in with correct credentials`() = runTest {
        val accounts = listOf(
            "admin" to Role.ADMIN,
            "staff" to Role.STAFF,
            "customer" to Role.CUSTOMER,
            "guest" to Role.GUEST
        )
        for ((username, expectedRole) in accounts) {
            val result = authService.login(username, username)
            assertTrue("login for $username should succeed", result.isSuccess)
            assertEquals(expectedRole, result.getOrThrow().role)
        }
    }

    // ── 7.5 login — invalid credentials ──────────────────────────────────────

    @Test
    fun `login with wrong password returns failure`() = runTest {
        val result = authService.login("admin", "wrongpassword")

        assertTrue(result.isFailure)
        assertNull(authService.currentUser.value)
        assertFalse(authService.isAuthenticated.value)
    }

    @Test
    fun `login with unknown username returns failure`() = runTest {
        val result = authService.login("nobody", "password")

        assertTrue(result.isFailure)
        assertFalse(authService.isAuthenticated.value)
    }

    @Test
    fun `failed login does NOT publish any event`() = runTest {
        authService.login("admin", "wrong")

        assertTrue(fakeEventBus.published.isEmpty())
    }

    // ── 7.5 logout ────────────────────────────────────────────────────────────

    @Test
    fun `logout clears state flows`() = runTest {
        authService.login("admin", "admin")
        authService.logout()

        assertNull(authService.currentUser.value)
        assertEquals(Role.GUEST, authService.currentRole.value)
        assertFalse(authService.isAuthenticated.value)
    }

    @Test
    fun `logout publishes UserLoggedOutEvent`() = runTest {
        authService.login("admin", "admin")
        fakeEventBus.published.clear()

        authService.logout()

        val events = fakeEventBus.published.filterIsInstance<UserLoggedOutEvent>()
        assertEquals(1, events.size)
    }

    @Test
    fun `logout removes session from storage`() = runTest {
        authService.login("admin", "admin")
        authService.logout()

        coVerify { storageManager.remove("auth_user_id") }
        coVerify { storageManager.remove("auth_username") }
        coVerify { storageManager.remove("auth_role") }
    }

    // ── 7.1 restoreSession ────────────────────────────────────────────────────

    @Test
    fun `restoreSession returns null when no stored session`() = runTest {
        coEvery { storageManager.getString("auth_user_id") } returns null

        val user = authService.restoreSession()

        assertNull(user)
        assertFalse(authService.isAuthenticated.value)
    }

    @Test
    fun `restoreSession restores admin user from storage`() = runTest {
        coEvery { storageManager.getString("auth_user_id") } returns "usr-001"
        coEvery { storageManager.getString("auth_username") } returns "admin"
        coEvery { storageManager.getString("auth_role") } returns "ADMIN"

        val user = authService.restoreSession()

        assertNotNull(user)
        assertEquals("admin", user!!.username)
        assertEquals(Role.ADMIN, user.role)
        assertTrue(authService.isAuthenticated.value)
    }

    @Test
    fun `restoreSession publishes UserAuthenticatedEvent on success`() = runTest {
        coEvery { storageManager.getString("auth_user_id") } returns "usr-002"
        coEvery { storageManager.getString("auth_username") } returns "staff"
        coEvery { storageManager.getString("auth_role") } returns "STAFF"

        authService.restoreSession()

        val events = fakeEventBus.published.filterIsInstance<UserAuthenticatedEvent>()
        assertEquals(1, events.size)
        assertEquals(Role.STAFF, events[0].role)
    }

    @Test
    fun `restoreSession with corrupted role defaults to GUEST`() = runTest {
        coEvery { storageManager.getString("auth_user_id") } returns "usr-003"
        coEvery { storageManager.getString("auth_username") } returns "customer"
        coEvery { storageManager.getString("auth_role") } returns "INVALID_ROLE"

        val user = authService.restoreSession()

        assertNotNull(user)
        assertEquals(Role.GUEST, user!!.role)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private class FakeEventBus : EventBus {
        val published = mutableListOf<ModuleEvent>()

        override fun <T : ModuleEvent> publish(event: T) {
            published.add(event)
        }

        override fun <T : ModuleEvent> subscribe(
            eventType: KClass<T>,
            onEvent: suspend (T) -> Unit
        ): Job = Job()
    }
}
