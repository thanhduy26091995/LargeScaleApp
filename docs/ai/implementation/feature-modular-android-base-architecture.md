---
phase: implementation
title: Implementation Guide
description: Technical patterns and code guidelines for modular architecture
feature: modular-android-base-architecture
---

# Implementation Guide

## Development Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Kotlin 1.9.22+
- Gradle 8.2+
- Android SDK 26-36

### Environment Setup
```bash
# Clone repository
git clone <repository-url>
cd LargeScaleModule

# Open in Android Studio
# File -> Open -> Select project directory

# Sync Gradle
./gradlew clean build

# Run on device/emulator
./gradlew installDebug
```

### IDE Configuration
- Enable Kotlin auto-imports
- Install ktlint plugin
- Configure code style (use project .editorconfig)
- Enable Compose preview

## Code Structure

### Module Organization
```
:app                    # App shell only
:contracts              # Interfaces/contracts (no implementation)
:wire                   # Module orchestration
:core                   # Core services (auth, network, storage)
:shared-ui              # Design system and reusable UI
:feature-<name>         # Feature modules (independent)
```

### Package Structure (per module)
```
com.densitech.largescale.<module>
├── presentation/               # UI Layer
│   ├── screens/               # Composable screens
│   ├── components/            # Local composables
│   └── viewmodels/            # ViewModels
├── domain/                    # Business Logic
│   ├── models/                # Domain models
│   ├── usecases/              # Use cases
│   └── repositories/          # Repository interfaces
├── data/                      # Data Layer
│   ├── repositories/          # Repository implementations
│   ├── datasources/           # Remote/local data sources
│   ├── api/                   # API interfaces
│   └── mappers/               # DTO to domain mappers
└── di/                        # Dependency Injection
    └── <Module>Module.kt      # Hilt module
```

## Implementation Notes

### Pattern 1: Creating a Feature Module

#### Step 1: Create Module Structure
```kotlin
// In settings.gradle.kts
include(":feature-example")

// In feature-example/build.gradle.kts
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":contracts"))
    implementation(project(":shared-ui"))
    implementation(project(":core"))
    
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // ... other dependencies
}
```

#### Step 2: Implement AppModule Interface
```kotlin
package com.densitech.largescale.feature.example

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.UISlot
import com.densitech.largescale.contracts.Role
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExampleFeatureModule @Inject constructor() : AppModule {
    
    override val metadata = ModuleMetadata(
        id = "example",
        version = "1.0.0",
        supportedRoles = listOf(Role.ADMIN, Role.STAFF),
        dependencies = emptyList(),
        enabled = true
    )
    
    private lateinit var context: ModuleContext
    
    override fun initialize(context: ModuleContext) {
        this.context = context
        // Subscribe to events if needed
        context.eventBus.subscribe<OrderCreatedEvent> { event ->
            // Handle event
        }
    }
    
    override fun onDestroy() {
        // Cleanup
    }
    
    override val diModule: Any = ExampleDiModule::class
    
    override fun provideRoutes(): List<ModuleRoute> {
        return listOf(
            ModuleRoute(
                path = "example",
                destination = { ExampleScreen() },
                requiredRoles = metadata.supportedRoles,
                label = "Example",
                icon = Icons.Default.Star
            ),
            ModuleRoute(
                path = "example/{id}",
                destination = { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    ExampleDetailScreen(id = id)
                }
            )
        )
    }
    
    override fun provideWidgets(): List<UISlot> {
        return listOf(
            UISlot(
                slotId = "home_widgets",
                moduleId = metadata.id,
                priority = 10,
                content = { ExampleWidget() },
                requiredRoles = metadata.supportedRoles
            )
        )
    }
}
```

#### Step 3: Create Screen
```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Example") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state.items) { item ->
                ExampleItem(item = item)
            }
        }
    }
}
```

#### Step 4: Create ViewModel
```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val repository: ExampleRepository,
    private val eventBus: EventBus
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExampleState())
    val state: StateFlow<ExampleState> = _state.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            repository.getData()
                .onSuccess { data ->
                    _state.update { it.copy(items = data) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }
    
    fun onAction() {
        // Publish event
        eventBus.publish(SomeEvent(data = "value"))
    }
}

data class ExampleState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

#### Step 5: Register in App
```kotlin
// In LargeScaleApp.kt
@HiltAndroidApp
class LargeScaleApp : Application() {
    
    @Inject lateinit var moduleRegistry: ModuleRegistry
    @Inject lateinit var exampleFeatureModule: ExampleFeatureModule
    
    override fun onCreate() {
        super.onCreate()
        
        // Register modules
        moduleRegistry.register(exampleFeatureModule)
        
        // Initialize modules
        val role = getCurrentUserRole()
        moduleRegistry.initializeModules(role)
    }
}
```

---

### Pattern 2: Using Event Bus

#### Publishing Events
```kotlin
// Define event in :contracts module
sealed class ModuleEvent {
    data class OrderCreated(
        val orderId: String,
        val amount: Double,
        val timestamp: Long = System.currentTimeMillis()
    ) : ModuleEvent()
}

