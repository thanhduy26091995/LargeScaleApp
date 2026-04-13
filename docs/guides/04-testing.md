# Guide 04 — Testing Modules

This guide covers unit and integration testing patterns for feature modules in this architecture.

---

## Test Locations

| What to test | Test type | Directory |
|---|---|---|
| `ModuleRegistry`, `FlowEventBus`, `SlotRegistryImpl`, `RoleManager` | Unit | `wire/src/test/` |
| `AuthServiceImpl` | Unit | `core/src/test/` |
| ViewModels | Unit | `feature-*/src/test/` |
| `AppModule` contract | Unit | `feature-*/src/test/` |
| Full navigation flow | Instrumented | `app/src/androidTest/` |
| Compose UI | Instrumented | `feature-*/src/androidTest/` |

---

## Testing an AppModule

Every feature module should verify its contract:

```kotlin
class OrdersFeatureModuleTest {

    @Test
    fun `module id is stable`() {
        assertEquals("orders", OrdersFeatureModule().metadata.id)
    }

    @Test
    fun `orders requires ADMIN or STAFF — not CUSTOMER or GUEST`() {
        val roles = OrdersFeatureModule().metadata.requiredRoles
        assertTrue(Role.ADMIN in roles)
        assertTrue(Role.STAFF in roles)
        assertFalse(Role.CUSTOMER in roles)
        assertFalse(Role.GUEST in roles)
    }

    @Test
    fun `provideRoutes includes ORDERS route`() {
        val routes = OrdersFeatureModule().provideRoutes().map { it.route }
        assertTrue(Routes.ORDERS in routes)
    }
}
```

---

## Testing a ViewModel

Use `kotlinx-coroutines-test` and a fake repository:

```kotlin
class OrdersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()   // sets Main dispatcher to TestDispatcher

    private val fakeRepository = FakeOrderRepository()
    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setup() {
        viewModel = OrdersViewModel(fakeRepository)
    }

    @Test
    fun `orders list reflects repository state`() = runTest {
        fakeRepository.setOrders(listOf(Order(id = "ORD-001", status = OrderStatus.PENDING)))

        val state = viewModel.uiState.value

        assertEquals(1, state.orders.size)
        assertEquals("ORD-001", state.orders[0].id)
    }
}

class FakeOrderRepository : OrderRepository {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    override val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    fun setOrders(orders: List<Order>) { _orders.value = orders }
}
```

---

## Testing EventBus interactions

Use `FlowEventBus` directly (no mocking needed):

```kotlin
class OrdersFeatureModuleEventTest {

    private val eventBus = FlowEventBus()

    @Test
    fun `publishing OrderCreatedEvent is received by subscriber`() = runTest {
        val received = mutableListOf<OrderCreatedEvent>()
        val job = eventBus.on<OrderCreatedEvent> { received.add(it) }

        eventBus.publish(OrderCreatedEvent(orderId = "ORD-001", customerId = "C1", amount = 99.0))
        delay(50)
        job.cancel()

        assertEquals(1, received.size)
        assertEquals("ORD-001", received[0].orderId)
    }
}
```

---

## Testing SlotRegistry integration

```kotlin
class OrdersWidgetRegistrationTest {

    private val slotRegistry = SlotRegistryImpl()
    private val fakeEventBus = FakeEventBus()
    private val fakeContext = FakeModuleContext(slotRegistry = slotRegistry, eventBus = fakeEventBus)

    @Test
    fun `initialize registers orders widget in HOME_WIDGETS slot`() {
        OrdersFeatureModule().initialize(fakeContext)

        val widgets = slotRegistry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)
        val widgetIds = widgets.map { it.widgetId }

        assertTrue("orders-summary" in widgetIds)
    }

    @Test
    fun `orders widget requires STAFF or higher`() {
        OrdersFeatureModule().initialize(fakeContext)

        val adminWidgets = slotRegistry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.ADMIN)
        val guestWidgets = slotRegistry.getSlotsForHost(SlotIds.HOME_WIDGETS, Role.GUEST)

        assertTrue(adminWidgets.any { it.widgetId == "orders-summary" })
        assertFalse(guestWidgets.any { it.widgetId == "orders-summary" })
    }
}
```

### FakeModuleContext helper

Create once in a `testFixtures` source set or copy per-module:

```kotlin
class FakeModuleContext(
    override val slotRegistry: SlotRegistry = SlotRegistryImpl(),
    override val eventBus: EventBus = FakeEventBus(),
    override val tenantConfig: StateFlow<TenantConfig?> = MutableStateFlow(null),
    override val currentRole: StateFlow<Role> = MutableStateFlow(Role.ADMIN)
) : ModuleContext {
    override fun navigate(route: String, args: Map<String, String>) {}
    override fun getApplicationContext(): android.content.Context = TODO("not needed in unit tests")
}
```

---

## Testing AuthService

Use `MockK` for `StorageManager` (wraps Android `DataStore`) and a `FakeEventBus`:

```kotlin
class AuthServiceImplTest {

    private val fakeEventBus = FakeEventBus()
    private val storageManager: StorageManager = mockk(relaxed = true)
    private val authService = AuthServiceImpl(fakeEventBus, storageManager)

    @Test
    fun `login with valid credentials publishes UserAuthenticatedEvent`() = runTest {
        authService.login("admin", "admin")

        val event = fakeEventBus.published.filterIsInstance<UserAuthenticatedEvent>().first()
        assertEquals(Role.ADMIN, event.role)
    }
}
```

---

## Testing RoleManager

`RoleManager` subscribes to `FlowEventBus` in `init{}`. Use a real `FlowEventBus`:

```kotlin
class RoleManagerTest {

    private val eventBus = FlowEventBus()
    private val roleManager = RoleManager(eventBus)

    @Test
    fun `role updates when UserAuthenticatedEvent is published`() = runTest {
        eventBus.publish(UserAuthenticatedEvent(userId = "usr-1", role = Role.STAFF))
        delay(50)

        assertEquals(Role.STAFF, roleManager.currentRole.value)
    }
}
```

---

## MainDispatcherRule

Required for ViewModels that use `viewModelScope` + `Dispatchers.Main`:

```kotlin
// shared-test/MainDispatcherRule.kt
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
```

---

## Test Coverage Targets

| Layer | Target coverage |
|---|---|
| `AppModule` contract (id, roles, routes) | 100% |
| ViewModel business logic | ≥ 80% |
| Wire core (Registry, EventBus, SlotRegistry) | ≥ 90% |
| AuthService (login/logout/restore) | ≥ 90% |
| Compose UI | Critical paths only (instrumented) |

---

## What NOT to test

- `provideWidgets()` returning an empty list — trivial
- Compose rendering details — prefer UI tests for visual regressions
- Hilt module bindings — trust the framework
- DataStore persistence in unit tests — use MockK or test on Android (instrumented)
