import Foundation

public struct Order: Identifiable, Equatable {
    public let id: String
    public let title: String
    public let amount: Double
    public let status: OrderStatus

    public init(id: String, title: String, amount: Double, status: OrderStatus) {
        self.id = id
        self.title = title
        self.amount = amount
        self.status = status
    }
}

public enum OrderStatus: String, Equatable {
    case pending, processing, completed, cancelled
}
