# LargeScaleApp — Multi-Platform Modular Architecture

A production-ready **multi-platform monorepo** demonstrating the same **plug-and-play, tenant-configurable, role-aware** module system applied to Android, Flutter, and iOS.

## Platform Folders

| Platform | Folder | Tooling |
|----------|--------|---------|
| Android | [android/](android/) | Gradle · Kotlin · Jetpack Compose · Hilt |
| Flutter | [flutter/](flutter/) | Flutter SDK · Dart · go_router · get_it |
| iOS | [ios/](ios/) | Xcode · Swift · SwiftUI · SPM · Combine |

Each platform is **independently buildable** and applies the same architectural principles: Wire Core, Module Registry, Event Bus, Slot Registry, Role-Based Module Loading.

See [modular_mobile_architecture_principal_guide.md](modular_mobile_architecture_principal_guide.md) for the platform-agnostic architecture reference.

---

## Android Architecture Overview

A production-ready Android template demonstrating a **plug-and-play, tenant-configurable, role-aware** module system built on Jetpack Compose, Hilt, and Kotlin Coroutines.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Module Map](#module-map)
3. [Dependency Rules](#dependency-rules)
4. [Quick Start](#quick-start)
5. [Key Concepts](#key-concepts)
6. [Developer Guides](#developer-guides)
7. [Test Accounts](#test-accounts)

---

## Architecture Overview

The app is split into **three layers**:

```
┌─────────────────────────────────────────────┐
│                  :app                        │  Shell — wires everything together
├──────────────┬──────────────────────────────┤
│   :feature-* │  :shared-ui                  │  Feature modules + Design system
├──────────────┴──────────────────────────────┤
│   :wire   │   :core                          │  Orchestration + Core services
├───────────┴─────────────────────────────────┤
│                :contracts                    │  Interfaces only — zero implementation
└─────────────────────────────────────────────┘
```

### Wire Core (`:wire`)

The central nervous system. Every feature module talks to the rest of the app only through Wire Core services — there are **no direct dependencies between feature modules**.

```
┌─────────────────── Wire Core ───────────────────────┐
│  ModuleRegistry   — registers & resolves modules     │
│  FlowEventBus     — cross-module publish/subscribe   │
│  SlotRegistry     — dynamic UI composition           │
│  AppNavigatorImpl — single NavHostController wrapper │
│  RoleManager      — tracks current user role         │
│  TenantResolverImpl — multi-tenant config            │
└──────────────────────────────────────────────────────┘
```

### Feature Module Lifecycle

```
App.onCreate()
  └─ registerModules(all modules)
       └─ ModuleRegistry.resolve(role, tenant)
            └─ filtered & sorted modules
                 └─ module.initialize(ModuleContext)
                      ├─ register UISlots → SlotRegistry
                      ├─ subscribe to events → EventBus
                      └─ expose routes → NavHost
```

---

## Module Map

| Module | Type | Depends On | Purpose |
|--------|------|-----------|---------|
| `:contracts` | android-library | — | Interfaces, data classes, event types |
| `:wire` | android-library | `:contracts` | Module registry, event bus, navigation, slot registry |
| `:core` | android-library | `:contracts` | Auth, network (Retrofit), storage (DataStore) |
| `:shared-ui` | android-library | `:contracts` | Design system, reusable Compose components |
| `:feature-core` | android-library | `:contracts`, `:core`, `:shared-ui` | Login, splash, auth flows |
| `:feature-dashboard` | android-library | `:contracts`, `:shared-ui` | Home screen (slot host) |
| `:feature-orders` | android-library | `:contracts`, `:shared-ui` | Orders list + detail + summary widget |
| `:feature-inventory` | android-library | `:contracts`, `:shared-ui` | Inventory (skeleton) |
| `:feature-wallet` | android-library | `:contracts`, `:shared-ui` | Wallet (skeleton) |
| `:app` | android-application | all | App shell, Hilt entry point, NavHost |

---

## Dependency Rules

```
:contracts  ←  :wire
:contracts  ←  :core
:contracts  ←  :shared-ui
:contracts  ←  :feature-*
:shared-ui  ←  :feature-*
:core       ←  :feature-core  (auth service)
:wire       ←  :app           (navigation setup)

FORBIDDEN:
  :core     →  :wire   (no circular dep)
  :feature-A →  :feature-B  (modules never import each other)
```

> Rule of thumb: if module A needs something from module B, make it an event or a contract interface — not a direct dependency.

---

## Quick Start

### Prerequisites

- Android Studio Hedgehog (2023.1+)
- JDK 17
- Android SDK 26–36

### Build

```bash
git clone <repo-url>
cd LargeScaleModule
./gradlew assembleDebug
```

### Run tests

```bash
# Wire core unit tests
./gradlew :wire:test

# Core module unit tests
./gradlew :core:test

# All unit tests
./gradlew test
```

---

## Key Concepts

### AppModule interface

Every feature module implements this contract:

```kotlin
interface AppModule {
    val metadata: ModuleMetadata   // id, name, roles, priority, tenants
    fun initialize(context: ModuleContext)  // register slots, subscribe events
    fun provideRoutes(): List<ModuleRoute>
    fun provideWidgets(): List<UISlot>
}
```

### Role-based access

Roles in ascending privilege order: `GUEST < CUSTOMER < STAFF < ADMIN`

A user with role R can access any resource whose `requiredRole.ordinal >= R.ordinal`.

```kotlin
// ADMIN (ordinal 0) can see everything
// GUEST (ordinal 3) can only see GUEST-required resources
userRole.ordinal <= resource.requiredRole.ordinal
```

### Multi-tenant filtering

`TenantConfig.enabledModules` acts as an allowlist. A module is visible only if:
1. Its `requiredRoles` includes the current user's role **AND**
2. The current tenant has enabled it (or `enabledModules` is empty = all)

### SlotRegistry — dynamic UI composition

Modules push `UISlot` composables into named slots during `initialize()`. The host screen (e.g., HomeScreen) renders whatever is registered:

```kotlin
// OrdersFeatureModule.initialize()
context.slotRegistry.register(UISlot(
    slotId   = SlotIds.HOME_WIDGETS,
    widgetId = "orders-summary",
    moduleId = "orders",
    priority = 800,
    requiredRole = Role.STAFF,
    content  = { OrdersSummaryWidget() }
))

// HomeScreen.kt
val widgets = slotRegistry.getSlotsForHost(SlotIds.HOME_WIDGETS, currentRole)
LazyColumn { items(widgets) { slot -> slot.content() } }
```

### EventBus — cross-module communication

Publish and subscribe without direct module coupling:

```kotlin
// Publish (from any module)
eventBus.publish(OrderCreatedEvent(orderId = "ORD-001"))

// Subscribe (from any other module)
eventBus.on<OrderCreatedEvent> { event ->
    refreshBadge(event.orderId)
}
```

---

## Developer Guides

| Guide | Description |
|-------|-------------|
| [01 — Creating a New Module](docs/guides/01-new-module.md) | Step-by-step: add a feature module from scratch |
| [02 — Adding Widgets/Slots](docs/guides/02-widgets.md) | Contribute UI to host screens via SlotRegistry |
| [03 — Cross-Module Events](docs/guides/03-events.md) | Publish and subscribe to events via EventBus |
| [04 — Testing Modules](docs/guides/04-testing.md) | Unit and integration test patterns |

---

## Test Accounts

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin` | ADMIN — sees all modules |
| `staff` | `staff` | STAFF — sees orders, inventory |
| `customer` | `customer` | CUSTOMER — sees wallet, dashboard |
| `guest` | `guest` | GUEST — dashboard only |
