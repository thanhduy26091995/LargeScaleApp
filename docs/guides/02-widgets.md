# Guide 02 — Adding Widgets / UI Slots

Feature modules can contribute Composable UI into predefined **slot hosts** on any screen — without the host screen knowing about the feature module.

---

## Available Slot Hosts

Defined in [contracts/src/main/java/.../contracts/SlotRegistry.kt](../../contracts/src/main/java/com/densitech/largescale/contracts/SlotRegistry.kt):

| Slot ID | Constant | Host Screen | Description |
|---------|----------|-------------|-------------|
| `home_widgets` | `SlotIds.HOME_WIDGETS` | HomeScreen | Main card widgets on the dashboard |
| `dashboard_header` | `SlotIds.DASHBOARD_HEADER` | HomeScreen | Header banner area |
| `profile_actions` | `SlotIds.PROFILE_ACTIONS` | ProfileScreen | Action buttons below avatar |
| `bottom_bar_actions` | `SlotIds.BOTTOM_BAR_ACTIONS` | MainActivity | Extra bottom bar items |
| `home_quick_actions` | `SlotIds.HOME_QUICK_ACTIONS` | HomeScreen | Quick-action chips row |

---

## How to Register a Widget

### Option A — Register in `initialize()` (recommended when widget needs navigation or context)

```kotlin
override fun initialize(context: ModuleContext) {
    context.slotRegistry.register(
        UISlot(
            slotId       = SlotIds.HOME_WIDGETS,
            widgetId     = "orders-summary",   // must be globally unique
            moduleId     = metadata.id,
            priority     = 800,                // higher = rendered first
            requiredRole = Role.STAFF,         // who can see this widget
            content      = { OrdersSummaryWidget() }
        )
    )
}
```

### Option B — Declare in `provideWidgets()` (for static widgets without context)

```kotlin
override fun provideWidgets() = listOf(
    UISlot(
        slotId       = SlotIds.HOME_WIDGETS,
        widgetId     = "inventory-status",
        moduleId     = metadata.id,
        priority     = 700,
        requiredRole = Role.STAFF,
        content      = { InventoryStatusWidget() }
    )
)
```

`provideWidgets()` slots are registered automatically by `ModuleRegistry.initializeAll()`.

---

## UISlot Fields

```kotlin
data class UISlot(
    val slotId: String,          // which host renders this
    val widgetId: String,        // globally unique identifier
    val moduleId: String,        // owning module (for clearModule())
    val priority: Int = 100,     // descending sort — 1000 is first
    val requiredRole: Role = Role.GUEST,
    val content: @Composable () -> Unit
)
```

### Priority guidelines

| Priority range | Suggested use |
|----------------|--------------|
| 900–1000 | Critical/system widgets (feature-core) |
| 700–899 | Primary feature widgets (orders, inventory) |
| 400–699 | Secondary feature widgets (wallet, analytics) |
| 100–399 | Supplementary widgets |

---

## How the Host Screen Works

`HomeScreen` renders whatever is in the registry for its slot:

```kotlin
// HomeViewModel.kt
val homeWidgets = slotRegistry
    .getSlotsForHost(SlotIds.HOME_WIDGETS, currentRole)

// HomeScreen.kt
LazyColumn {
    items(state.homeWidgets, key = { it.widgetId }) { slot ->
        slot.content()   // renders your @Composable
    }
}
```

Widgets are automatically **filtered by role** and **sorted by priority** — no code changes in the host screen required.

---

## Widget ViewModel pattern

Widgets that need data should use their own `@HiltViewModel`:

```kotlin
// OrdersSummaryWidget.kt
@Composable
fun OrdersSummaryWidget(
    viewModel: OrdersWidgetViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    AppCard {
        Text("${state.pendingCount} pending orders")
    }
}

// OrdersWidgetViewModel.kt
@HiltViewModel
class OrdersWidgetViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {
    val uiState = repository.orders
        .map { orders -> WidgetState(pendingCount = orders.count { it.status == PENDING }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WidgetState())
}
```

---

## Removing a Widget

When a module is unloaded or a user logs out, clean up its slots:

```kotlin
// Remove a specific widget
slotRegistry.unregister("orders-summary")

// Remove all widgets from a module at once
slotRegistry.clearModule("orders")
```

`clearModule()` is called automatically by `ModuleRegistry` if you unload a module at runtime.

---

## Adding a New Slot Host

To create a new slot in a screen not yet listed above:

1. Add the constant to `SlotIds` in [SlotRegistry.kt](../../contracts/src/main/java/com/densitech/largescale/contracts/SlotRegistry.kt):
   ```kotlin
   object SlotIds {
       const val MY_NEW_SLOT = "my_new_slot"
   }
   ```

2. Render it in your screen:
   ```kotlin
   val slots = slotRegistry.getSlotsForHost(SlotIds.MY_NEW_SLOT, currentRole)
   slots.forEach { it.content() }
   ```

3. Document the new slot in the table at the top of this guide.
