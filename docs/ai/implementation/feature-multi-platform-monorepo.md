---
phase: implementation
title: Implementation Guide
description: Technical implementation notes for the multi-platform monorepo restructure
feature: multi-platform-monorepo
---

# Implementation Guide

## Development Setup

### Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Android Studio | Hedgehog+ | Android development |
| Flutter SDK | ≥ 3.22 | Flutter development |
| Dart SDK | ≥ 3.3 (bundled) | Flutter Dart runtime |
| Xcode | ≥ 15 | iOS development |
| iOS SDK | ≥ 16 | iOS target |
| melos (optional) | latest | Flutter multi-package workspace |

---

## Phase 1: Android Move — Step-by-Step

### 1. Create the android/ directory and move files

```bash
# From repo root
mkdir android

# Move all Gradle modules
mv app android/
mv core android/
mv contracts android/
mv wire android/
mv feature-core android/
mv feature-dashboard android/
mv feature-inventory android/
mv feature-orders android/
mv feature-wallet android/
mv shared-ui android/

# Move Gradle infrastructure
mv build.gradle.kts android/
mv settings.gradle.kts android/
mv gradle.properties android/
mv gradlew android/
mv gradlew.bat android/
mv gradle android/
mv local.properties android/
```

### 2. Update android/settings.gradle.kts

No path changes needed — all modules are still relative to the settings file which moved with them. Verify that `include(":app")`, `include(":core")`, etc. still resolve correctly.

### 3. Update android/local.properties

`local.properties` contains the absolute path to the Android SDK. After moving, the path itself doesn't change — only verify it still points to a valid SDK installation:

```properties
sdk.dir=/Users/<username>/Library/Android/sdk
```

### 4. Verify Android build

```bash
cd android
./gradlew assembleDebug
```

---

## Phase 2: Flutter Scaffold — Step-by-Step

### Directory & package creation order

1. `contracts/` → no dependencies
2. `core/` → depends on `contracts/`
3. `wire/` → depends on `contracts/`, `core/`
4. `shared_ui/` → depends on `contracts/`
5. `feature_*/` → depends on `contracts/`, `core/`, `wire/`, `shared_ui/`
6. `app/` → depends on all of the above

### Wire Core (`flutter/wire/`)

Key files to implement:

**`lib/src/module_registry.dart`**
```dart
import 'package:contracts/contracts.dart';

class ModuleRegistry {
  final List<AppModule> _modules;

  ModuleRegistry(this._modules);

  List<AppModule> resolve(Role role) {
    return _modules.where((m) => m.supportedRoles().contains(role)).toList();
  }
}
```

**`lib/src/event_bus.dart`**
```dart
import 'dart:async';

class AppEventBus {
  final _controller = StreamController<Object>.broadcast();

  Stream<T> on<T>() => _controller.stream.whereType<T>();

  void publish(Object event) => _controller.add(event);

  void dispose() => _controller.close();
}
```

**`lib/src/slot_registry.dart`**
```dart
import 'package:flutter/widgets.dart';

class SlotRegistry {
  final _slots = <String, List<Widget Function()>>{};

  void register(String slotId, Widget Function() builder) {
    _slots.putIfAbsent(slotId, () => []).add(builder);
  }

  List<Widget> resolve(String slotId) {
    return (_slots[slotId] ?? []).map((b) => b()).toList();
  }
}
```

**`lib/src/wire_container.dart`** (get_it wrapper)
```dart
import 'package:get_it/get_it.dart';

class WireContainer {
  final GetIt _locator = GetIt.instance;

  void register<T extends Object>(T instance) {
    if (!_locator.isRegistered<T>()) {
      _locator.registerSingleton<T>(instance);
    }
  }

  T resolve<T extends Object>() => _locator<T>();
}
```

### Contracts (`flutter/contracts/`)

**`lib/src/app_module.dart`**
```dart
import 'app_route.dart';
import 'role.dart';
import '../wire_container.dart';  // imported from wire package

abstract class AppModule {
  String get id;
  List<Role> supportedRoles();
  void register(WireContainer container);
  List<AppRoute> routes();
}
```

