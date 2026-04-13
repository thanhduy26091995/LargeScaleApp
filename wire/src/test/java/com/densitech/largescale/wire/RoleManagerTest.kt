package com.densitech.largescale.wire

import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.UserAuthenticatedEvent
import com.densitech.largescale.contracts.UserLoggedOutEvent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RoleManagerTest {

    private lateinit var eventBus: FlowEventBus
    private lateinit var roleManager: RoleManager

    @Before
    fun setup() {
        eventBus = FlowEventBus()
        roleManager = RoleManager(eventBus)
    }

    // ── 7.2 Initial state ─────────────────────────────────────────────────────

    @Test
    fun `initial role is GUEST`() {
        assertEquals(Role.GUEST, roleManager.currentRole.value)
    }

    // ── 7.2 React to UserAuthenticatedEvent ───────────────────────────────────

    @Test
    fun `publishes ADMIN role updates currentRole to ADMIN`() = runTest {
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-001", role = Role.ADMIN))
        kotlinx.coroutines.delay(50)

        assertEquals(Role.ADMIN, roleManager.currentRole.value)
    }

    @Test
    fun `publishes STAFF role updates currentRole to STAFF`() = runTest {
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-002", role = Role.STAFF))
        kotlinx.coroutines.delay(50)

        assertEquals(Role.STAFF, roleManager.currentRole.value)
    }

    @Test
    fun `publishes CUSTOMER role updates currentRole to CUSTOMER`() = runTest {
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-003", role = Role.CUSTOMER))
        kotlinx.coroutines.delay(50)

        assertEquals(Role.CUSTOMER, roleManager.currentRole.value)
    }

    @Test
    fun `publishes GUEST role keeps currentRole as GUEST`() = runTest {
        // Pre-set to ADMIN then switch back to GUEST via authenticated event
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-001", role = Role.ADMIN))
        kotlinx.coroutines.delay(50)
        assertEquals(Role.ADMIN, roleManager.currentRole.value)

        eventBus.publish(UserAuthenticatedEvent(userId = "usr-004", role = Role.GUEST))
        kotlinx.coroutines.delay(50)

        assertEquals(Role.GUEST, roleManager.currentRole.value)
    }

    // ── 7.2 React to UserLoggedOutEvent ───────────────────────────────────────

    @Test
    fun `UserLoggedOutEvent resets role to GUEST`() = runTest {
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-001", role = Role.ADMIN))
        kotlinx.coroutines.delay(50)
        assertEquals(Role.ADMIN, roleManager.currentRole.value)

        eventBus.publish(UserLoggedOutEvent())
        kotlinx.coroutines.delay(50)

        assertEquals(Role.GUEST, roleManager.currentRole.value)
    }

    @Test
    fun `logout from STAFF resets to GUEST`() = runTest {
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-002", role = Role.STAFF))
        kotlinx.coroutines.delay(50)

        eventBus.publish(UserLoggedOutEvent())
        kotlinx.coroutines.delay(50)

        assertEquals(Role.GUEST, roleManager.currentRole.value)
    }

    @Test
    fun `multiple auth events — last one wins`() = runTest {
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-001", role = Role.ADMIN))
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-002", role = Role.STAFF))
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-003", role = Role.CUSTOMER))
        kotlinx.coroutines.delay(100)

        assertEquals(Role.CUSTOMER, roleManager.currentRole.value)
    }

    // ── 7.2 setRole (direct override) ─────────────────────────────────────────

    @Test
    fun `setRole directly updates currentRole without events`() {
        roleManager.setRole(Role.STAFF)

        assertEquals(Role.STAFF, roleManager.currentRole.value)
    }

    @Test
    fun `setRole can be overridden by subsequent event`() = runTest {
        roleManager.setRole(Role.ADMIN)

        eventBus.publish(UserLoggedOutEvent())
        kotlinx.coroutines.delay(50)

        assertEquals(Role.GUEST, roleManager.currentRole.value)
    }
}
