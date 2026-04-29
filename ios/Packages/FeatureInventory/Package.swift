// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "FeatureInventory",
    platforms: [.iOS(.v16)],
    products: [.library(name: "FeatureInventory", targets: ["FeatureInventory"])],
    dependencies: [
        .package(path: "../Contracts"),
        .package(path: "../WireCore"),
        .package(path: "../SharedUI"),
    ],
    targets: [
        .target(name: "FeatureInventory", dependencies: ["Contracts", "WireCore", "SharedUI"], path: "Sources/FeatureInventory"),
    ]
)
