# Android — LargeScaleApp

This folder contains the Android implementation of the LargeScaleApp modular architecture.

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK API 35

## Setup

1. Open `android/` in Android Studio (File → Open → select this folder)
2. Android Studio will sync Gradle automatically
3. Update `local.properties` with your SDK path if needed:
   ```properties
   sdk.dir=/Users/<username>/Library/Android/sdk
   ```

## Build & Run

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run on connected device/emulator
./gradlew installDebug
```

## Module Structure

```
android/
├── app/              # App shell — MainActivity, Application class, DI setup
├── wire/             # Wire Core — ModuleRegistry, EventBus, SlotRegistry, Navigation
├── contracts/        # Shared interfaces — AppModule, AppNavigator, AppRoute, Role
├── core/             # Always-loaded services — Auth, Network, Storage
├── shared-ui/        # Design system — Compose components, theme
├── feature-core/     # Feature Core module
├── feature-dashboard/# Dashboard feature
├── feature-orders/   # Orders feature
├── feature-inventory/# Inventory feature
└── feature-wallet/   # Wallet feature
```

## Architecture

See the shared [architecture guide](../modular_mobile_architecture_principal_guide.md) and [docs/ai/](../docs/ai/) for full documentation.

Key patterns:
- **Wire Core** orchestrates module lifecycle
- **ModuleRegistry** resolves modules by user role
- **AppEventBus** (Kotlin Flow) enables decoupled inter-module communication
- **SlotRegistry** enables widget injection into host screens
- **Hilt** for dependency injection
- **Jetpack Compose** for UI
- **Type-safe Navigation** with Kotlin serialization
