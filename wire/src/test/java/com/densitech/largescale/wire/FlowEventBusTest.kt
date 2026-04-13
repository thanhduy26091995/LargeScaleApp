package com.densitech.largescale.wire

import app.cash.turbine.test
import com.densitech.largescale.contracts.ModuleInitializedEvent
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.UserAuthenticatedEvent
import com.densitech.largescale.contracts.UserLoggedOutEvent
import com.densitech.largescale.contracts.on
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlowEventBusTest {

    private lateinit var eventBus: FlowEventBus

    @Before
    fun setup() {
        eventBus = FlowEventBus()
    }

    // ── 7.4 publish / subscribe ───────────────────────────────────────────────

    @Test
    fun `subscriber receives published event of matching type`() = runTest {
        val received = mutableListOf<UserAuthenticatedEvent>()
        val job = eventBus.on<UserAuthenticatedEvent> { received.add(it) }

        eventBus.publish(UserAuthenticatedEvent(userId = "usr-1", role = Role.ADMIN))

        // Allow coroutine to process
        kotlinx.coroutines.delay(50)
        job.cancel()

        assertEquals(1, received.size)
        assertEquals("usr-1", received[0].userId)
        assertEquals(Role.ADMIN, received[0].role)
    }

    @Test
    fun `subscriber does NOT receive events of a different type`() = runTest {
        val received = mutableListOf<UserLoggedOutEvent>()
        val job = eventBus.on<UserLoggedOutEvent> { received.add(it) }

        // Publish a DIFFERENT event type
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-1", role = Role.STAFF))
        kotlinx.coroutines.delay(50)
        job.cancel()

        assertTrue("Should not receive unrelated event", received.isEmpty())
    }

    @Test
    fun `multiple subscribers each receive the event`() = runTest {
        val received1 = mutableListOf<ModuleInitializedEvent>()
        val received2 = mutableListOf<ModuleInitializedEvent>()

        val job1 = eventBus.on<ModuleInitializedEvent> { received1.add(it) }
        val job2 = eventBus.on<ModuleInitializedEvent> { received2.add(it) }

        eventBus.publish(ModuleInitializedEvent(moduleId = "orders"))
        kotlinx.coroutines.delay(50)
        job1.cancel()
        job2.cancel()

        assertEquals(1, received1.size)
        assertEquals(1, received2.size)
        assertEquals("orders", received1[0].moduleId)
    }

    @Test
    fun `cancelled job no longer receives events`() = runTest {
        val received = mutableListOf<UserLoggedOutEvent>()
        val job = eventBus.on<UserLoggedOutEvent> { received.add(it) }

        job.cancel()

        eventBus.publish(UserLoggedOutEvent())
        kotlinx.coroutines.delay(50)

        assertTrue("Cancelled subscriber should not receive events", received.isEmpty())
    }

    @Test
    fun `multiple events of same type are all delivered`() = runTest {
        val received = mutableListOf<ModuleInitializedEvent>()
        val job = eventBus.on<ModuleInitializedEvent> { received.add(it) }

        eventBus.publish(ModuleInitializedEvent(moduleId = "core"))
        eventBus.publish(ModuleInitializedEvent(moduleId = "dashboard"))
        eventBus.publish(ModuleInitializedEvent(moduleId = "orders"))
        kotlinx.coroutines.delay(100)
        job.cancel()

        assertEquals(3, received.size)
        assertEquals(listOf("core", "dashboard", "orders"), received.map { it.moduleId })
    }

    @Test
    fun `publish from multiple threads does not drop events`() = runTest {
        val received = mutableListOf<ModuleInitializedEvent>()
        val job = eventBus.on<ModuleInitializedEvent> { received.add(it) }

        val publishCount = 20
        repeat(publishCount) { i ->
            kotlinx.coroutines.launch(kotlinx.coroutines.Dispatchers.Default) {
                eventBus.publish(ModuleInitializedEvent(moduleId = "mod-$i"))
            }
        }
        kotlinx.coroutines.delay(200)
        job.cancel()

        assertEquals(publishCount, received.size)
    }
}
