# Guide 01 — Creating a New Feature Module

This guide walks through adding a fully-integrated feature module from scratch.
Goal: create `:feature-profile` that shows a user profile screen and contributes a quick-action widget to the dashboard.

---

## Step 1: Create the Gradle module

### 1a. Add to `settings.gradle.kts`

```kotlin
include(":feature-profile")
project(":feature-profile").projectDir = File("feature-profile")
```

### 1b. Create `feature-profile/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.densitech.largescale.feature.profile"
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

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
```

### 1c. Create `feature-profile/src/main/AndroidManifest.xml`

```xml
<manifest />
```

### 1d. Sync Gradle

Android Studio → **File → Sync Project with Gradle Files**

---

## Step 2: Implement the AppModule entry point

```
feature-profile/src/main/java/com/densitech/largescale/feature/profile/
└── ProfileFeatureModule.kt
```

```kotlin
package com.densitech.largescale.feature.profile

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.contracts.SlotIds
import com.densitech.largescale.contracts.UISlot

class ProfileFeatureModule : AppModule {

    override val metadata = ModuleMetadata(
        id           = "profile",
        name         = "Profile",
        version      = "1.0.0",
        requiredRoles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER), // not GUEST
        priority     = 750
    )

    override fun initialize(context: ModuleContext) {
        // Register a quick-action widget into the dashboard HOME_WIDGETS slot
        context.slotRegistry.register(
            UISlot(
                slotId       = SlotIds.HOME_QUICK_ACTIONS,
                widgetId     = "profile-quick-action",
                moduleId     = "profile",
                priority     = 750,
                requiredRole = Role.CUSTOMER,
                content      = { ProfileQuickActionWidget(onNavigate = { context.navigate(Routes.PROFILE) }) }
            )
        )
    }

    override fun provideRoutes() = listOf(
        ModuleRoute(route = Routes.PROFILE, requiredRole = Role.CUSTOMER)
    )

    override fun provideWidgets(): List<UISlot> = emptyList() // registered in initialize()
}
```

> **Why register in `initialize()` instead of `provideWidgets()`?**
> `initialize()` receives a live `ModuleContext`, so widgets can capture navigation lambdas.
> `provideWidgets()` is for static widgets that don't need context injection.

---

## Step 3: Add the route constant

Open [contracts/src/main/java/com/densitech/largescale/contracts/Routes.kt](../../contracts/src/main/java/com/densitech/largescale/contracts/Routes.kt) and add:

```kotlin
const val PROFILE = "profile"
```

---

## Step 4: Create the screen and ViewModel

```
feature-profile/src/main/java/.../feature/profile/
├── ui/
│   └── profile/
│       ├── ProfileScreen.kt
│       └── ProfileViewModel.kt
└── nav/
    └── ProfileNavGraph.kt
```

### ProfileViewModel.kt

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    val user: StateFlow<User?> = authService.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
```

### ProfileScreen.kt

```kotlin
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    // ... your UI here
}
```

### ProfileNavGraph.kt

```kotlin
fun NavGraphBuilder.profileNavGraph(navigator: AppNavigator) {
    composable(Routes.PROFILE) {
        ProfileScreen(onBack = { navigator.navigateBack() })
    }
}
```

---

## Step 5: Register the module in the App

Open [app/src/main/java/com/densitech/largescale/LargeScaleApp.kt](../../app/src/main/java/com/densitech/largescale/LargeScaleApp.kt):

```kotlin
private fun registerModules() {
    moduleRegistry.register(CoreFeatureModule())
    moduleRegistry.register(DashboardFeatureModule())
    moduleRegistry.register(OrdersFeatureModule())
    moduleRegistry.register(InventoryFeatureModule())
    moduleRegistry.register(WalletFeatureModule())
    moduleRegistry.register(ProfileFeatureModule())   // ← add this
}
```

---

## Step 6: Add the nav graph to MainActivity

Open [app/src/main/java/com/densitech/largescale/MainActivity.kt](../../app/src/main/java/com/densitech/largescale/MainActivity.kt):

```kotlin
NavHost(navController = navController, startDestination = Routes.SPLASH) {
    coreNavGraph(navigator, authService)
    dashboardNavGraph(navigator)
    ordersNavGraph(navigator)
    inventoryNavGraph(navigator)
    walletNavGraph(navigator)
    profileNavGraph(navigator)   // ← add this
}
```

---

## Step 7: Write tests

Create `feature-profile/src/test/java/.../feature/profile/ProfileFeatureModuleTest.kt`:

```kotlin
class ProfileFeatureModuleTest {

    @Test
    fun `module requires CUSTOMER or higher role`() {
        val module = ProfileFeatureModule()
        assertTrue(Role.CUSTOMER in module.metadata.requiredRoles)
        assertFalse(Role.GUEST in module.metadata.requiredRoles)
    }

    @Test
    fun `provideRoutes includes PROFILE route`() {
        val routes = ProfileFeatureModule().provideRoutes().map { it.route }
        assertTrue(Routes.PROFILE in routes)
    }
}
```

---

## Checklist

- [ ] Module added to `settings.gradle.kts`
- [ ] `build.gradle.kts` created with correct dependencies
- [ ] `AndroidManifest.xml` created (empty)
- [ ] `ProfileFeatureModule` implements `AppModule`
- [ ] Route constant added to `Routes.kt`
- [ ] Screen + ViewModel created
- [ ] NavGraph extension function created
- [ ] Module registered in `LargeScaleApp.registerModules()`
- [ ] NavGraph added to `MainActivity`
- [ ] Unit tests written

> A new module following this guide should take **less than 30 minutes** to scaffold.
