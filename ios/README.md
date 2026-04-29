# iOS — LargeScaleApp

iOS Swift implementation of the LargeScaleApp modular architecture.

## Prerequisites

- Xcode 15+
- iOS 16+ deployment target
- macOS Ventura 13+ (for building)

## Package Structure

```
ios/
├── App/                     # App shell — LargeScaleApp.swift, ContentView.swift
└── Packages/                # Swift Package Manager local packages
    ├── Contracts/           # Protocols — AppModule, AppNavigator, AppRoute, Role
    ├── WireCore/            # Wire Core — ModuleRegistry, AppEventBus, SlotRegistry, WireContainer
    ├── Core/                # Always-loaded services — Auth, Network, Storage
    ├── SharedUI/            # Design system — SwiftUI components and theme
    ├── FeatureDashboard/    # Dashboard feature
    ├── FeatureOrders/       # Orders feature
    ├── FeatureInventory/    # Inventory feature
    └── FeatureWallet/       # Wallet feature
```

## Setup

1. Open `ios/App/` in Xcode (or create an Xcode project pointing to this folder)
2. Add local Swift packages:
   - File → Add Package Dependencies → Add Local → select each folder under `ios/Packages/`
   - Or reference them in `Package.swift` with `.package(path: "../Packages/<Name>")`
3. Build and run on simulator

## Build & Run

```bash
# Open Xcode project
open ios/App/App.xcodeproj

# Build from command line
xcodebuild -project ios/App/App.xcodeproj -scheme App -destination 'platform=iOS Simulator,name=iPhone 15' build
```

## Running Tests

```bash
xcodebuild test -project ios/App/App.xcodeproj -scheme App -destination 'platform=iOS Simulator,name=iPhone 15'
```

## Architecture

See the shared [architecture guide](../modular_mobile_architecture_principal_guide.md).

Key patterns:
- **WireCore** orchestrates module lifecycle
- **ModuleRegistry** resolves modules by user role
- **AppEventBus** (Combine `PassthroughSubject`) enables decoupled inter-module communication
- **SlotRegistry** enables SwiftUI view injection into host screens
- **WireContainer** (lightweight dictionary-based DI, swap for Swinject in production)
- **SwiftUI** + **NavigationStack** for UI and navigation
- **ObservableObject** + **@Published** for state management
