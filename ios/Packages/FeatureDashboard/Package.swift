// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "FeatureDashboard",
    platforms: [.iOS(.v16)],
    products: [.library(name: "FeatureDashboard", targets: ["FeatureDashboard"])],
    dependencies: [
        .package(path: "../Contracts"),
        .package(path: "../WireCore"),
        .package(path: "../SharedUI"),
    ],
    targets: [
        .target(name: "FeatureDashboard", dependencies: ["Contracts", "WireCore", "SharedUI"], path: "Sources/FeatureDashboard"),
    ]
)
