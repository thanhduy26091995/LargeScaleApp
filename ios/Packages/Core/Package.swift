// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Core",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "Core", targets: ["Core"]),
    ],
    dependencies: [
        .package(path: "../Contracts"),
        .package(path: "../WireCore"),
    ],
    targets: [
        .target(
            name: "Core",
            dependencies: ["Contracts", "WireCore"],
            path: "Sources/Core"
        ),
    ]
)
