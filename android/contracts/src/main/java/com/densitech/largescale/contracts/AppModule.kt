package com.densitech.largescale.contracts

/**
 * Core interface that all feature modules must implement.
 * Provides module metadata, lifecycle hooks, and contribution points for the Wire Core.
 */
interface AppModule {
    /**
     * Metadata describing this module.
     */
    val metadata: ModuleMetadata

    /**
     * Initialize the module with the provided context.
     * Called once during app startup after Wire Core is ready.
     *
     * Use this to:
     * - Subscribe to events from EventBus
     * - Set up module-specific services
     * - Register background workers
     *
     * @param context Provides access to Wire Core services
     */
    fun initialize(context: ModuleContext)

    /**
     * Cleanup resources when the module is destroyed.
     * Called during app shutdown or when the module is dynamically unloaded.
     */
    fun onDestroy() {
        // Default empty implementation
    }

    /**
     * Provide navigation routes for this module.
     * These routes will be registered with the AppNavigator.
     *
     * @return List of navigation routes exposed by this module
     */
    fun provideRoutes(): List<ModuleRoute> = emptyList()

    /**
     * Provide UI widgets/slots for dynamic composition.
     * These widgets can be composed into slot hosts (e.g., dashboard widgets).
     *
     * @return List of UI slots provided by this module
     */
    fun provideWidgets(): List<UISlot> = emptyList()
}
