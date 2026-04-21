---
phase: testing
title: Testing Strategy — Order Cart
description: Define testing approach, test cases, and quality assurance
---

# Testing Strategy — Order Cart

## Test Coverage Goals

- Unit test coverage: **≥ 80 %** of all new/changed source files
- All use cases: 100 % branch coverage
- `CartViewModel`: 100 % of state transitions
- Integration: critical path (add → view → submit) + error path (submit failure)
- End-to-end: manual smoke test before each release

---

## Unit Tests

### `AddToCartUseCase`

- [ ] Adds a new item when the cart is empty
- [ ] Increments quantity when the same `itemId` is added again
- [ ] Does not create a duplicate entry for the same `itemId`

### `RemoveFromCartUseCase`

- [ ] Removes an item that exists in the cart
- [ ] No-ops gracefully when `itemId` is not found

### `UpdateQuantityUseCase`

- [ ] Updates quantity to a new valid value (≥ 1)
- [ ] Delegates to `RemoveFromCartUseCase` when new quantity is 0
- [ ] Rejects negative quantity (throws `IllegalArgumentException`)

### `ClearCartUseCase`

- [ ] Empties the cart regardless of contents
- [ ] Calling on an already-empty cart does not throw

### `SubmitCartUseCase`

- [ ] On success: publishes `CartSubmittedEvent` with correct `cartItems`, `totalAmount`, `customerId`
- [ ] On success: calls `clearCart()` after event is published
- [ ] On failure: does NOT publish `CartSubmittedEvent`
- [ ] On failure: does NOT call `clearCart()`
- [ ] Propagates the `Result.failure` to the caller

### `CartViewModel`

| Test | Scenario |
|------|----------|
| Initial state is `Loading` | ViewModel created; repository not yet emitting |
| State becomes `Success(cart)` | Repository emits a non-empty `Cart` |
| State becomes `Success(emptyCart)` | Repository emits an empty `Cart` |
| State becomes `Error` | Repository `observeCart()` throws |
| `add()` delegates to `AddToCartUseCase` | Verify use case `invoke()` called with correct item |
| `remove()` delegates to `RemoveFromCartUseCase` | Verify use case `invoke()` called with correct itemId |
| `updateQty()` delegates to `UpdateQuantityUseCase` | Verify use case called with itemId + new quantity |
| `clear()` delegates to `ClearCartUseCase` | Verify use case `invoke()` called |
| `submit()` delegates to `SubmitCartUseCase` | Verify use case `invoke()` called with customerId |
| Error snackbar shown on submit failure | `uiState` transitions to `Error` when `submitCart` returns failure |

> Use `MockK` for mocking and `kotlinx-coroutines-test` with `TestScope` + `UnconfinedTestDispatcher`.

---

## Integration Tests

- [ ] **Cart persistence**: write items via `CartRepositoryImpl`, kill process (simulate), re-read — items must match
- [ ] **Submit end-to-end**: call `SubmitCartUseCase` with a real (fake) EventBus → verify `CartSubmittedEvent` received by a subscriber
- [ ] **Slot registration**: `CartFeatureModule.initialize()` with a fake `ModuleContext` → verify `HOME_QUICK_ACTIONS` slot contains `cart-badge`
- [ ] **Route provision**: `CartFeatureModule.provideRoutes()` returns `Routes.CART` with `Role.CUSTOMER`

---

## End-to-End Tests (Manual Smoke)

Run with CUSTOMER test account (see README Test Accounts).

- [ ] **Add item to cart**: navigate to a product → tap "Add to Cart" → cart badge on dashboard increments
- [ ] **View cart**: tap cart badge → `CartScreen` opens → item appears with correct name, price, quantity
- [ ] **Change quantity**: tap `+` → subtotal updates → total updates
- [ ] **Remove item**: tap remove icon → item disappears → total updates
- [ ] **Submit order**: tap "Submit Order" → confirmation dialog → confirm → cart cleared → badge shows 0
- [ ] **Submit empty cart**: verify "Submit Order" button is disabled
- [ ] **Submit failure**: mock network error → cart NOT cleared → error snackbar shown
- [ ] **Cart persists**: add item → force-close app → reopen → item still in cart

---

## Test Data

```kotlin
// Reusable fake cart item for unit tests
val fakeCartItem = CartItem(
    itemId    = "item-001",
    name      = "Test Product",
    imageUrl  = null,
    unitPrice = 9.99,
    quantity  = 1
)

val fakeCart = Cart(items = listOf(fakeCartItem))
```

Use a `FakeCartRepository` implementing `CartRepository` backed by a `MutableStateFlow<Cart>` for ViewModel tests.

---

## Test Reporting & Coverage

Run unit tests:
```bash
./gradlew :feature-order-cart:testDebugUnitTest
```

Coverage report (JaCoCo):
```bash
./gradlew :feature-order-cart:jacocoTestReport
# Report: feature-order-cart/build/reports/jacoco/
```

Coverage threshold: fail build if line coverage < 80 % (configure in `build.gradle.kts`).

---

## Manual Testing Checklist

- [ ] Accessibility: cart item rows readable by TalkBack (content descriptions on quantity buttons)
- [ ] Empty state: empty cart shows helpful message + CTA to browse products
- [ ] Rotation: cart screen survives configuration change (state preserved via ViewModel)
- [ ] Dark mode: all cart UI elements visible in dark theme

---

## Bug Tracking

- Severity **Critical**: cart data lost unexpectedly; submit creates duplicate orders
- Severity **High**: badge count out of sync; submit button enabled on empty cart
- Severity **Medium**: quantity controls unresponsive; UI layout broken on small screens
- Regression tests required for any Critical/High fix before merge
