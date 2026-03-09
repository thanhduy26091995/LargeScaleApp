---
phase: testing
title: Testing Strategy
description: Comprehensive testing approach for modular architecture
feature: modular-android-base-architecture
---

# Testing Strategy

## Test Coverage Goals

### Coverage Targets
- **Unit Tests**: 80%+ coverage for business logic (ViewModels, UseCases, Repositories)
- **Integration Tests**: All critical module interactions and wire core functionality
- **UI Tests**: Key user journeys and screen rendering
- **End-to-End Tests**: Complete flows across multiple modules

### Alignment with Requirements
Tests must verify:
- ✅ Module registration and discovery works correctly
- ✅ Role-based module loading filters appropriately
- ✅ Event bus communication between modules
- ✅ Widget slot injection and rendering
- ✅ Navigation across module boundaries
- ✅ Module isolation (no direct dependencies)
- ✅ Error handling and graceful degradation

---

## Unit Tests

### Wire Core Components

#### ModuleRegistry Tests
```kotlin
@Test
fun `registerModule adds module to registry`() {
    // Arrange
    val registry = ModuleRegistry()
    val module = MockModule(id = "test", roles = listOf(Role.ADMIN))
    
    // Act
    registry.register(module)
    
    // Assert
    assertEquals(1, registry.getAllModules().size)
    assertEquals("test", registry.getModuleById("test")?.metadata?.id)
}

@Test
fun `resolve filters modules by role`() {
    // Arrange
    val registry = ModuleRegistry()
    registry.register(MockModule(id = "admin", roles = listOf(Role.ADMIN)))
    registry.register(MockModule(id = "customer", roles = listOf(Role.CUSTOMER)))
    registry.register(MockModule(id = "all", roles = listOf(Role.ADMIN, Role.CUSTOMER)))
    
    // Act
    val customerModules = registry.resolve(Role.CUSTOMER)
    
    // Assert
    assertEquals(2, customerModules.size) // "customer" and "all"
    assertTrue(customerModules.none { it.metadata.id == "admin" })
}

@Test
fun `getModuleById returns null for non-existent module`() {
    val registry = ModuleRegistry()
    assertNull(registry.getModuleById("nonexistent"))
}
```

#### EventBus Tests
```kotlin
@Test
fun `publish and subscribe delivers event`() = runTest {
    // Arrange
    val eventBus = FlowEventBus()
    var receivedEvent: OrderCreatedEvent? = null
    
    val job = eventBus.subscribe<OrderCreatedEvent> { event ->
        receivedEvent = event
    }
    
    // Act
    eventBus.publish(OrderCreatedEvent(orderId = "123"))
    advanceUntilIdle()
    
    // Assert
    assertEquals("123", receivedEvent?.orderId)
    job.cancel()
}

@Test
fun `subscribe filters by event type`() = runTest {
    val eventBus = FlowEventBus()
    var orderEventCount = 0
    var userEventCount = 0
    
    eventBus.subscribe<OrderCreatedEvent> { orderEventCount++ }
    eventBus.subscribe<UserAuthenticatedEvent> { userEventCount++ }
    
    eventBus.publish(OrderCreatedEvent("1"))
    eventBus.publish(UserAuthenticatedEvent("user1", Role.ADMIN))
    advanceUntilIdle()
    
    assertEquals(1, orderEventCount)
    assertEquals(1, userEventCount)
}

@Test
fun `multiple subscribers receive same event`() = runTest {
    val eventBus = FlowEventBus()
    val received = mutableListOf<String>()
    
    eventBus.subscribe<OrderCreatedEvent> { received.add("sub1: ${it.orderId}") }
    eventBus.subscribe<OrderCreatedEvent> { received.add("sub2: ${it.orderId}") }
    
    eventBus.publish(OrderCreatedEvent("123"))
    advanceUntilIdle()
    
    assertEquals(2, received.size)
    assertTrue(received.contains("sub1: 123"))
    assertTrue(received.contains("sub2: 123"))
}
```

