// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "FeatureOrders",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "FeatureOrders", targets: ["FeatureOrders"]),
    ],
    dependencies: [
        .package(path: "../Contracts"),
        .package(path: "../WireCore"),
        .package(path: "../Core"),
        .package(path: "../SharedUI"),
    ],
    targets: [
        .target(
            name: "FeatureOrders",
            dependencies: ["Contracts", "WireCore", "Core", "SharedUI"],
            path: "Sources/FeatureOrders"
        ),
        .testTarget(
            name: "FeatureOrdersTests",
            dependencies: ["FeatureOrders"],
            path: "Tests/FeatureOrdersTests"
        ),
    ]
)