// Publish from any module
class OrderViewModel @Inject constructor(
    private val eventBus: EventBus
) : ViewModel() {
    
    fun createOrder(order: Order) {
        viewModelScope.launch {
            repository.createOrder(order)
            // Notify other modules
            eventBus.publish(
                ModuleEvent.OrderCreated(
                    orderId = order.id,
                    amount = order.total
                )
            )
        }
    }
}
```

#### Subscribing to Events
```kotlin
// Subscribe in module initialization
class InventoryFeatureModule @Inject constructor() : AppModule {
    
    override fun initialize(context: ModuleContext) {
        // Subscribe to order events
        context.eventBus.subscribe<ModuleEvent.OrderCreated> { event ->
            // Update inventory
            updateInventory(event.orderId)
        }.launchIn(moduleScope) // Use appropriate scope
    }
}

// Or subscribe in ViewModel
class InventoryViewModel @Inject constructor(
    private val eventBus: EventBus
) : ViewModel() {
    
    init {
        eventBus.subscribe<ModuleEvent.OrderCreated> { event ->
            _state.update {
                it.copy(recentOrder = event.orderId)
            }
        }.launchIn(viewModelScope)
    }
}
```

---

### Pattern 3: Navigation Between Modules

#### Define Routes (in :contracts)
```kotlin
sealed class AppRoute(val path: String) {
    object Dashboard : AppRoute("dashboard")
    object Orders : AppRoute("orders")
    data class OrderDetail(val orderId: String) : AppRoute("orders/$orderId")
    data class Profile(val userId: String) : AppRoute("profile/$userId")
}
```

#### Navigate from Any Module
```kotlin
@Composable
fun SomeScreen(
    navigator: AppNavigator = hiltViewModel<SomeViewModel>().navigator
) {
    Button(
        onClick = {
            navigator.navigate(AppRoute.OrderDetail("123").path)
        }
    ) {
        Text("View Order")
    }
}

// In ViewModel
class SomeViewModel @Inject constructor(
    val navigator: AppNavigator
) : ViewModel() {
    
    fun onItemClick(id: String) {
        navigator.navigate("orders/$id")
    }
}
```

---

### Pattern 4: Widget/Slot Injection

#### Host Screen (Dashboard)
```kotlin
@Composable
fun DashboardScreen(
    slotRegistry: SlotRegistry,
    userRole: Role
) {
    Column {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        
        // Host for widgets
        WidgetSlotHost(
            slotId = "home_widgets",
            slotRegistry = slotRegistry,
            userRole = userRole
        )
    }
}

@Composable
fun WidgetSlotHost(
    slotId: String,
    slotRegistry: SlotRegistry,
    userRole: Role
) {
    val widgets = remember(slotId, userRole) {
        slotRegistry.getSlotsForHost(slotId, userRole)
            .sortedByDescending { it.priority }
    }
    
    LazyColumn {
        items(widgets) { widget ->
            widget.content()
        }
    }
}
```

#### Widget Provider (Feature Module)
```kotlin
@Composable
fun OrdersWidget(
    viewModel: OrdersWidgetViewModel = hiltViewModel()
) {
    val recentOrders by viewModel.recentOrders.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Recent Orders", style = MaterialTheme.typography.titleMedium)
            recentOrders.take(3).forEach { order ->
                OrderSummaryRow(order)
            }
        }
    }
}
```

---

### Pattern 5: Dependency Injection with Hilt

#### Module-Level DI
```kotlin
// In :feature-example/di/ExampleModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class ExampleModule {
    
    @Binds
    abstract fun bindExampleRepository(
        impl: ExampleRepositoryImpl
    ): ExampleRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ExampleNetworkModule {
    
    @Provides
    @Singleton
    fun provideExampleApi(retrofit: Retrofit): ExampleApi {
        return retrofit.create(ExampleApi::class.java)
    }
}
```

#### Injecting Cross-Module Dependencies
```kotlin
// Only inject from :contracts, :core, :shared-ui
class ExampleViewModel @Inject constructor(
    private val authService: AuthService,        // From :core
    private val eventBus: EventBus,              // From :wire (via interface)
    private val navigator: AppNavigator,         // From :wire (via interface)
    private val exampleRepository: ExampleRepository // Local
) : ViewModel()
```

---

## Integration Points

### App Initialization Flow
```kotlin
@HiltAndroidApp
class LargeScaleApp : Application() {
    
    @Inject lateinit var moduleRegistry: ModuleRegistry
    @Inject lateinit var authService: AuthService
    @Inject lateinit var moduleContext: ModuleContext
    
    // Inject all feature modules
    @Inject lateinit var coreFeatureModule: CoreFeatureModule
    @Inject lateinit var dashboardModule: DashboardFeatureModule
    @Inject lateinit var ordersModule: OrdersFeatureModule
    