#### SlotRegistry Tests
```kotlin
@Test
fun `registerSlot adds slot to registry`() {
    val registry = SlotRegistryImpl()
    val slot = UISlot(
        slotId = "home_widgets",
        moduleId = "orders",
        priority = 10,
        content = { Text("Widget") }
    )
    
    registry.registerSlot(slot)
    
    val slots = registry.getSlotsForHost("home_widgets", Role.ADMIN)
    assertEquals(1, slots.size)
}

@Test
fun `getSlotsForHost filters by role`() {
    val registry = SlotRegistryImpl()
    registry.registerSlot(
        UISlot("home", "admin", 10, {}, requiredRoles = listOf(Role.ADMIN))
    )
    registry.registerSlot(
        UISlot("home", "customer", 5, {}, requiredRoles = listOf(Role.CUSTOMER))
    )
    
    val customerSlots = registry.getSlotsForHost("home", Role.CUSTOMER)
    assertEquals(1, customerSlots.size)
    assertEquals("customer", customerSlots.first().moduleId)
}

@Test
fun `slots are sorted by priority descending`() {
    val registry = SlotRegistryImpl()
    registry.registerSlot(UISlot("home", "low", 1, {}))
    registry.registerSlot(UISlot("home", "high", 100, {}))
    registry.registerSlot(UISlot("home", "medium", 50, {}))
    
    val slots = registry.getSlotsForHost("home", Role.ADMIN)
    assertEquals(listOf("high", "medium", "low"), slots.map { it.moduleId })
}
```

#### Navigation Tests
```kotlin
@Test
fun `navigate updates current route`() = runTest {
    val navigator = AppNavigatorImpl(navController = mockNavController)
    
    navigator.navigate("orders")
    
    verify(mockNavController).navigate("orders", null)
}

@Test
fun `navigateBack calls popBackStack`() {
    val navigator = AppNavigatorImpl(mockNavController)
    
    navigator.navigateBack()
    
    verify(mockNavController).popBackStack()
}
```

---

### Feature Module Tests

#### ViewModel Tests
```kotlin
@Test
fun `loadOrders updates state with data`() = runTest {
    // Arrange
    val mockRepo = MockOrdersRepository(
        result = Result.success(listOf(Order("1"), Order("2")))
    )
    val viewModel = OrdersViewModel(mockRepo, mockEventBus, mockNavigator)
    
    // Act
    viewModel.loadOrders()
    advanceUntilIdle()
    
    // Assert
    val state = viewModel.state.value
    assertFalse(state.isLoading)
    assertEquals(2, state.orders.size)
    assertNull(state.error)
}

@Test
fun `loadOrders handles error gracefully`() = runTest {
    val mockRepo = MockOrdersRepository(
        result = Result.failure(Exception("Network error"))
    )
    val viewModel = OrdersViewModel(mockRepo, mockEventBus, mockNavigator)
    
    viewModel.loadOrders()
    advanceUntilIdle()
    
    val state = viewModel.state.value
    assertFalse(state.isLoading)
    assertTrue(state.orders.isEmpty())
    assertEquals("Network error", state.error)
}

@Test
fun `createOrder publishes OrderCreatedEvent`() = runTest {
    val mockRepo = MockOrdersRepository(createResult = Result.success(Order("123")))
    val capturedEvents = mutableListOf<ModuleEvent>()
    val mockEventBus = mock<EventBus> {
        on { publish(any()) } doAnswer { capturedEvents.add(it.arguments[0] as ModuleEvent) }
    }
    
    val viewModel = OrdersViewModel(mockRepo, mockEventBus, mockNavigator)
    viewModel.createOrder(Order("123"))
    advanceUntilIdle()
    
    assertEquals(1, capturedEvents.size)
    assertTrue(capturedEvents.first() is OrderCreatedEvent)
}
```

#### UseCase Tests
```kotlin
@Test
fun `GetOrdersUseCase returns mapped domain models`() = runTest {
    val mockRepo = mock<OrdersRepository> {
        onBlocking { getOrders() } doReturn Result.success(listOf(
            OrderDto("1", "Item", 100.0),
            OrderDto("2", "Item2", 200.0)
        ))
    }
    
    val useCase = GetOrdersUseCase(mockRepo)
    val result = useCase()
    
    assertTrue(result.isSuccess)
    assertEquals(2, result.getOrNull()?.size)
}
```

