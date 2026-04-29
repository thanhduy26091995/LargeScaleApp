import SwiftUI

/// A navigation route contributed by a module.
///
/// Mirrors Android `ModuleRoute` and Flutter `ModuleRoute`.
public struct ModuleRoute {
    /// The route path string (e.g., "/orders", "/orders/:id").
    public let route: String

    /// Minimum role required to navigate to this route.
    public let requiredRole: Role

    public init(route: String, requiredRole: Role = .GUEST) {
        self.route = route
        self.requiredRole = requiredRole
    }
}

/// Known slot host IDs used across the app.
///
/// Mirrors Android `SlotIds` object and Flutter `SlotIds` class.
public enum SlotIds {
    /// Dashboard home screen — primary widget area.
    public static let homeWidgets = "home_widgets"
    /// Dashboard header — branding or summary info.
    public static let dashboardHeader = "dashboard_header"
    /// Profile screen — extra actions contributed by modules.
    public static let profileActions = "profile_actions"
    /// Bottom navigation bar — extra nav items contributed by modules.
    public static let bottomBarActions = "bottom_bar_actions"
    /// Quick-action FAB area on the home screen.
    public static let homeQuickActions = "home_quick_actions"
}

/// A UI widget contributed by a module for dynamic slot composition.
///
/// Mirrors Android `UISlot`. Modules return a list from `AppModule.provideWidgets()`.
public struct UISlot: Identifiable {
    /// The slot host identifier (use `SlotIds` constants).
    public let slotId: String
    /// Unique identifier for this widget.
    public let widgetId: String
    /// ID of the module contributing this widget (used by `SlotRegistry.clearModule`).
    public let moduleId: String
    /// Display order — higher priority renders first.
    public let priority: Int
    /// Minimum role required to see this widget.
    public let requiredRole: Role
    /// SwiftUI view factory.
    public let content: () -> AnyView

    public var id: String { widgetId }

    public init(
        slotId: String,
        widgetId: String,
        moduleId: String,
        priority: Int = 100,
        requiredRole: Role = .GUEST,
        content: @escaping () -> AnyView
    ) {
        self.slotId = slotId
        self.widgetId = widgetId
        self.moduleId = moduleId
        self.priority = priority
        self.requiredRole = requiredRole
        self.content = content
    }
}
