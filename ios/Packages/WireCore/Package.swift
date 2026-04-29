// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "WireCore",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "WireCore", targets: ["WireCore"]),
    ],
    dependencies: [
        .package(path: "../Contracts"),
    ],
    targets: [
        .target(
            name: "WireCore",
            dependencies: ["Contracts"],
            path: "Sources/WireCore"
        ),
        .testTarget(
            name: "WireCoreTests",
            dependencies: ["WireCore"],
            path: "Tests/WireCoreTests"
        ),
    ]
)
