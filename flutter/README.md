# LargeScaleApp Flutter

Flutter implementation of the LargeScaleApp modular architecture.

## Prerequisites

- Flutter SDK ≥ 3.22 ([install](https://docs.flutter.dev/get-started/install))
- Dart ≥ 3.3 (bundled with Flutter)

## Package Structure

```
flutter/
├── app/              # App shell — main.dart, bootstraps Wire Core
├── wire/             # Wire Core — ModuleRegistry, EventBus, SlotRegistry, WireContainer
├── contracts/        # Shared contracts — AppModule, AppNavigator, AppRoute, Role
├── core/             # Always-loaded services — Auth, Network, Storage
├── shared_ui/        # Design system — widgets and theme
├── feature_dashboard/# Dashboard feature
├── feature_orders/   # Orders feature
├── feature_inventory/# Inventory feature
└── feature_wallet/   # Wallet feature
```

## Build & Run

```bash
# Navigate to the app shell
cd flutter/app

# Get dependencies for all packages (run once from each package, or use melos)
flutter pub get

# Run on connected device/simulator
flutter run

# Run unit tests (per package)
flutter test
```

## Using Melos (optional — recommended for workspace management)

```bash
# Install melos globally
dart pub global activate melos

# From flutter/ root
melos bootstrap   # installs deps for all packages
melos run test    # runs tests across all packages
```

## Architecture

See the shared [architecture guide](../modular_mobile_architecture_principal_guide.md).

Key patterns:
- **Wire Core** orchestrates module lifecycle
- **ModuleRegistry** resolves modules by user role
- **AppEventBus** (Dart Stream) enables decoupled inter-module communication
- **SlotRegistry** enables widget injection into host screens
- **get_it** for dependency injection
- **go_router** for navigation
- **Material 3** for UI