    override fun onCreate() {
        super.onCreate()
        initializeApp()
    }
    
    private fun initializeApp() {
        // Step 1: Register all modules
        listOf(
            coreFeatureModule,
            dashboardModule,
            ordersModule
        ).forEach { moduleRegistry.register(it) }
        
        // Step 2: Get user role (mock for now)
        val userRole = authService.getCurrentRole() ?: Role.GUEST
        
        // Step 3: Resolve enabled modules for role
        val enabledModules = moduleRegistry.resolve(userRole)
        
        // Step 4: Initialize modules
        enabledModules.forEach { module ->
            try {
                module.initialize(moduleContext)
            } catch (e: Exception) {
                Log.e("App", "Failed to initialize ${module.metadata.id}", e)
            }
        }
        
        // Step 5: Build navigation graph (done in MainActivity)
    }
}
```

### MainActivity Setup
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject lateinit var moduleRegistry: ModuleRegistry
    @Inject lateinit var slotRegistry: SlotRegistry
    @Inject lateinit var authService: AuthService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            LargeScaleModuleTheme {
                AppNavigation(
                    moduleRegistry = moduleRegistry,
                    slotRegistry = slotRegistry,
                    userRole = authService.getCurrentRole() ?: Role.GUEST
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    moduleRegistry: ModuleRegistry,
    slotRegistry: SlotRegistry,
    userRole: Role
) {
    val navController = rememberNavController()
    val modules = remember(userRole) {
        moduleRegistry.resolve(userRole)
    }
    
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        modules.forEach { module ->
            module.provideRoutes().forEach { route ->
                composable(route.path) {
                    route.destination(it)
                }
            }
        }
    }
}
```

## Error Handling

### Module Initialization Errors
```kotlin
private fun initializeModuleSafely(module: AppModule) {
    try {
        module.initialize(moduleContext)
        Log.d("App", "Module ${module.metadata.id} initialized")
    } catch (e: Exception) {
        Log.e("App", "Failed to initialize ${module.metadata.id}", e)
        // Don't crash app, just skip this module
        // Optionally show fallback UI
    }
}
```

### Repository Error Handling
```kotlin
class ExampleRepositoryImpl @Inject constructor(
    private val api: ExampleApi
) : ExampleRepository {
    
    override suspend fun getData(): Result<List<Item>> {
        return try {
            val response = api.getData()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(ApiException(response.code()))
            }
        } catch (e: IOException) {
            Result.failure(NetworkException(e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### UI Error Handling
```kotlin
@Composable
fun ExampleScreen(viewModel: ExampleViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorView(
            message = state.error,
            onRetry = { viewModel.retry() }
        )
        else -> ContentView(state.items)
    }
}
```

## Performance Considerations

### Module Initialization
```kotlin
// Use lazy initialization for heavy operations
class ExampleModule @Inject constructor() : AppModule {
    
    private val heavyService by lazy {
        // Only created when first accessed
        HeavyService()
    }
    
    override fun initialize(context: ModuleContext) {
        // Keep initialization light
        // Defer heavy work to first use
    }
}
```

### Composition Optimization
```kotlin
@Composable
fun OptimizedList(items: List<Item>) {
    // Use keys for efficient recomposition
    LazyColumn {
        items(
            items = items,
            key = { it.id }  // Stable key
        ) { item ->
            ItemRow(item)
        }
    }
}
```

### Flow Collection
```kotlin
@Composable
fun ScreenWithFlow(viewModel: MyViewModel = hiltViewModel()) {
    // Use collectAsStateWithLifecycle for automatic lifecycle management
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Not collectAsState() - that doesn't respect lifecycle
}
```

## Security Notes

### Role-Based Access
```kotlin
// Enforce role checks
class SecureNavigator @Inject constructor(
    private val authService: AuthService
) : AppNavigator {
    
    override fun navigate(route: String, args: Bundle?) {
        val currentRole = authService.getCurrentRole()
        val routeConfig = getRouteConfig(route)
        
        if (currentRole !in routeConfig.requiredRoles) {
            // Block unauthorized navigation
            Log.w("Navigation", "Access denied to $route")
            return
        }
        
        // Proceed with navigation
        navController.navigate(route, args)
    }
}
```

### Data Encryption
```kotlin
// Encrypt sensitive data in storage
class SecureStorageManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences
) {
    fun saveToken(token: String) {
        encryptedPrefs.edit()
            .putString("auth_token", token)
            .apply()
    }
}
```

## Naming Conventions

- **Modules**: `feature-<feature-name>` (e.g., `feature-orders`)
- **Packages**: `com.densitech.largescale.feature.<name>`
- **Classes**: PascalCase (`OrdersFeatureModule`)
- **Functions**: camelCase (`provideRoutes`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_ITEMS`)
- **Resources**: snake_case (`ic_orders_icon`, `string_order_title`)

## Testing Patterns

See [feature-modular-android-base-architecture-testing.md](./feature-modular-android-base-architecture-testing.md) for comprehensive testing strategy.