**`lib/src/role.dart`**
```dart
enum Role { admin, staff, customer, guest }
```

**`lib/src/app_route.dart`**
```dart
sealed class AppRoute {
  final String path;
  const AppRoute(this.path);
}

class DashboardRoute extends AppRoute {
  const DashboardRoute() : super('/dashboard');
}

class OrdersRoute extends AppRoute {
  const OrdersRoute() : super('/orders');
}
```

### Feature Module Pattern (`flutter/feature_orders/`)

```
feature_orders/
├── lib/
│   ├── src/
│   │   ├── presentation/
│   │   │   ├── orders_screen.dart      # Stateful BLoC/Riverpod screen
│   │   │   └── orders_bloc.dart        # Business logic / state
│   │   ├── domain/
│   │   │   ├── orders_use_case.dart
│   │   │   └── order_model.dart
│   │   ├── data/
│   │   │   ├── orders_repository.dart       # Abstract
│   │   │   └── orders_repository_impl.dart  # Stub implementation
│   │   └── module/
│   │       └── orders_module.dart           # AppModule implementation
│   └── feature_orders.dart                  # Barrel export
└── pubspec.yaml
```

**`lib/src/module/orders_module.dart`**
```dart
import 'package:contracts/contracts.dart';
import 'package:wire/wire.dart';
import '../presentation/orders_screen.dart';

class OrdersModule implements AppModule {
  @override
  String get id => 'orders';

  @override
  List<Role> supportedRoles() => [Role.admin, Role.staff];

  @override
  void register(WireContainer container) {
    container.register<OrdersRepository>(OrdersRepositoryImpl());
  }

  @override
  List<AppRoute> routes() => [OrdersRoute()];
}
```

### App Shell (`flutter/app/lib/main.dart`)

```dart
import 'package:flutter/material.dart';
import 'package:wire/wire.dart';
import 'package:contracts/contracts.dart';
import 'package:feature_dashboard/feature_dashboard.dart';
import 'package:feature_orders/feature_orders.dart';

void main() {
  final container = WireContainer();
  final eventBus = AppEventBus();
  final slotRegistry = SlotRegistry();

  final registry = ModuleRegistry([
    DashboardModule(),
    OrdersModule(),
    // InventoryModule(), WalletModule() ...
  ]);

  // Demo: load as ADMIN
  final modules = registry.resolve(Role.admin);
  for (final module in modules) {
    module.register(container);
  }

  runApp(MyApp(modules: modules));
}
```

### pubspec.yaml for a feature package

```yaml
name: feature_orders
description: Orders feature module
version: 0.1.0
publish_to: none

environment:
  sdk: '>=3.3.0 <4.0.0'
  flutter: '>=3.22.0'

dependencies:
  flutter:
    sdk: flutter
  contracts:
    path: ../contracts
  wire:
    path: ../wire
  core:
    path: ../core
  shared_ui:
    path: ../shared_ui
  get_it: ^7.7.0

dev_dependencies:
  flutter_test:
    sdk: flutter
  mocktail: ^1.0.4
```

---

## Phase 3: iOS Scaffold — Step-by-Step

### Package creation order

1. `Contracts/` → no dependencies
2. `WireCore/` → depends on `Contracts`
3. `Core/` → depends on `Contracts`, `WireCore`
4. `SharedUI/` → depends on `Contracts`
5. `Feature*/` → depends on all above
6. `App/` → depends on all packages

### Contracts (`ios/Packages/Contracts/Package.swift`)

```swift
// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Contracts",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "Contracts", targets: ["Contracts"])
    ],
    targets: [
        .target(name: "Contracts", path: "Sources/Contracts")
    ]
)
```

**`Sources/Contracts/AppModule.swift`**
```swift
import Foundation

public protocol AppModule {
    var id: String { get }
    func supportedRoles() -> [Role]
    func register(container: WireContainer)
    func routes() -> [AppRoute]
}
```

**`Sources/Contracts/Role.swift`**
```swift
public enum Role {
    case admin, staff, customer, guest
}
```

