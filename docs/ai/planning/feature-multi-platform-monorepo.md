---
phase: planning
title: Project Planning & Task Breakdown
description: Task breakdown for multi-platform monorepo restructuring (Android, Flutter, iOS)
feature: multi-platform-monorepo
---

# Project Planning & Task Breakdown

## Milestones

- [ ] **Milestone 1**: Android moved to `android/` — existing build fully passes from new path
- [ ] **Milestone 2**: Flutter scaffold complete — `flutter run` succeeds with Wire Core + 4 feature modules
- [ ] **Milestone 3**: iOS scaffold complete — Xcode builds `App` target with Wire Core + 4 feature modules
- [ ] **Milestone 4**: Documentation updated — platform READMEs and shared docs updated for new paths

---

## Task Breakdown

### Phase 1: Monorepo Restructure (Android Move)

- [ ] **Task 1.1**: Create `android/` directory at repo root
- [ ] **Task 1.2**: Move all Android Gradle modules into `android/`:
  - `app/` → `android/app/`
  - `core/` → `android/core/`
  - `contracts/` → `android/contracts/`
  - `wire/` → `android/wire/`
  - `feature-core/` → `android/feature-core/`
  - `feature-dashboard/` → `android/feature-dashboard/`
  - `feature-inventory/` → `android/feature-inventory/`
  - `feature-orders/` → `android/feature-orders/`
  - `feature-wallet/` → `android/feature-wallet/`
  - `shared-ui/` → `android/shared-ui/`
- [ ] **Task 1.3**: Move Gradle infrastructure into `android/`:
  - `build.gradle.kts` → `android/build.gradle.kts`
  - `settings.gradle.kts` → `android/settings.gradle.kts`
  - `gradle.properties` → `android/gradle.properties`
  - `gradlew` / `gradlew.bat` → `android/gradlew` / `android/gradlew.bat`
  - `gradle/` → `android/gradle/`
  - `local.properties` → `android/local.properties`
- [ ] **Task 1.4**: Update `android/settings.gradle.kts` — verify all module includes still resolve
- [ ] **Task 1.5**: Update `android/app/proguard-rules.pro` path references if any
- [ ] **Task 1.6**: Verify Android build: `cd android && ./gradlew assembleDebug`
- [ ] **Task 1.7**: Create `android/README.md` with setup and build instructions

### Phase 2: Flutter Scaffold

- [ ] **Task 2.1**: Run `flutter create --template=package wire` inside `flutter/wire/`
- [ ] **Task 2.2**: Implement `wire/` package:
  - `ModuleRegistry` (resolve by role)
  - `AppEventBus` (Stream-based pub/sub)
  - `SlotRegistry` (widget slot injection)
  - `WireContainer` (get_it wrapper)
  - `NavigationAssembler` (go_router routes builder)
- [ ] **Task 2.3**: Create `flutter/contracts/` package:
  - `AppModule` abstract class
  - `AppNavigator` abstract class
  - `AppWidget` abstract class
  - `AppRoute` sealed class
  - `Role` enum
- [ ] **Task 2.4**: Create `flutter/core/` package:
  - Auth service stub
  - Network service stub (Dio)
  - Storage service stub (shared_preferences)
- [ ] **Task 2.5**: Create `flutter/shared_ui/` package:
  - `AppTheme` (Material 3 tokens)
  - `ProductCard` widget
  - `AvatarWidget`
  - `RatingWidget`
- [ ] **Task 2.6**: Scaffold `flutter/feature_dashboard/` package:
  - `DashboardModule` (implements `AppModule`)
  - `DashboardScreen` (StatelessWidget / BLoC)
  - `DashboardUseCase`
  - `DashboardRepository` (interface + stub implementation)
- [ ] **Task 2.7**: Scaffold `flutter/feature_orders/` package (same structure as 2.6)
- [ ] **Task 2.8**: Scaffold `flutter/feature_inventory/` package
- [ ] **Task 2.9**: Scaffold `flutter/feature_wallet/` package
- [ ] **Task 2.10**: Create `flutter/app/` — main app shell:
  - `main.dart` initializes `WireContainer`, resolves role, loads modules
  - `go_router` navigation assembled from module routes
  - Role selection demo screen (for testing)
- [ ] **Task 2.11**: Create root `flutter/pubspec.yaml` workspace or `melos.yaml`
- [ ] **Task 2.12**: Verify: `cd flutter/app && flutter run`
- [ ] **Task 2.13**: Create `flutter/README.md`

### Phase 3: iOS Scaffold

- [ ] **Task 3.1**: Create `ios/Packages/Contracts/` SPM package:
  - `AppModule` protocol
  - `AppNavigator` protocol
  - `AppWidget` protocol
  - `AppRoute` enum
  - `Role` enum
