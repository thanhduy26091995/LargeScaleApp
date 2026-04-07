package com.densitech.largescale.wire

import android.content.Context
import com.densitech.largescale.contracts.AppNavigator
import com.densitech.largescale.contracts.EventBus
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.SlotRegistry
import com.densitech.largescale.contracts.TenantConfig
import kotlinx.coroutines.flow.StateFlow

/**
 * Concrete implementation of [ModuleContext].
 *
 * Acts as the single gateway each feature module uses to reach Wire Core services.
 * All fields are injected as singletons by [WireCoreModule], so every module
 * receives the same shared instances.
 *
 * Construction is handled by Hilt — see [WireCoreModule.provideModuleContext].
 */
class ModuleContextImpl(
    private val appContext: Context,
    override val eventBus: EventBus,
    override val slotRegistry: SlotRegistry,
    private val navigator: AppNavigator,
    tenantFlow: StateFlow<TenantConfig?>,
    roleFlow: StateFlow<Role>
) : ModuleContext {

    override val tenantConfig: StateFlow<TenantConfig?> = tenantFlow
    override val currentRole: StateFlow<Role> = roleFlow

    override fun navigate(route: String) = navigator.navigate(route)

    override fun getApplicationContext(): Any = appContext
}
