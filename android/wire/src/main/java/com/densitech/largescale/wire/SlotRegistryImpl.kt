package com.densitech.largescale.wire

import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.SlotRegistry
import com.densitech.largescale.contracts.UISlot
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe implementation of [SlotRegistry].
 *
 * Stores widgets keyed by [UISlot.widgetId] in a [ConcurrentHashMap].
 * Retrieval filters by slot host ID and user role, then sorts by descending priority.
 *
 * Role privilege order (highest → lowest): ADMIN, STAFF, CUSTOMER, GUEST
 * A user with role R can see any widget whose [UISlot.requiredRole] has ordinal >= R.ordinal.
 * Example: STAFF (ordinal 1) can see widgets requiring STAFF (1), CUSTOMER (2), or GUEST (3),
 * but NOT widgets requiring ADMIN (0) exclusively.
 */
@Singleton
class SlotRegistryImpl @Inject constructor() : SlotRegistry {

    private val slots = ConcurrentHashMap<String, UISlot>()

    override fun register(slot: UISlot) {
        slots[slot.widgetId] = slot
    }

    override fun getSlotsForHost(slotId: String, userRole: Role): List<UISlot> {
        return slots.values
            .filter { slot ->
                slot.slotId == slotId && userRole.ordinal <= slot.requiredRole.ordinal
            }
            .sortedByDescending { it.priority }
    }

    override fun unregister(widgetId: String) {
        slots.remove(widgetId)
    }

    override fun clearModule(moduleId: String) {
        val toRemove = slots.values.filter { it.moduleId == moduleId }.map { it.widgetId }
        toRemove.forEach { slots.remove(it) }
    }
}
