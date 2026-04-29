// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Contracts",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "Contracts", targets: ["Contracts"]),
    ],
    targets: [
        .target(
            name: "Contracts",
            path: "Sources/Contracts"
        ),
    ]
)
