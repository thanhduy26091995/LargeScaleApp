import SwiftUI

public struct RatingView: View {
    public let rating: Double
    public let maxRating: Int

    public init(rating: Double, maxRating: Int = 5) {
        self.rating = rating
        self.maxRating = maxRating
    }

    public var body: some View {
        HStack(spacing: 2) {
            ForEach(0..<maxRating, id: \.self) { index in
                Image(systemName: imageName(for: index))
                    .foregroundStyle(.yellow)
                    .font(.caption)
            }
        }
    }

    private func imageName(for index: Int) -> String {
        let filled = Double(index) < rating.rounded(.down)
        let half = !filled && Double(index) < rating
        if filled { return "star.fill" }
        if half { return "star.leadinghalf.filled" }
        return "star"
    }
}
