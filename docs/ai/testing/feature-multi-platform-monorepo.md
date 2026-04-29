---
phase: testing
title: Testing Strategy
description: Testing approach for multi-platform monorepo restructure (Android, Flutter, iOS)
feature: multi-platform-monorepo
---

# Testing Strategy

## Test Coverage Goals

- **Android**: All existing tests must pass after the move — zero regressions
- **Flutter**: Wire Core unit tests 100%; feature module use cases unit tested
- **iOS**: Wire Core unit tests 100%; feature module use cases unit tested
- **Integration**: Each platform's app shell boots and resolves modules correctly for each role

---

## Unit Tests

### Android — Post-Move Regression

- [ ] **Wire Core**: `ModuleRegistry.resolve()` returns correct modules per role
- [ ] **Wire Core**: `AppEventBus` delivers events to subscribers
- [ ] **Wire Core**: `SlotRegistry.resolve()` returns registered widgets for a slot
- [ ] **Feature modules**: All existing use case tests pass without modification
- [ ] **Build**: `./gradlew test` from `android/` passes with no failures

### Flutter — Wire Core

- [ ] `ModuleRegistry.resolve(Role.admin)` returns only admin-enabled modules
- [ ] `ModuleRegistry.resolve(Role.guest)` returns only guest-accessible modules
- [ ] `AppEventBus.on<T>()` receives published events of type T
- [ ] `AppEventBus` does not deliver events of the wrong type
- [ ] `SlotRegistry.register()` + `SlotRegistry.resolve()` return correct widgets
- [ ] `WireContainer.register()` + `WireContainer.resolve<T>()` round-trip correctly
- [ ] `WireContainer.resolve<T>()` for unregistered type throws `StateError` in debug

### Flutter — Feature Modules

- [ ] `OrdersModule.supportedRoles()` returns `[Role.admin, Role.staff]`
- [ ] `OrdersModule.register()` registers `OrdersRepository` into container
- [ ] `OrdersModule.routes()` returns `[OrdersRoute()]`
- [ ] `OrdersUseCase` returns stub data from `OrdersRepository`
- [ ] Repeat for `DashboardModule`, `InventoryModule`, `WalletModule`

### iOS — Wire Core

- [ ] `ModuleRegistry.resolve(role: .admin)` returns admin modules only
- [ ] `AppEventBus.publish()` delivers event via `on(_:)` publisher
- [ ] `AppEventBus` does not emit for unrelated event types
- [ ] `SlotRegistry.register(slotId:view:)` + `resolve(slotId:)` returns registered views
- [ ] `WireContainer` resolves registered services correctly

### iOS — Feature Modules

- [ ] `OrdersModule.supportedRoles()` returns `[.admin, .staff]`
- [ ] `OrdersModule.register(container:)` registers `OrdersRepository` conforming type
- [ ] `OrdersModule.routes()` returns `[.orders]`
- [ ] `OrdersViewModel` publishes expected initial state
- [ ] Repeat for Dashboard, Inventory, Wallet modules

---

## Integration Tests

- [ ] **Android**: `./gradlew connectedAndroidTest` passes from `android/`
- [ ] **Flutter**: App shell initializes `WireContainer`, loads all admin modules, builds `go_router` navigation without error
- [ ] **iOS**: App builds and launches in simulator; root `ContentView` renders without crash for `.admin` role
- [ ] **Cross-role loading**: For each platform, verify modules for `Role.guest` do NOT include admin-only features

---

## End-to-End Tests

- [ ] **Android**: Launch app → see Dashboard screen (role: admin) — existing E2E still passes
- [ ] **Flutter**: `flutter drive` or manual — app launches, bottom nav shows Dashboard, Orders tabs
- [ ] **iOS**: Xcode UI Test — app launches, shows Dashboard and Orders in navigation

---

## Test Data

- Stub implementations provided for all Repository interfaces
- `Role` selection exposed via a debug toggle on the home screen for manual testing
- No real network calls in scaffold; all repositories return hardcoded stub data

---

## Test Reporting & Coverage

**Android**:
```bash
cd android && ./gradlew koverHtmlReport
# Open android/build/reports/kover/html/index.html
```

**Flutter**:
```bash
cd flutter
# Run for all packages
melos run test
# Or per package:
cd wire && flutter test --coverage
genhtml coverage/lcov.info -o coverage/html
```

**iOS**:
```
Product → Test in Xcode (⌘U)
# Coverage visible in Xcode Report Navigator
```

---

## Manual Testing

- [ ] **Android**: Open `android/` in Android Studio → sync Gradle → run on emulator → app launches
- [ ] **Flutter**: `cd flutter/app && flutter run` → app launches with bottom navigation
- [ ] **iOS**: Open `ios/App/App.xcodeproj` → run on iPhone simulator → app launches
- [ ] **Role switching**: On each platform, switch role in the demo UI → verify correct modules load
- [ ] **Event Bus**: Trigger an event from one module → verify another module's UI updates

---

## Bug Tracking

- Any build failure post-Android-move is Priority 1 — block on resolution before Flutter/iOS work
- Architectural deviations (module importing another module directly) flagged as architecture violations
- Use GitHub Issues with label `platform: android`, `platform: flutter`, `platform: ios`