#### Repository Tests
```kotlin
@Test
fun `repository returns success when API call succeeds`() = runTest {
    val mockApi = mock<OrdersApi> {
        onBlocking { getOrders() } doReturn Response.success(
            listOf(OrderDto("1", "Item", 100.0))
        )
    }
    
    val repository = OrdersRepositoryImpl(mockApi)
    val result = repository.getOrders()
    
    assertTrue(result.isSuccess)
    assertEquals(1, result.getOrNull()?.size)
}

@Test
fun `repository handles network errors`() = runTest {
    val mockApi = mock<OrdersApi> {
        onBlocking { getOrders() } doThrow IOException("Network error")
    }
    
    val repository = OrdersRepositoryImpl(mockApi)
    val result = repository.getOrders()
    
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is NetworkException)
}

@Test
fun `repository handles API errors`() = runTest {
    val mockApi = mock<OrdersApi> {
        onBlocking { getOrders() } doReturn Response.error(404, mockResponseBody)
    }
    
    val repository = OrdersRepositoryImpl(mockApi)
    val result = repository.getOrders()
    
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is ApiException)
}
```

---

## Integration Tests

### Module Lifecycle Integration
```kotlin
@Test
fun `app initializes all registered modules`() {
    // Arrange
    val registry = ModuleRegistry()
    val modules = listOf(
        TestModule("core"),
        TestModule("dashboard"),
        TestModule("orders")
    )
    modules.forEach { registry.register(it) }
    
    val context = TestModuleContext()
    
    // Act
    val resolved = registry.resolve(Role.ADMIN)
    resolved.forEach { it.initialize(context) }
    
    // Assert
    assertEquals(3, resolved.size)
    modules.forEach { assertTrue(it.isInitialized) }
}

@Test
fun `modules receive correct context dependencies`() {
    val context = ModuleContextImpl(
        appContext = mockContext,
        eventBus = realEventBus,
        navigator = realNavigator,
        slotRegistry = realSlotRegistry
    )
    
    val module = TestModule("test")
    module.initialize(context)
    
    assertNotNull(module.receivedContext)
    assertNotNull(module.receivedContext?.eventBus)
    assertNotNull(module.receivedContext?.navigator)
}
```

### Cross-Module Communication
```kotlin
@Test
fun `module A publishes event and module B receives it`() = runTest {
    // Arrange
    val eventBus = FlowEventBus()
    val context = TestModuleContext(eventBus = eventBus)
    
    val moduleA = OrdersFeatureModule()
    val moduleB = InventoryFeatureModule()
    
    moduleA.initialize(context)
    moduleB.initialize(context)
    
    // Act
    eventBus.publish(OrderCreatedEvent("123"))
    advanceUntilIdle()
    
    // Assert
    assertTrue(moduleB.receivedOrderEvent)
    assertEquals("123", moduleB.lastOrderId)
}
```

### Navigation Integration
```kotlin
@HiltAndroidTest
@Test
fun `navigation works across module boundaries`() {
    // Arrange
    composeTestRule.setContent {
        val navController = rememberNavController()
        TestAppNavigation(navController)
    }
    
    // Act - Start at dashboard
    composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
    
    // Navigate to orders module
    composeTestRule.onNodeWithText("Orders").performClick()
    
    // Assert - Verify orders screen shown
    composeTestRule.onNodeWithText("Orders List").assertIsDisplayed()
}
```

### Widget Slot Integration
```kotlin
@Test
fun `dashboard displays widgets from multiple modules`() {
    // Arrange
    val slotRegistry = SlotRegistryImpl()
    val ordersModule = OrdersFeatureModule()
    val inventoryModule = InventoryFeatureModule()
    
    ordersModule.provideWidgets().forEach { slotRegistry.registerSlot(it) }
    inventoryModule.provideWidgets().forEach { slotRegistry.registerSlot(it) }
    
    // Act
    val widgets = slotRegistry.getSlotsForHost("home_widgets", Role.ADMIN)
    
    // Assert
    assertEquals(2, widgets.size)
    assertTrue(widgets.any { it.moduleId == "orders" })
    assertTrue(widgets.any { it.moduleId == "inventory" })
}

@Test
fun `widgets render in correct priority order`() {
    composeTestRule.setContent {
        DashboardScreen(slotRegistry = testSlotRegistry, userRole = Role.ADMIN)
    }
    
    // Verify high priority widget appears first
    composeTestRule.onAllNodesWithTag("widget")
        .assertCountEquals(3)
        .onFirst()
        .assertTextContains("Orders") // Priority 100
}
```

