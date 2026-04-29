import Foundation

public protocol OrdersRepository: AnyObject {
    func getOrders() async throws -> [Order]
}
