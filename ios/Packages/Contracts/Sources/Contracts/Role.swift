import Foundation

/// User roles supported by the system.
/// Mirrors Android `Role` enum — uppercase ADMIN, STAFF, CUSTOMER, GUEST.
public enum Role: String, CaseIterable, Equatable, Hashable {
    case ADMIN
    case STAFF
    case CUSTOMER
    case GUEST
}