**`Sources/Contracts/AppRoute.swift`**
```swift
public enum AppRoute: Hashable {
    case dashboard
    case orders
    case inventory
    case wallet
}
```

### WireCore (`ios/Packages/WireCore/`)

**`Sources/WireCore/ModuleRegistry.swift`**
```swift
import Contracts

public final class ModuleRegistry {
    private let modules: [any AppModule]

    public init(modules: [any AppModule]) {
        self.modules = modules
    }

    public func resolve(role: Role) -> [any AppModule] {
        modules.filter { $0.supportedRoles().contains(role) }
    }
}
```

**`Sources/WireCore/AppEventBus.swift`**
```swift
import Combine

public final class AppEventBus {
    private let subject = PassthroughSubject<Any, Never>()

    public func publish<T>(_ event: T) {
        subject.send(event)
    }

    public func on<T>(_ type: T.Type) -> AnyPublisher<T, Never> {
        subject.compactMap { $0 as? T }.eraseToAnyPublisher()
    }
}
```

**`Sources/WireCore/SlotRegistry.swift`**
```swift
import SwiftUI

public final class SlotRegistry {
    private var slots: [String: [AnyView]] = [:]

    public func register<V: View>(slotId: String, view: V) {
        slots[slotId, default: []].append(AnyView(view))
    }

    public func resolve(slotId: String) -> [AnyView] {
        slots[slotId] ?? []
    }
}
```

### Feature Module Pattern (iOS)

```
FeatureOrders/
└── Sources/FeatureOrders/
    ├── Presentation/
    │   ├── OrdersView.swift          # SwiftUI View
    │   └── OrdersViewModel.swift     # ObservableObject
    ├── Domain/
    │   ├── OrdersUseCase.swift
    │   └── Order.swift               # Domain model
    ├── Data/
    │   ├── OrdersRepository.swift    # Protocol
    │   └── OrdersRepositoryImpl.swift
    └── Module/
        └── OrdersModule.swift        # AppModule conformance
```

**`Module/OrdersModule.swift`**
```swift
import Contracts
import WireCore

public final class OrdersModule: AppModule {
    public var id: String { "orders" }

    public init() {}

    public func supportedRoles() -> [Role] { [.admin, .staff] }

    public func register(container: WireContainer) {
        container.register(OrdersRepositoryImpl() as OrdersRepository)
    }

    public func routes() -> [AppRoute] { [.orders] }
}
```

### App Shell (`ios/App/ContentView.swift`)

```swift
import SwiftUI
import WireCore
import Contracts
import FeatureDashboard
import FeatureOrders

@main
struct LargeScaleApp: App {
    private let container = WireContainer()
    private let eventBus = AppEventBus()
    private let slotRegistry = SlotRegistry()

    init() {
        let registry = ModuleRegistry(modules: [
            DashboardModule(),
            OrdersModule(),
        ])
        let modules = registry.resolve(role: .admin)
        modules.forEach { $0.register(container: container) }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

---

## Integration Points

- **Android → contracts**: All feature modules depend on `:contracts` Gradle module
- **Flutter → contracts**: All Flutter packages depend on local `contracts/` package via `path:`
- **iOS → Contracts**: All SPM packages list `Contracts` as a dependency in `Package.swift`
- **Event Bus**: Modules never call each other directly; always publish/subscribe via the bus
- **Slot Registry**: Feature modules register widgets in `register(container:)` — host screens call `slotRegistry.resolve(slotId:)` to render injected UI

## Error Handling

- Feature modules must handle null/empty data gracefully; show empty state UI
- `WireContainer` should throw (Dart) / `fatalError` (Swift) on unregistered type resolution in debug; return nil in release
- Event bus errors must not crash the app — use `try/catch` in Dart, `sink` error handling in Combine

## Security Notes

- No credentials in source files; use `.env` / `BuildConfig` / `Info.plist` for secrets
- Role resolution must come from a trusted auth source (never from client-side preference only)
- SPM and pub.dev packages should be reviewed for known CVEs before adding
