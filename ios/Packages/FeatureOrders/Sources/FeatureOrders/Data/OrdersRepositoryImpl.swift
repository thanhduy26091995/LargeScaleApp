import Foundation

/// Stub repository — returns hardcoded data for scaffolding.
public final class OrdersRepositoryImpl: OrdersRepository {
    public init() {}

    public func getOrders() async throws -> [Order] {
        [
            Order(id: "1", title: "Order #1001", amount: 45.00, status: .completed),
            Order(id: "2", title: "Order #1002", amount: 120.50, status: .processing),
            Order(id: "3", title: "Order #1003", amount: 89.99, status: .pending),
        ]
    }
}
