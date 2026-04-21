---
phase: implementation
title: Implementation Guide — Order Cart
description: Technical implementation notes, patterns, and code guidelines
---

# Implementation Guide — Order Cart

## Development Setup

**Prerequisites:**
- Android Studio Iguana or later
- JDK 17 (project `jvmTarget = "17"`)
- Gradle sync must pass after Step 1 before writing Kotlin source

**Module to create:** `feature-order-cart/`

**Existing patterns to follow:** `:feature-orders` (same dependency set, same module registration pattern)

---

## Code Structure

```
feature-order-cart/
└── src/main/java/com/densitech/largescale/feature/cart/
    ├── CartFeatureModule.kt
    ├── data/
    │   ├── CartRepository.kt
    │   └── CartRepositoryImpl.kt
    ├── domain/
    │   ├── Cart.kt
    │   ├── CartItem.kt
    │   ├── AddToCartUseCase.kt
    │   ├── RemoveFromCartUseCase.kt
    │   ├── UpdateQuantityUseCase.kt
    │   ├── ClearCartUseCase.kt
    │   └── SubmitCartUseCase.kt
    ├── ui/
    │   ├── cart/
    │   │   ├── CartScreen.kt
    │   │   └── CartViewModel.kt
    │   └── widget/
    │       └── CartBadgeWidget.kt
    ├── nav/
    │   └── CartNavGraph.kt
    └── di/
        └── CartModule.kt
```

Naming conventions match the rest of the project:
- Feature entry point: `<Feature>FeatureModule.kt`
- Screen: `<Feature>Screen.kt`
- ViewModel: `<Feature>ViewModel.kt`
- NavGraph: `<Feature>NavGraph.kt`

---

## Implementation Notes

### Step 1 — Gradle scaffold

`feature-order-cart/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.densitech.largescale.feature.cart"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":contracts"))
    implementation(project(":shared-ui"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
```

`settings.gradle.kts` — add:
```kotlin
include(":feature-order-cart")
project(":feature-order-cart").projectDir = File("feature-order-cart")
```

### Step 2 — Contracts additions

**`contracts/.../Routes.kt`** — add inside `object Routes`:
```kotlin
const val CART = "/cart"
```

**`contracts/.../ModuleEvent.kt`** — add:
```kotlin
data class CartSubmittedEvent(
    val cartItems: List<String>,  // itemIds
    val totalAmount: Double,
    val customerId: String
) : ModuleEvent
```

### Step 3 — Domain models

`domain/CartItem.kt`:
```kotlin
data class CartItem(
    val itemId: String,
    val name: String,
    val imageUrl: String?,
    val unitPrice: Double,
    val quantity: Int
) {
    val subtotal: Double get() = unitPrice * quantity
}
```

`domain/Cart.kt`:
```kotlin
data class Cart(val items: List<CartItem> = emptyList()) {
    val totalPrice: Double get() = items.sumOf { it.subtotal }
    val totalCount: Int    get() = items.sumOf { it.quantity }
    val isEmpty: Boolean   get() = items.isEmpty()
}
```

### Step 4 — Repository

`data/CartRepository.kt`:
```kotlin
interface CartRepository {
    fun observeCart(): Flow<Cart>
    suspend fun addItem(item: CartItem)
    suspend fun removeItem(itemId: String)
    suspend fun updateQuantity(itemId: String, quantity: Int)
    suspend fun clearCart()
    suspend fun submitCart(customerId: String): Result<Unit>
}
```

`data/CartRepositoryImpl.kt` — persist items as JSON in `DataStore<Preferences>`.
Use a `StringPreferencesKey("cart_items")` and serialize the list with `kotlinx.serialization` or `Gson`.

### Step 5 — Use cases

Each use case follows the `operator fun invoke` pattern from the Clean Architecture skill:

```kotlin
class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(item: CartItem) = repository.addItem(item)
}
```

`SubmitCartUseCase` must:
1. Call `repository.submitCart(customerId)`.
2. On `Success` — publish `CartSubmittedEvent` via `EventBus` and call `clearCart()`.
3. On `Failure` — propagate the error; do NOT clear the cart.

### Step 6 — Hilt module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {
    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
}
// Provide DataStore in a separate @Provides fun if not already provided project-wide.
```

### Step 7 — ViewModel

```kotlin
@HiltViewModel
class CartViewModel @Inject constructor(
    private val addToCart: AddToCartUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val updateQuantity: UpdateQuantityUseCase,
    private val clearCart: ClearCartUseCase,
    private val submitCart: SubmitCartUseCase,
    private val repository: CartRepository
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = repository.observeCart()
        .map { CartUiState.Success(it) }
        .catch { emit(CartUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartUiState.Loading)

    fun add(item: CartItem) = viewModelScope.launch { addToCart(item) }
    fun remove(itemId: String) = viewModelScope.launch { removeFromCart(itemId) }
    fun updateQty(itemId: String, qty: Int) = viewModelScope.launch { updateQuantity(itemId, qty) }
    fun clear() = viewModelScope.launch { clearCart() }
    fun submit(customerId: String) = viewModelScope.launch { submitCart(customerId) }
}
```

### Step 8 — CartScreen (Compose)

Key guidelines:
- Use `LazyColumn` with `key = { it.itemId }` for stable recomposition.
- Each row: product image (coil `AsyncImage`), name, unit price, `+`/`-` quantity controls, subtotal, remove icon.
- Footer: total price + "Submit Order" `Button` (disabled when cart is empty).
- Collect `uiState` with `collectAsStateWithLifecycle()`.
- Show `Snackbar` for error states; do not clear cart on error.

### Step 9 — CartBadgeWidget

```kotlin
@Composable
fun CartBadgeWidget(cartCount: Int, onNavigate: () -> Unit) {
    // Small card with shopping cart icon + badge count
    // Tapping calls onNavigate() → Routes.CART
}
```

Register in `CartFeatureModule.initialize()`:
```kotlin
context.slotRegistry.register(
    UISlot(
        slotId       = SlotIds.HOME_QUICK_ACTIONS,
        widgetId     = "cart-badge",
        moduleId     = "cart",
        priority     = 900,
        requiredRole = Role.CUSTOMER
    ) { CartBadgeWidget(cartCount = ..., onNavigate = { context.navigate(Routes.CART) }) }
)
```

### Step 10 — Register in :app

`App.kt` — add `CartFeatureModule()` to `registerModules(...)`.

`app/build.gradle.kts` — add:
```kotlin
implementation(project(":feature-order-cart"))
```

`MainNavHost.kt` (or wherever NavHost is built) — call `CartNavGraph(navController)`.

---

## Integration Points

- **EventBus** — `SubmitCartUseCase` publishes `CartSubmittedEvent`; `:feature-orders` or analytics can subscribe.
- **AppNavigator** — `Routes.CART` registered via `CartFeatureModule.provideRoutes()`; other modules can deep-link.
- **SlotRegistry** — `CartBadgeWidget` injected into `HOME_QUICK_ACTIONS`; dashboard renders it automatically for CUSTOMER role.

---

## Error Handling

- Network/submit error: surface via `CartUiState.Error`, show `Snackbar`, preserve cart contents.
- DataStore write error: log warning; treat as no-op (cart may be transiently inconsistent — acceptable for v1).
- Quantity reaching 0: `UpdateQuantityUseCase` delegates to `RemoveFromCartUseCase` automatically.

---

## Security Notes

- `unitPrice` stored in DataStore is display-only. Authoritative price must be validated server-side when the order is created.
- No PII beyond `customerId` (already managed by AuthService) is stored in the cart.
