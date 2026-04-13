package com.densitech.largescale.wire

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.TenantConfig
import com.densitech.largescale.contracts.UISlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ModuleRegistryTest {

    private lateinit var registry: ModuleRegistry

    @Before
    fun setup() {
        registry = ModuleRegistry()
    }

    // ── 7.1 Registration ──────────────────────────────────────────────────────

    @Test
    fun `registering a module makes it retrievable by id`() {
        val module = fakeModule("orders", roles = setOf(Role.ADMIN, Role.STAFF))
        registry.register(module)

        assertEquals(module, registry.getModuleById("orders"))
    }

    @Test
    fun `duplicate registration is ignored`() {
        val module = fakeModule("orders")
        registry.register(module)
        registry.register(module) // second call should be a no-op

        assertEquals(1, registry.getAllModules().size)
    }

    @Test
    fun `getModuleById returns null for unknown id`() {
        assertNull(registry.getModuleById("unknown"))
    }

    // ── 7.5 Role-based resolution ─────────────────────────────────────────────

    @Test
    fun `ADMIN sees all modules`() {
        registry.register(fakeModule("core",      roles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER, Role.GUEST)))
        registry.register(fakeModule("orders",    roles = setOf(Role.ADMIN, Role.STAFF)))
        registry.register(fakeModule("inventory", roles = setOf(Role.ADMIN, Role.STAFF)))
        registry.register(fakeModule("wallet",    roles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER)))

        val result = registry.resolve(Role.ADMIN, tenantConfig = null)

        assertEquals(4, result.size)
    }

    @Test
    fun `CUSTOMER only sees modules requiring CUSTOMER or GUEST`() {
        registry.register(fakeModule("core",   roles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER, Role.GUEST)))
        registry.register(fakeModule("orders", roles = setOf(Role.ADMIN, Role.STAFF)))  // STAFF only
        registry.register(fakeModule("wallet", roles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER)))

        val result = registry.resolve(Role.CUSTOMER, tenantConfig = null)
        val ids = result.map { it.metadata.id }

        assertTrue("core should be visible to CUSTOMER", "core" in ids)
        assertTrue("wallet should be visible to CUSTOMER", "wallet" in ids)
        assertTrue("orders should NOT be visible to CUSTOMER", "orders" !in ids)
        assertEquals(2, result.size)
    }

    @Test
    fun `GUEST only sees modules that include GUEST role`() {
        registry.register(fakeModule("core",   roles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER, Role.GUEST)))
        registry.register(fakeModule("orders", roles = setOf(Role.ADMIN, Role.STAFF)))

        val result = registry.resolve(Role.GUEST, tenantConfig = null)

        assertEquals(1, result.size)
        assertEquals("core", result[0].metadata.id)
    }

    @Test
    fun `modules are sorted by descending priority`() {
        registry.register(fakeModule("wallet",    roles = setOf(Role.ADMIN), priority = 700))
        registry.register(fakeModule("orders",    roles = setOf(Role.ADMIN), priority = 800))
        registry.register(fakeModule("dashboard", roles = setOf(Role.ADMIN), priority = 900))
        registry.register(fakeModule("core",      roles = setOf(Role.ADMIN), priority = 1000))

        val result = registry.resolve(Role.ADMIN, tenantConfig = null)
        val ids = result.map { it.metadata.id }

        assertEquals(listOf("core", "dashboard", "orders", "wallet"), ids)
    }

    // ── Tenant filtering ──────────────────────────────────────────────────────

    @Test
    fun `module with empty supportedTenants is visible to all tenants`() {
        registry.register(fakeModule("core", roles = setOf(Role.ADMIN), supportedTenants = emptyList()))
        val tenant = TenantConfig(
            tenantId = "brand-a",
            displayName = "Brand A",
            enabledModules = listOf("core")
        )

        val result = registry.resolve(Role.ADMIN, tenantConfig = tenant)
        assertEquals(1, result.size)
    }

    @Test
    fun `module not in tenant enabledModules is excluded`() {
        registry.register(fakeModule("orders", roles = setOf(Role.ADMIN)))
        val tenantWithoutOrders = TenantConfig(
            tenantId = "brand-b",
            displayName = "Brand B",
            enabledModules = listOf("core", "dashboard") // no "orders"
        )

        val result = registry.resolve(Role.ADMIN, tenantConfig = tenantWithoutOrders)

        assertTrue("orders should be excluded for this tenant", result.isEmpty())
    }

    @Test
    fun `module in supportedTenants but not enabledModules is excluded`() {
        registry.register(
            fakeModule("orders", roles = setOf(Role.ADMIN), supportedTenants = listOf("brand-a"))
        )
        val tenant = TenantConfig(
            tenantId = "brand-a",
            displayName = "Brand A",
            enabledModules = emptyList() // not enabled
        )

        val result = registry.resolve(Role.ADMIN, tenantConfig = tenant)
        assertTrue(result.isEmpty())
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun fakeModule(
        id: String,
        roles: Set<Role> = setOf(Role.ADMIN),
        priority: Int = 100,
        supportedTenants: List<String> = emptyList()
    ): AppModule = object : AppModule {
        override val metadata = ModuleMetadata(
            id = id,
            name = id.replaceFirstChar { it.uppercase() },
            requiredRoles = roles,
            priority = priority,
            supportedTenants = supportedTenants
        )
        override fun initialize(context: ModuleContext) {}
        override fun provideRoutes(): List<ModuleRoute> = emptyList()
        override fun provideWidgets(): List<UISlot> = emptyList()
    }
}
