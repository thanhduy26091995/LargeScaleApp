import SwiftUI

public struct AvatarView: View {
    public let initials: String
    public let size: CGFloat

    public init(initials: String, size: CGFloat = 40) {
        self.initials = initials
        self.size = size
    }

    public var body: some View {
        Circle()
            .fill(AppTheme.primaryColor)
            .frame(width: size, height: size)
            .overlay(
                Text(initials)
                    .font(.system(size: size * 0.4, weight: .semibold))
                    .foregroundStyle(.white)
            )
    }
}
