import SwiftUI

public struct ProductCardView: View {
    public let title: String
    public let subtitle: String
    public let action: (() -> Void)?

    public init(title: String, subtitle: String, action: (() -> Void)? = nil) {
        self.title = title
        self.subtitle = subtitle
        self.action = action
    }

    public var body: some View {
        Button(action: { action?() }) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(title).font(AppTheme.titleFont)
                    Text(subtitle).font(AppTheme.captionFont).foregroundStyle(.secondary)
                }
                Spacer()
                Image(systemName: "chevron.right").foregroundStyle(.secondary)
            }
            .padding()
            .background(AppTheme.surfaceColor)
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .buttonStyle(.plain)
    }
}
