import Foundation

/// Network service protocol.
public protocol NetworkService: AnyObject {
    func get(endpoint: String) async throws -> [String: Any]
    func post(endpoint: String, body: [String: Any]) async throws -> [String: Any]
}

/// Stub implementation for scaffolding.
public final class StubNetworkService: NetworkService {
    public init() {}

    public func get(endpoint: String) async throws -> [String: Any] { [:] }
    public func post(endpoint: String, body: [String: Any]) async throws -> [String: Any] { [:] }
}