---

## UI Tests (Compose)

### Screen Rendering Tests
```kotlin
@Test
fun `OrdersScreen displays loading state`() {
    composeTestRule.setContent {
        OrdersScreen(
            viewModel = FakeOrdersViewModel(isLoading = true)
        )
    }
    
    composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
}

@Test
fun `OrdersScreen displays orders list`() {
    composeTestRule.setContent {
        OrdersScreen(
            viewModel = FakeOrdersViewModel(
                orders = listOf(
                    Order("1", "Order 1"),
                    Order("2", "Order 2")
                )
            )
        )
    }
    
    composeTestRule.onNodeWithText("Order 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Order 2").assertIsDisplayed()
}

@Test
fun `OrdersScreen displays error state`() {
    composeTestRule.setContent {
        OrdersScreen(
            viewModel = FakeOrdersViewModel(error = "Network error")
        )
    }
    
    composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
}
```

### Interaction Tests
```kotlin
@Test
fun `clicking order navigates to detail screen`() {
    composeTestRule.setContent {
        val navController = rememberNavController()
        TestNavHost(navController)
    }
    
    // Click first order
    composeTestRule.onNodeWithText("Order 1").performClick()
    
    // Verify detail screen shown
    composeTestRule.onNodeWithText("Order Detail").assertIsDisplayed()
}

@Test
fun `retry button reloads data`() {
    val viewModel = TestOrdersViewModel()
    composeTestRule.setContent {
        OrdersScreen(viewModel = viewModel)
    }
    
    // Click retry
    composeTestRule.onNodeWithText("Retry").performClick()
    
    // Verify reload called
    assertTrue(viewModel.reloadCalled)
}
```

### Widget Tests
```kotlin
@Test
fun `OrdersWidget displays recent orders`() {
    composeTestRule.setContent {
        OrdersWidget(
            viewModel = FakeOrdersWidgetViewModel(
                recentOrders = listOf(Order("1"), Order("2"), Order("3"))
            )
        )
    }
    
    composeTestRule.onNodeWithText("Recent Orders").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("order_row").assertCountEquals(3)
}
```

---

## End-to-End Tests

### Complete User Journey: View Orders
```kotlin
@HiltAndroidTest
@Test
fun `user can view and interact with orders`() {
    // Launch app
    val scenario = launchActivity<MainActivity>()
    
    // Step 1: User sees dashboard
    onView(withText("Dashboard")).check(matches(isDisplayed()))
    
    // Step 2: Navigate to orders
    onView(withText("Orders")).perform(click())
    
    // Step 3: See orders list
    onView(withText("Orders List")).check(matches(isDisplayed()))
    onView(withId(R.id.orders_list)).check(matches(hasMinimumChildCount(1)))
    
    // Step 4: Click on order
    onView(withText("Order #123")).perform(click())
    
    // Step 5: See order details
    onView(withText("Order Detail")).check(matches(isDisplayed()))
    onView(withText("#123")).check(matches(isDisplayed()))
}
```

### Module Loading by Role
```kotlin
@Test
fun `admin sees all modules, customer sees limited modules`() {
    // Test as ADMIN
    val adminScenario = launchActivity<MainActivity> {
        putExtra("user_role", "ADMIN")
    }
    
    onView(withText("Orders")).check(matches(isDisplayed()))
    onView(withText("Admin Panel")).check(matches(isDisplayed()))
    
    adminScenario.close()
    
    // Test as CUSTOMER
    val customerScenario = launchActivity<MainActivity> {
        putExtra("user_role", "CUSTOMER")
    }
    
    onView(withText("Orders")).check(doesNotExist())
    onView(withText("Admin Panel")).check(doesNotExist())
    onView(withText("Profile")).check(matches(isDisplayed()))
}
```

