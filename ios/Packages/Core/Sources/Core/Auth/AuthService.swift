import Contracts

/// Authentication service protocol.
public protocol AuthService: AnyObject {
    var currentRole: Role { get }
    var isAuthenticated: Bool { get }
}

/// Stub implementation for scaffolding.
public final class StubAuthService: AuthService {
    public var currentRole: Role { .admin }
    public var isAuthenticated: Bool { true }
    public init() {}
}
