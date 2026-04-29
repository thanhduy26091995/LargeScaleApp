import SwiftUI

public struct OrdersView: View {
    @StateObject private var viewModel: OrdersViewModel

    public init(viewModel: OrdersViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    public var body: some View {
        List(viewModel.orders) { order in
            HStack {
                VStack(alignment: .leading) {
                    Text(order.title).font(.headline)
                    Text(String(format: "$%.2f", order.amount)).font(.subheadline)
                }
                Spacer()
                Text(order.status.rawValue.capitalized)
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.accentColor.opacity(0.15))
                    .clipShape(Capsule())
            }
        }
        .navigationTitle("Orders")
        .task { await viewModel.loadOrders() }
        .overlay {
            if viewModel.isLoading {
                ProgressView()
            }
        }
    }
}
