import SwiftUI

public struct InventoryView: View {
    public init() {}

    public var body: some View {
        NavigationStack {
            Text("Inventory — product list goes here")
                .navigationTitle("Inventory")
        }
    }
}