- [ ] **Task 3.2**: Create `ios/Packages/WireCore/` SPM package:
  - `ModuleRegistry` (resolve by role)
  - `AppEventBus` (Combine `PassthroughSubject`)
  - `SlotRegistry` (UI widget injection)
  - `WireContainer` (Swinject wrapper)
  - `NavigationAssembler` (NavigationStack path builder)
- [ ] **Task 3.3**: Create `ios/Packages/Core/` SPM package:
  - `AuthService` stub
  - `NetworkService` stub (URLSession / Alamofire)
  - `StorageService` stub (UserDefaults wrapper)
- [ ] **Task 3.4**: Create `ios/Packages/SharedUI/` SPM package:
  - `AppTheme` (Color + Font tokens)
  - `ProductCardView` (SwiftUI)
  - `AvatarView` (SwiftUI)
  - `RatingView` (SwiftUI)
- [ ] **Task 3.5**: Scaffold `ios/Packages/FeatureDashboard/` SPM package:
  - `DashboardModule` (implements `AppModule`)
  - `DashboardView` (SwiftUI)
  - `DashboardViewModel` (`ObservableObject`)
  - `DashboardUseCase`
  - `DashboardRepository` (protocol + stub)
- [ ] **Task 3.6**: Scaffold `ios/Packages/FeatureOrders/`
- [ ] **Task 3.7**: Scaffold `ios/Packages/FeatureInventory/`
- [ ] **Task 3.8**: Scaffold `ios/Packages/FeatureWallet/`
- [ ] **Task 3.9**: Create `ios/App/` Xcode project:
  - `AppDelegate.swift` — bootstraps WireCore
  - `ContentView.swift` — role-aware root navigation
  - Link all SPM packages as local dependencies
- [ ] **Task 3.10**: Verify: open `ios/App/App.xcodeproj` → build succeeds in Xcode
- [ ] **Task 3.11**: Create `ios/README.md`

### Phase 4: Documentation & Cleanup

- [ ] **Task 4.1**: Update root `README.md` to describe the monorepo structure
- [ ] **Task 4.2**: Update `docs/guides/01-new-module.md` to include Flutter and iOS instructions
- [ ] **Task 4.3**: Verify `docs/ai/` ADR and design docs link correctly after file moves
- [ ] **Task 4.4**: Remove any now-stale root-level Android build files (gradlew, build.gradle.kts, etc.) after confirming they're safely in `android/`

---

## Dependencies

```
Phase 1 (Android Move)
    └─ must complete before any CI references android path

Phase 2 (Flutter)
    ├─ Task 2.2 (wire) must complete before 2.6–2.10 (features depend on wire)
    ├─ Task 2.3 (contracts) must complete before 2.2 and 2.6–2.10
    └─ Task 2.4–2.5 (core, shared_ui) must complete before 2.6–2.10

Phase 3 (iOS)
    ├─ Task 3.1 (Contracts) must complete before 3.2–3.9
    ├─ Task 3.2 (WireCore) must complete before 3.5–3.9
    └─ Task 3.3–3.4 (Core, SharedUI) must complete before 3.5–3.9

Phase 4 (Docs)
    └─ depends on Phases 1–3 completing
```

---

## Timeline & Estimates

| Phase | Effort Estimate |
|-------|----------------|
| Phase 1: Android Move | 2–4 hours (mostly mechanical) |
| Phase 2: Flutter Scaffold | 2–3 days (new platform, architecture translation) |
| Phase 3: iOS Scaffold | 2–3 days (SPM setup + SwiftUI patterns) |
| Phase 4: Documentation | 2–4 hours |
| **Total** | **~1 week** |

---

## Risks & Mitigation

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| Android build breaks after move (path issues) | Medium | High | Update `settings.gradle.kts` carefully; test before committing |
| Flutter multi-package workspace tooling complexity | Medium | Medium | Use `melos` for package management across Flutter packages |
| iOS SPM local package resolution issues | Medium | Medium | Test each package in isolation before wiring into App project |
| `local.properties` containing absolute SDK path | High | Low | Update SDK path in `android/local.properties` after move |
| CI/CD pipelines referencing old root-level paths | High | Medium | Update CI configs after Phase 1 completes |

---

## Resources Needed

- Android Studio (to verify Android build post-move)
- Flutter SDK ≥ 3.22 + Dart ≥ 3.3
- Xcode ≥ 15 + iOS 16 SDK
- `melos` CLI (optional, for Flutter workspace management): `dart pub global activate melos`
- `swinject` Swift package (for iOS DI)
- `get_it` + `go_router` Flutter packages
