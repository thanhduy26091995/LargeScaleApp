import SwiftUI
import WireCore
import Contracts
import FeatureDashboard
import FeatureOrders
import FeatureInventory
import FeatureWallet

struct ContentView: View {
    let container: WireContainer
    let slotRegistry: SlotRegistry

    var body: some View {
        TabView {
            DashboardView(slotRegistry: slotRegistry)
                .tabItem {
                    Label("Dashboard", systemImage: "house")
                }

            OrdersView()
                .tabItem {
                    Label("Orders", systemImage: "list.bullet")
                }

            InventoryView()
                .tabItem {
                    Label("Inventory", systemImage: "archivebox")
                }

            WalletView()
                .tabItem {
                    Label("Wallet", systemImage: "creditcard")
                }
        }
    }
}

