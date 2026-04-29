import Foundation

/// All navigable routes in the app.
public enum AppRoute: String, Hashable, CaseIterable {
    case dashboard
    case orders
    case inventory
    case wallet

    public var path: String { "/\(rawValue)" }
}
