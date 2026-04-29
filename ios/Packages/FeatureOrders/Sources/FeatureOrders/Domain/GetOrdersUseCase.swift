import Foundation

public final class GetOrdersUseCase {
    private let repository: any OrdersRepository

    public init(repository: any OrdersRepository) {
        self.repository = repository
    }

    public func callAsFunction() async throws -> [Order] {
        try await repository.getOrders()
    }
}
