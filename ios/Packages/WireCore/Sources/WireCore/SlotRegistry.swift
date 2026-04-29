import SwiftUI
import Contracts

/// Registry for dynamic UI slot composition.
///
/// Feature modules contribute `UISlot`s during `AppModule.initialize(context:)`
/// (via `AppModule.provideWidgets()`). Host screens call `getSlotsForHost` to
/// render contributed content.
///
/// Mirrors Android `SlotRegistry` and Flutter `SlotRegistry`.
public final class SlotRegistry: ObservableObject, SlotRegistryProtocol {
    private var slots: [String: [UISlot]] = [:]

    public init() {}

    /// Register a `UISlot`. If a widget with the same `widgetId` already exists
    /// it is replaced.
    public func register(_ slot: UISlot) {
        var existing = slots[slot.slotId, default: []]
        existing.removeAll { $0.widgetId == slot.widgetId }
        existing.append(slot)
        existing.sort { $0.priority > $1.priority }
        slots[slot.slotId] = existing
    }

    /// Returns all widgets for `slotId` visible to `userRole`,
    /// sorted by descending priority.
    public func getSlotsForHost(slotId: String, userRole: Role) -> [UISlot] {
        (slots[slotId] ?? []).filter { roleAllowed(user: userRole, required: $0.requiredRole) }
    }

    /// Remove a specific widget by `widgetId`.
    public func unregister(widgetId: String) {
        for key in slots.keys {
            slots[key]?.removeAll { $0.widgetId == widgetId }
        }
    }

    /// Remove all widgets contributed by `moduleId`. Call in `AppModule.onDestroy()`.
    public func clearModule(moduleId: String) {
        for key in slots.keys {
            slots[key]?.removeAll { $0.moduleId == moduleId }
        }
    }

    // MARK: - Private

    private let roleOrder: [Role] = [.GUEST, .CUSTOMER, .STAFF, .ADMIN]

    private func roleAllowed(user: Role, required: Role) -> Bool {
        let userIdx = roleOrder.firstIndex(of: user) ?? 0
        let reqIdx = roleOrder.firstIndex(of: required) ?? 0
        return userIdx >= reqIdx
    }
}

