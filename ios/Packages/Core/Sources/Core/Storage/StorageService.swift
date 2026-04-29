import Foundation

/// Key-value storage service protocol.
public protocol StorageService: AnyObject {
    func write(key: String, value: String)
    func read(key: String) -> String?
    func delete(key: String)
}

/// Stub in-memory implementation for scaffolding.
public final class StubStorageService: StorageService {
    private var store: [String: String] = [:]
    public init() {}

    public func write(key: String, value: String) { store[key] = value }
    public func read(key: String) -> String? { store[key] }
    public func delete(key: String) { store.removeValue(forKey: key) }
}
