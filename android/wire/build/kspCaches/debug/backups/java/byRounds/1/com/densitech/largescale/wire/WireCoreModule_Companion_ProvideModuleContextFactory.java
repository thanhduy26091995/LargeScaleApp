package com.densitech.largescale.wire;

import android.content.Context;
import com.densitech.largescale.contracts.AppNavigator;
import com.densitech.largescale.contracts.EventBus;
import com.densitech.largescale.contracts.ModuleContext;
import com.densitech.largescale.contracts.SlotRegistry;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class WireCoreModule_Companion_ProvideModuleContextFactory implements Factory<ModuleContext> {
  private final Provider<Context> contextProvider;

  private final Provider<EventBus> eventBusProvider;

  private final Provider<SlotRegistry> slotRegistryProvider;

  private final Provider<AppNavigator> navigatorProvider;

  private final Provider<TenantResolverImpl> tenantResolverProvider;

  private final Provider<RoleManager> roleManagerProvider;

  public WireCoreModule_Companion_ProvideModuleContextFactory(Provider<Context> contextProvider,
      Provider<EventBus> eventBusProvider, Provider<SlotRegistry> slotRegistryProvider,
      Provider<AppNavigator> navigatorProvider, Provider<TenantResolverImpl> tenantResolverProvider,
      Provider<RoleManager> roleManagerProvider) {
    this.contextProvider = contextProvider;
    this.eventBusProvider = eventBusProvider;
    this.slotRegistryProvider = slotRegistryProvider;
    this.navigatorProvider = navigatorProvider;
    this.tenantResolverProvider = tenantResolverProvider;
    this.roleManagerProvider = roleManagerProvider;
  }

  @Override
  public ModuleContext get() {
    return provideModuleContext(contextProvider.get(), eventBusProvider.get(), slotRegistryProvider.get(), navigatorProvider.get(), tenantResolverProvider.get(), roleManagerProvider.get());
  }

  public static WireCoreModule_Companion_ProvideModuleContextFactory create(
      Provider<Context> contextProvider, Provider<EventBus> eventBusProvider,
      Provider<SlotRegistry> slotRegistryProvider, Provider<AppNavigator> navigatorProvider,
      Provider<TenantResolverImpl> tenantResolverProvider,
      Provider<RoleManager> roleManagerProvider) {
    return new WireCoreModule_Companion_ProvideModuleContextFactory(contextProvider, eventBusProvider, slotRegistryProvider, navigatorProvider, tenantResolverProvider, roleManagerProvider);
  }

  public static ModuleContext provideModuleContext(Context context, EventBus eventBus,
      SlotRegistry slotRegistry, AppNavigator navigator, TenantResolverImpl tenantResolver,
      RoleManager roleManager) {
    return Preconditions.checkNotNullFromProvides(WireCoreModule.Companion.provideModuleContext(context, eventBus, slotRegistry, navigator, tenantResolver, roleManager));
  }
}
