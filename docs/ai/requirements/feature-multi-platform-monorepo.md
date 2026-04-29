---
phase: requirements
title: Requirements & Problem Understanding
description: Restructure monorepo to support Android, Flutter, and iOS with the same modular architecture
feature: multi-platform-monorepo
---

# Requirements & Problem Understanding

## Terminology & Key Concepts

**Core Terms:**
- **Monorepo**: A single repository containing source code for all platforms (Android, Flutter, iOS)
- **Platform Folder**: Top-level directory for each platform (`android/`, `flutter/`, `ios/`)
- **Wire Core**: Infrastructure layer that orchestrates module lifecycle, communication, and composition — translated to each platform's idioms
- **Module Registry**: Central registry that discovers, manages, and provides access to all feature modules — per platform
- **Contracts**: Shared interfaces/protocols that define how modules communicate
- **Feature Module**: Self-contained unit with presentation/domain/data layers implementing a business capability

**Platform-specific DI Tools:**
| Platform | DI Tool |
|----------|---------|
| Android  | Hilt / Koin |
| iOS      | Swinject |
| Flutter  | get_it |

**Platform-specific Navigation:**
| Platform | Navigation Tool |
|----------|----------------|
| Android  | Jetpack Navigation / type-safe routes |
| iOS      | Coordinator pattern via UIHostingController / NavigationStack |
| Flutter  | go_router |

**State Management:**
| Platform | Approach |
|----------|---------|
| Android  | ViewModel + StateFlow |
| iOS      | ObservableObject + @Published (SwiftUI) |
| Flutter  | StatefulWidget + FutureBuilder (scaffold); Riverpod recommended for production |

## Problem Statement
**What problem are we solving?**

- The current LargeScaleApp repository is Android-only, with a production-level modular architecture (Wire Core, Module Registry, Event Bus, Widget Slots, Role-Based Loading) already implemented.
- The product vision covers **three platforms** (Android, Flutter, iOS), all needing the same modular, scalable architecture.
- Without a monorepo structure, the architecture knowledge, conventions, and contracts are fragmented across separate repositories, making it difficult to keep platforms in sync.
- Developers on Flutter and iOS teams lack a starting scaffold that matches the architectural contracts defined in `modular_mobile_architecture_principal_guide.md`.

**Who is affected?**
- Android, Flutter, and iOS development teams
- Tech leads and architects maintaining architectural consistency
- Product managers expecting feature parity across platforms
- QA teams that need to validate the same feature behaviour on all three platforms

**Current situation:**
- Only the `android/` equivalent (current root) is implemented with the full modular architecture
- Flutter and iOS platforms have no scaffold
- No monorepo structure separating platforms cleanly

## Goals & Objectives
**What do we want to achieve?**

### Primary Goals
- Restructure the repository root to contain three top-level platform directories: `android/`, `flutter/`, `ios/`
- Move all existing Android source files (Gradle modules, build scripts, source sets) into `android/`
- Create a Flutter project scaffold under `flutter/` that mirrors the Android module structure
- Create an iOS project scaffold under `ios/` that mirrors the Android module structure
- Apply the same architectural principles (Wire Core, Module Registry, Event Bus, Widget Slots, Role-Based Loading) to each platform using idiomatic tooling
- Maintain a single `docs/` directory at the repo root for shared architecture documentation

### Secondary Goals
- Create `README.md` files for each platform folder explaining how to build and run
- Keep shared architecture contracts documented in the existing `docs/ai/` structure
- Provide reference implementations of the same feature modules (dashboard, orders, inventory, wallet) for each platform to demonstrate architectural parity

### Non-Goals (Out of Scope)
- Sharing business logic between platforms via KMP or other cross-platform code sharing
- CI/CD pipeline configuration
- App store deployment scripts
- Full feature implementation (only scaffold/skeleton modules)
- Backend API changes

## User Stories & Use Cases

### Developer Stories
- As an **Android developer**, I want all Android source to live under `android/` so the platform boundary is clear
- As a **Flutter developer**, I want a Flutter project under `flutter/` with the same module structure (wire, core, contracts, feature modules) as Android
- As an **iOS developer**, I want an iOS Swift project under `ios/` with the same module boundaries as Android but using Swift Package Manager
- As a **tech lead**, I want every platform to implement the same `AppModule` contract (adapted to each language) so architectural patterns are consistent
- As a **developer joining the team**, I want platform folders to be immediately recognizable and independently buildable
- As an **architect**, I want the `modular_mobile_architecture_principal_guide.md` to remain the single source of truth for all three platforms

### Workflow Scenarios
- Developer clones repo, navigates to `android/`, opens in Android Studio and builds — works out of the box
- Developer navigates to `flutter/`, runs `flutter pub get && flutter run` — works out of the box
- Developer navigates to `ios/`, opens `ios/App/App.xcodeproj` in Xcode — builds out of the box (note: `.xcodeproj` must be created in Xcode pointing to `ios/App/` with local SPM packages from `ios/Packages/`)
- Adding a new feature module follows the same conceptual steps on all three platforms (see `docs/guides/01-new-module.md`)

## Success Criteria
**How will we know when we're done?**

- [ ] `android/` contains all existing Android modules; the Android project builds successfully from `android/`
- [ ] `flutter/` contains a Flutter project with Wire Core, Module Registry, Event Bus, 4 feature modules, shared UI, and contracts — `flutter run` succeeds
- [ ] `ios/` contains an iOS project with the equivalent architecture using Swift packages — Xcode build succeeds
- [ ] Each platform's `README.md` documents setup, build, and run instructions
- [ ] Architecture parity: all three platforms implement Module Registry, Event Bus, Widget Slots, Role-Based Loading
- [ ] All existing Android Gradle builds still pass post-move

## Constraints & Assumptions

- **Constraint**: The Android project must not break after the move — Gradle paths, `settings.gradle.kts`, and `local.properties` must be updated accordingly
- **Constraint**: Flutter requires Dart ≥ 3.3 and Flutter ≥ 3.22 (null-safety, records, sealed classes)
- **Constraint**: iOS target is iOS 16+ using SwiftUI and Swift Package Manager
- **Assumption**: Each platform is independently buildable — no cross-compilation or shared runtime
- **Assumption**: Feature modules scaffold only (no real API calls or backend)
- **Assumption**: DI, navigation, and state management libraries chosen are the idiomatic defaults for each platform

## Questions & Open Items

- [ ] Should `docs/guides/` (01-new-module.md, etc.) be duplicated per platform or remain shared?
- [ ] Should `gradle/libs.versions.toml` stay at root or move into `android/gradle/`?
- [x] **RESOLVED** — iOS project uses pure SwiftUI + NavigationStack (not UIKit + Coordinator)
- [x] **RESOLVED** — Flutter scaffold uses StatefulWidget + FutureBuilder; Riverpod is the recommended upgrade path for production state management
- [x] **RESOLVED** — `wire/` module mirrors the same 5 conceptual components on all platforms: ModuleRegistry, AppEventBus, SlotRegistry, WireContainer, NavigationAssembler

### Edge Cases (to validate during testing)
- What happens when a role has zero active modules? (App should render an empty state, not crash)
- What if `local.properties` contains an absolute SDK path that differs on CI machines post-move?
- Xcode project file (`.xcodeproj`) must be manually created — it is not auto-generated by SPM alone
