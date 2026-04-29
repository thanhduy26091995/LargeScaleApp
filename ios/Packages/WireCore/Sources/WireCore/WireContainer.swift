import Foundation

/// Lightweight dependency injection container.
///
/// Wraps a dictionary-based service locator.
/// For production use, replace the internals with Swinject.
public final class WireContainer {
    private var services: [ObjectIdentifier: Any] = [:]

    public init() {}

    /// Register a singleton instance of type T.
    public func register<T>(_ instance: T) {
        services[ObjectIdentifier(T.self)] = instance
    }

    /// Resolve a registered service of type T.
    /// Triggers a `fatalError` in debug if T is not registered.
    public func resolve<T>() -> T {
        guard let service = services[ObjectIdentifier(T.self)] as? T else {
            fatalError("WireContainer: \(T.self) is not registered.")
        }
        return service
    }

    /// Resolve a registered service of type T, returning nil if not registered.
    public func tryResolve<T>() -> T? {
        services[ObjectIdentifier(T.self)] as? T
    }
}
