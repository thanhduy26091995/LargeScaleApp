import SwiftUI

public struct WalletView: View {
    public init() {}

    public var body: some View {
        NavigationStack {
            Text("Wallet — balance and transactions go here")
                .navigationTitle("Wallet")
        }
    }
}
