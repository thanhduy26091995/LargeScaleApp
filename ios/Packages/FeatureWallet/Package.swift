// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "FeatureWallet",
    platforms: [.iOS(.v16)],
    products: [.library(name: "FeatureWallet", targets: ["FeatureWallet"])],
    dependencies: [
        .package(path: "../Contracts"),
        .package(path: "../WireCore"),
        .package(path: "../SharedUI"),
    ],
    targets: [
        .target(name: "FeatureWallet", dependencies: ["Contracts", "WireCore", "SharedUI"], path: "Sources/FeatureWallet"),
    ]
)
