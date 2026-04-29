import SwiftUI

public struct DashboardView: View {
    public init() {}

    public var body: some View {
        NavigationStack {
            VStack {
                Text("Dashboard")
                    .font(.largeTitle)
                Text("Slot-injected widgets appear here")
                    .foregroundStyle(.secondary)
            }
            .navigationTitle("Dashboard")
        }
    }
}
