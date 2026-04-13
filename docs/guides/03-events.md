# Guide 03 — Cross-Module Communication via EventBus

Feature modules never import each other directly. All cross-module communication happens through `EventBus` — a type-safe, coroutine-based publish/subscribe system backed by `SharedFlow`.

---

## Architecture

```
FeatureA  ──publish()──►  FlowEventBus  ──subscribe()──►  FeatureB
                              │
                         SharedFlow<ModuleEvent>
                         (replay=0, buffer=64)
```

Both modules only depend on `:contracts` — they never know about each other.

---

## Existing Event Types

Defined in [contracts/src/main/java/.../contracts/ModuleEvent.kt](../../contracts/src/main/java/com/densitech/largescale/contracts/ModuleEvent.kt):

| Event | Published by | Consumed by |
|-------|-------------|-------------|
| `UserAuthenticatedEvent(userId, role)` | `AuthServiceImpl` on login/restore | `RoleManager`, any module needing role |
| `UserLoggedOutEvent` | `AuthServiceImpl` on logout | `CoreFeatureModule` (navigate to login), `RoleManager` |
| `ModuleInitializedEvent(moduleId)` | `ModuleRegistry` per module | monitoring/analytics |
| `OrderCreatedEvent(orderId, customerId, amount)` | orders module | inventory, wallet, analytics |
| `TenantSwitchedEvent(oldId, newId, config)` | `TenantResolverImpl` | any tenant-aware module |
| `TenantConfigUpdatedEvent(tenantId, config)` | `TenantResolverImpl` | any tenant-aware module |
| `FeatureFlagChangedEvent(flag, enabled)` | remote config | any flag-gated feature |
| `NavigationRequestedEvent(route, args)` | any module | `AppNavigatorImpl` |

---

## Publishing an Event

Access `eventBus` via `ModuleContext`:

```kotlin
override fun initialize(context: ModuleContext) {
    // Subscribe to order creation from anywhere in the orders module
}

// Inside a ViewModel or repository:
class OrderRepository @Inject constructor(
    private val eventBus: EventBus   // injected by Hilt
) {
    suspend fun createOrder(order: Order) {
        _orders.value = _orders.value + order
        eventBus.publish(
            OrderCreatedEvent(
                orderId    = order.id,
                customerId = order.customerId,
                amount     = order.total
            )
        )
    }
}
```

`publish()` is non-suspending — it uses `tryEmit()` on the `SharedFlow` with a buffer of 64.

---

## Subscribing to Events

### From `AppModule.initialize()` (module lifecycle)

```kotlin
override fun initialize(context: ModuleContext) {
    // Using the reified extension — no KClass boilerplate
    context.eventBus.on<OrderCreatedEvent> { event ->
        refreshBadgeCount(event.orderId)
    }

    context.eventBus.on<UserLoggedOutEvent> {
        clearLocalCache()
    }
}
```

### From a ViewModel (UI lifecycle)

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val eventBus: EventBus
) : ViewModel() {

    init {
        eventBus.on<OrderCreatedEvent> { event ->
            // runs on Dispatchers.Default
            _newOrderCount.update { it + 1 }
        }.also { job ->
            // cancel when ViewModel is cleared
            viewModelScope.launch { job.join() }
        }
    }
}
```

> Prefer subscribing in `initialize()` for module-scoped reactions, and in `ViewModel.init{}` for UI-scoped reactions. Both patterns are safe.

---

## Defining a New Event Type

Add to [ModuleEvent.kt](../../contracts/src/main/java/com/densitech/largescale/contracts/ModuleEvent.kt):

```kotlin
sealed class ModuleEvent

// ... existing events ...

data class InventoryLowStockEvent(
    val productId: String,
    val remainingQuantity: Int
) : ModuleEvent()
```

Rules:
- Always extend `ModuleEvent`
- Make it a `data class` (or `object` for no-payload events like `UserLoggedOutEvent`)
- Keep the name as `<Noun><Verb>Event` or `<Noun><State>Event`
- Put payload fields directly — no nested wrappers

---

## Lifecycle and Cancellation

`FlowEventBus` uses `CoroutineScope(SupervisorJob() + Dispatchers.Default)`. Subscriptions live until the returned `Job` is cancelled.

```kotlin
// The on<T>() extension returns a Job
val job: Job = eventBus.on<OrderCreatedEvent> { ... }

// Cancel when done (e.g., ViewModel.onCleared)
job.cancel()
```

Subscriptions registered in `AppModule.initialize()` live for the app's lifetime, which is correct for singleton modules.

---

## Thread Safety

- `publish()` is thread-safe (uses `tryEmit` on a `MutableSharedFlow`)
- Subscribers receive events on `Dispatchers.Default`
- If you need to update UI state, switch dispatcher:

```kotlin
eventBus.on<OrderCreatedEvent> { event ->
    withContext(Dispatchers.Main) {
        _uiState.value = _uiState.value.copy(newOrder = event.orderId)
    }
}
```

---

## Common Patterns

### Request-response via events

```kotlin
// Requester (module A)
eventBus.publish(NavigationRequestedEvent(route = Routes.ORDERS))

// Handler (AppNavigatorImpl or wire module)
eventBus.on<NavigationRequestedEvent> { event ->
    navigator.navigate(event.route, event.args)
}
```

### Broadcast to refresh UI

```kotlin
// Publisher
eventBus.publish(TenantConfigUpdatedEvent(tenantId = "brand-a", config = newConfig))

// All interested modules react independently
eventBus.on<TenantConfigUpdatedEvent> { event ->
    reloadContentForTenant(event.config)
}
```
