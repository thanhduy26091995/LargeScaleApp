import Foundation

@MainActor
public final class OrdersViewModel: ObservableObject {
    @Published public private(set) var orders: [Order] = []
    @Published public private(set) var isLoading = false
    @Published public private(set) var errorMessage: String?

    private let useCase: GetOrdersUseCase

    public init(useCase: GetOrdersUseCase) {
        self.useCase = useCase
    }

    public func loadOrders() async {
        isLoading = true
        errorMessage = nil
        do {
            orders = try await useCase()
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}
