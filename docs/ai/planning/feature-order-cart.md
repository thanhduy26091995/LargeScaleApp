---
phase: planning
title: Project Planning & Task Breakdown — Order Cart
description: Break down work into actionable tasks and estimate timeline
---

# Project Planning & Task Breakdown — Order Cart

## Milestones

- [ ] **M1 — Module scaffold**: Gradle module, manifest, route constants, contracts additions
- [ ] **M2 — Domain & data layer**: models, repository interface, DataStore implementation, use cases
- [ ] **M3 — Presentation layer**: CartScreen, CartViewModel, CartBadgeWidget, NavGraph
- [ ] **M4 — Wire Core integration**: register module in `:app`, Hilt wiring, slot registration
- [ ] **M5 — Tests & polish**: unit tests for use cases + ViewModel, UI smoke test, docs

---

## Task Breakdown

### Phase 1 — Foundation (M1)

- [ ] **T1.1** Add `:feature-order-cart` to `settings.gradle.kts`
- [ ] **T1.2** Create `feature-order-cart/build.gradle.kts` (mirror `:feature-orders` config, `namespace = "com.densitech.largescale.feature.cart"`)
- [ ] **T1.3** Create `feature-order-cart/src/main/AndroidManifest.xml` (`<manifest />`)
- [ ] **T1.4** Add `Routes.CART = "/cart"` to `:contracts/Routes.kt`
- [ ] **T1.5** Add `CartSubmittedEvent` data class to `:contracts/ModuleEvent.kt`
- [ ] **T1.6** Gradle sync

### Phase 2 — Domain & Data Layer (M2)

- [ ] **T2.1** Create `domain/CartItem.kt` and `domain/Cart.kt` (pure Kotlin data classes)
- [ ] **T2.2** Create `data/CartRepository.kt` (interface with `observeCart`, `addItem`, `removeItem`, `updateQuantity`, `clearCart`, `submitCart`)
- [ ] **T2.3** Create `data/CartRepositoryImpl.kt` using `DataStore<Preferences>` for persistence; serialize cart as JSON string
- [ ] **T2.4** Create `domain/AddToCartUseCase.kt`
- [ ] **T2.5** Create `domain/RemoveFromCartUseCase.kt`
- [ ] **T2.6** Create `domain/UpdateQuantityUseCase.kt`
- [ ] **T2.7** Create `domain/ClearCartUseCase.kt`
- [ ] **T2.8** Create `domain/SubmitCartUseCase.kt` — publishes `CartSubmittedEvent` via `EventBus`, calls `clearCart` on success
- [ ] **T2.9** Create `di/CartModule.kt` — Hilt `@Module` binding `CartRepository` → `CartRepositoryImpl`, providing DataStore instance

### Phase 3 — Presentation Layer (M3)

- [ ] **T3.1** Create `ui/cart/CartViewModel.kt` — exposes `StateFlow<CartUiState>`, handles add/remove/update/clear/submit actions
- [ ] **T3.2** Create `ui/cart/CartScreen.kt` — `LazyColumn` of `CartItemRow`, total price footer, "Submit Order" button, error snackbar
- [ ] **T3.3** Create `ui/widget/CartBadgeWidget.kt` — compact `HOME_QUICK_ACTIONS` widget showing badge count
- [ ] **T3.4** Create `nav/CartNavGraph.kt` — registers `Routes.CART` composable destination
- [ ] **T3.5** Create `CartFeatureModule.kt` — implements `AppModule`, registers widget in `initialize()`, provides routes

### Phase 4 — Wire Core Integration (M4)

- [ ] **T4.1** Register `CartFeatureModule` in `:app`'s `App.kt` (add to `registerModules(...)` list)
- [ ] **T4.2** Add `:feature-order-cart` dependency to `:app/build.gradle.kts`
- [ ] **T4.3** Add `CartNavGraph` call inside `:app`'s `NavHost` setup
- [ ] **T4.4** Verify slot appears in `HOME_QUICK_ACTIONS` on dashboard for CUSTOMER role

### Phase 5 — Tests & Polish (M5)

- [ ] **T5.1** Unit tests: `AddToCartUseCase`, `RemoveFromCartUseCase`, `UpdateQuantityUseCase`, `ClearCartUseCase`
- [ ] **T5.2** Unit tests: `SubmitCartUseCase` (verify `CartSubmittedEvent` is published, cart is cleared)
- [ ] **T5.3** Unit tests: `CartViewModel` — state transitions for each action
- [ ] **T5.4** Verify `CartRepositoryImpl` correctly reads/writes DataStore (integration-style unit test with fake DataStore)
- [ ] **T5.5** Manual smoke test: add item → view cart → change quantity → remove item → submit → verify event

---

## Dependencies

```
T1.x  →  T2.x  →  T3.x  →  T4.x  →  T5.x

T1.4 (Routes.CART) must precede T3.5 (CartFeatureModule).
T1.5 (CartSubmittedEvent) must precede T2.8 (SubmitCartUseCase).
T2.2 (CartRepository interface) must precede T2.3, T2.4–T2.8, T3.1.
T2.9 (Hilt module) must precede T4.x.
```

External dependencies:
- `kotlinx-serialization-json` needed in `feature-order-cart/build.gradle.kts` for DataStore serialisation (or use Gson which is already in the project via Retrofit).

---

## Timeline & Estimates

| Phase | Effort |
|-------|--------|
| P1 — Foundation | 1–2 hours |
| P2 — Domain & Data | 3–4 hours |
| P3 — Presentation | 4–6 hours |
| P4 — Integration | 1–2 hours |
| P5 — Tests | 3–4 hours |
| **Total** | **~12–18 hours** |

---

## Risks & Mitigation

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| DataStore serialisation complexity for nested objects | Medium | Use `kotlinx-serialization` or a flat Preferences structure per item |
| `CartSubmittedEvent` payload disagreement with orders module | Low | Agree payload schema (Q-4 from requirements) before T2.8 |
| Cart badge widget slot not visible if CUSTOMER role not set at login | Low | Use emulator test account with CUSTOMER role (see README Test Accounts) |
| Hilt scoping conflict between `CartRepository` and `DataStore` instance | Low | Scope DataStore to `@Singleton`; validate with Hilt component tree |

---

## Resources Needed

- Android developer familiar with Hilt + Compose + DataStore
- Design spec for `CartScreen` (item row, badge widget) — Material 3 components from `:shared-ui`
- Product sign-off on open questions Q-1 through Q-4 before Phase 2 begins