### Event Flow Across Modules
```kotlin
@Test
fun `creating order updates inventory widget`() {
    launchActivity<MainActivity>()
    
    // Navigate to orders
    onView(withText("Orders")).perform(click())
    
    // Create new order
    onView(withId(R.id.fab_create_order)).perform(click())
    onView(withId(R.id.et_order_name)).perform(typeText("New Order"))
    onView(withText("Submit")).perform(click())
    
    // Navigate back to dashboard
    pressBack()
    pressBack()
    
    // Verify inventory widget updated
    onView(withText("Recent Order: New Order"))
        .check(matches(isDisplayed()))
}
```

---

## Test Data

### Test Fixtures
```kotlin
object TestData {
    val sampleOrder = Order(
        id = "test-123",
        name = "Test Order",
        amount = 100.0,
        status = OrderStatus.PENDING
    )
    
    val sampleOrders = listOf(
        Order("1", "Order 1", 50.0),
        Order("2", "Order 2", 75.0),
        Order("3", "Order 3", 100.0)
    )
    
    val adminUser = User(
        id = "admin-1",
        name = "Admin User",
        role = Role.ADMIN
    )
    
    val customerUser = User(
        id = "customer-1",
        name = "Customer User",
        role = Role.CUSTOMER
    )
}
```

### Mock Implementations
```kotlin
class MockOrdersRepository(
    private val result: Result<List<Order>> = Result.success(emptyList())
) : OrdersRepository {
    
    var getOrdersCalled = false
    
    override suspend fun getOrders(): Result<List<Order>> {
        getOrdersCalled = true
        return result
    }
}

class FakeOrdersViewModel(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null
) {
    val state = MutableStateFlow(
        OrdersState(
            isLoading = isLoading,
            orders = orders,
            error = error
        )
    )
}
```

---

## Test Reporting & Coverage

### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run specific module tests
./gradlew :feature-orders:test

# Run UI tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport

# View coverage
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

### Coverage Thresholds
```kotlin
// In build.gradle.kts
jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // 80% coverage minimum
            }
        }
    }
}
```

### Coverage Gaps & Rationale
- **UI Composables**: 60% coverage acceptable (visual testing covered by screenshots)
- **DI Modules**: No coverage required (Hilt generated code)
- **Data classes**: No coverage required (no logic)
- **Preview functions**: No coverage required (dev-only)

---

## Manual Testing Checklist

### Functional Testing
- [ ] App launches successfully
- [ ] All modules load based on user role
- [ ] Navigation works between all screens
- [ ] Widgets display on dashboard
- [ ] Events propagate between modules
- [ ] Module can be added without changing app shell
- [ ] Role-based access controls enforced

### UI/UX Testing
- [ ] All screens render correctly on phone
- [ ] All screens render correctly on tablet
- [ ] Dark mode works correctly
- [ ] Animations smooth (no jank)
- [ ] Touch targets properly sized
- [ ] Accessibility: TalkBack works
- [ ] Accessibility: Text scaling works

### Performance Testing
- [ ] App starts in <3 seconds
- [ ] No ANRs during normal use
- [ ] Scrolling smooth (60fps)
- [ ] Memory usage reasonable
- [ ] No memory leaks

### Compatibility Testing
- [ ] Works on Android 8 (API 26)
- [ ] Works on Android 14 (API 34)
- [ ] Works on various screen sizes
- [ ] Works on low-end devices

---

## Bug Tracking

### Issue Template
```
**Module**: [e.g., feature-orders]
**Severity**: [Critical/High/Medium/Low]
**Type**: [Bug/Performance/UI]

**Description**:
Clear description of the issue

**Steps to Reproduce**:
1. Launch app
2. Navigate to...
3. Click on...

**Expected Behavior**:
What should happen

**Actual Behavior**:
What actually happens

**Environment**:
- Device: Pixel 6
- Android Version: 13
- App Version: 1.0.0
```

### Regression Testing
- [ ] Run full test suite before each release
- [ ] Test all module interactions
- [ ] Verify no existing features broken
- [ ] Test on multiple devices/OS versions
