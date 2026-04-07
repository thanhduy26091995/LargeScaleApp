package com.densitech.largescale.wire

import android.content.Context
import com.densitech.largescale.contracts.AppNavigator
import com.densitech.largescale.contracts.EventBus
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.SlotRegistry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that wires up all Wire Core services.
 *
 * Provides:
 * - [EventBus] → [FlowEventBus]
 * - [SlotRegistry] → [SlotRegistryImpl]
 * - [AppNavigator] → [AppNavigatorImpl]
 * - [TenantResolverImpl] (concrete, injected into [ModuleContext])
 * - [RoleManager] (concrete, injected into [ModuleContext])
 * - [ModuleContext] → [ModuleContextImpl]
 * - [ModuleRegistry] (self-binding singleton via @Inject constructor)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class WireCoreModule {

    // ── Interface bindings ────────────────────────────────────────────────────

    @Binds
    @Singleton
    abstract fun bindEventBus(impl: FlowEventBus): EventBus

    @Binds
    @Singleton
    abstract fun bindSlotRegistry(impl: SlotRegistryImpl): SlotRegistry

    @Binds
    @Singleton
    abstract fun bindAppNavigator(impl: AppNavigatorImpl): AppNavigator

    // ── Object provisions ─────────────────────────────────────────────────────

    companion object {

        /**
         * Provide the [ModuleContext] used by every feature module.
         *
         * [TenantResolverImpl] and [RoleManager] are concrete singletons (not interface-bound)
         * so their [StateFlow]s can be extracted directly here.
         */
        @Provides
        @Singleton
        fun provideModuleContext(
            @ApplicationContext context: Context,
            eventBus: EventBus,
            slotRegistry: SlotRegistry,
            navigator: AppNavigator,
            tenantResolver: TenantResolverImpl,
            roleManager: RoleManager
        ): ModuleContext = ModuleContextImpl(
            appContext = context,
            eventBus = eventBus,
            slotRegistry = slotRegistry,
            navigator = navigator,
            tenantFlow = tenantResolver.currentTenant,
            roleFlow = roleManager.currentRole
        )
    }
}
