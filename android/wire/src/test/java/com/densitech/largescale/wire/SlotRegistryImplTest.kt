package com.densitech.largescale.wire

import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.SlotIds
import com.densitech.largescale.contracts.UISlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SlotRegistryImplTest {

    private lateinit var registry: SlotRegistryImpl

    @Before
    fun setup() {
        registry = SlotRegistryImpl()
    }

    // ── 7.3 SlotId filtering ──────────────────────────────────────────────────

    @Test
    fun `getSlotsForHost returns only slots matching the given slotId`() {
        registry.register(fakeSlot("w1", slotId = SlotIds.HOME_WIDGETS, moduleId = "orders"))
        registry.register(fakeSlot("w2", slotId = SlotIds.DASHBOARD_HEADER, moduleId = "dashboard"))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)

        assertEquals(1, result.size)
        assertEquals("w1", result[0].widgetId)
    }

    @Test
    fun `getSlotsForHost returns empty list when no slots match slotId`() {
        registry.register(fakeSlot("w1", slotId = SlotIds.DASHBOARD_HEADER, moduleId = "dashboard"))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)

        assertTrue(result.isEmpty())
    }

    // ── 7.3 Role filtering ────────────────────────────────────────────────────

    @Test
    fun `ADMIN sees all slots regardless of requiredRole`() {
        registry.register(fakeSlot("admin-only",    requiredRole = Role.ADMIN))
        registry.register(fakeSlot("staff-plus",    requiredRole = Role.STAFF))
        registry.register(fakeSlot("customer-plus", requiredRole = Role.CUSTOMER))
        registry.register(fakeSlot("guest-plus",    requiredRole = Role.GUEST))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)

        assertEquals(4, result.size)
    }

    @Test
    fun `STAFF sees STAFF, CUSTOMER, and GUEST slots but NOT ADMIN-only slots`() {
        registry.register(fakeSlot("admin-only",    requiredRole = Role.ADMIN))
        registry.register(fakeSlot("staff-plus",    requiredRole = Role.STAFF))
        registry.register(fakeSlot("customer-plus", requiredRole = Role.CUSTOMER))
        registry.register(fakeSlot("guest-plus",    requiredRole = Role.GUEST))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.STAFF)
        val ids = result.map { it.widgetId }

        assertEquals(3, result.size)
        assertTrue("admin-only" !in ids)
        assertTrue("staff-plus" in ids)
        assertTrue("customer-plus" in ids)
        assertTrue("guest-plus" in ids)
    }

    @Test
    fun `CUSTOMER sees CUSTOMER and GUEST slots only`() {
        registry.register(fakeSlot("admin-only",    requiredRole = Role.ADMIN))
        registry.register(fakeSlot("staff-plus",    requiredRole = Role.STAFF))
        registry.register(fakeSlot("customer-plus", requiredRole = Role.CUSTOMER))
        registry.register(fakeSlot("guest-plus",    requiredRole = Role.GUEST))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.CUSTOMER)
        val ids = result.map { it.widgetId }

        assertEquals(2, result.size)
        assertTrue("admin-only" !in ids)
        assertTrue("staff-plus" !in ids)
        assertTrue("customer-plus" in ids)
        assertTrue("guest-plus" in ids)
    }

    @Test
    fun `GUEST sees only GUEST slots`() {
        registry.register(fakeSlot("admin-only",    requiredRole = Role.ADMIN))
        registry.register(fakeSlot("customer-plus", requiredRole = Role.CUSTOMER))
        registry.register(fakeSlot("guest-plus",    requiredRole = Role.GUEST))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.GUEST)
        val ids = result.map { it.widgetId }

        assertEquals(1, result.size)
        assertTrue("guest-plus" in ids)
    }

    // ── 7.3 Priority sorting ─────────────────────────────────────────────────

    @Test
    fun `slots are returned sorted by descending priority`() {
        registry.register(fakeSlot("low",    priority = 100))
        registry.register(fakeSlot("high",   priority = 900))
        registry.register(fakeSlot("medium", priority = 500))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)
        val ids = result.map { it.widgetId }

        assertEquals(listOf("high", "medium", "low"), ids)
    }

    // ── 7.3 clearModule ───────────────────────────────────────────────────────

    @Test
    fun `clearModule removes all slots registered by that module`() {
        registry.register(fakeSlot("w1", moduleId = "orders"))
        registry.register(fakeSlot("w2", moduleId = "orders"))
        registry.register(fakeSlot("w3", moduleId = "inventory"))

        registry.clearModule("orders")

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)
        val ids = result.map { it.widgetId }

        assertEquals(1, result.size)
        assertTrue("w1" !in ids)
        assertTrue("w2" !in ids)
        assertTrue("w3" in ids)
    }

    @Test
    fun `clearModule on unknown moduleId does nothing`() {
        registry.register(fakeSlot("w1", moduleId = "orders"))

        registry.clearModule("nonexistent")

        assertEquals(1, registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN).size)
    }

    // ── 7.3 unregister ───────────────────────────────────────────────────────

    @Test
    fun `unregister removes specific widget by widgetId`() {
        registry.register(fakeSlot("w1", moduleId = "orders"))
        registry.register(fakeSlot("w2", moduleId = "orders"))

        registry.unregister("w1")

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)
        assertEquals(1, result.size)
        assertEquals("w2", result[0].widgetId)
    }

    @Test
    fun `unregister on unknown widgetId does nothing`() {
        registry.register(fakeSlot("w1", moduleId = "orders"))

        registry.unregister("nonexistent")

        assertEquals(1, registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN).size)
    }

    // ── 7.3 re-registration ───────────────────────────────────────────────────

    @Test
    fun `registering the same widgetId twice replaces the existing slot`() {
        registry.register(fakeSlot("w1", priority = 100, moduleId = "orders"))
        registry.register(fakeSlot("w1", priority = 999, moduleId = "orders"))

        val result = registry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)

        assertEquals(1, result.size)
        assertEquals(999, result[0].priority)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun fakeSlot(
        widgetId: String,
        slotId: String = SlotIds.HOME_WIDGETS,
        moduleId: String = "test-module",
        priority: Int = 100,
        requiredRole: Role = Role.ADMIN
    ) = UISlot(
        slotId = slotId,
        widgetId = widgetId,
        moduleId = moduleId,
        priority = priority,
        requiredRole = requiredRole,
        content = {}
    )
}
