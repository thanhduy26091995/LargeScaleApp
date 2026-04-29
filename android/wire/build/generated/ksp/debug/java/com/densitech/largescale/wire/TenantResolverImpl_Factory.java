package com.densitech.largescale.wire;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class TenantResolverImpl_Factory implements Factory<TenantResolverImpl> {
  private final Provider<FlowEventBus> eventBusProvider;

  public TenantResolverImpl_Factory(Provider<FlowEventBus> eventBusProvider) {
    this.eventBusProvider = eventBusProvider;
  }

  @Override
  public TenantResolverImpl get() {
    return newInstance(eventBusProvider.get());
  }

  public static TenantResolverImpl_Factory create(Provider<FlowEventBus> eventBusProvider) {
    return new TenantResolverImpl_Factory(eventBusProvider);
  }

  public static TenantResolverImpl newInstance(FlowEventBus eventBus) {
    return new TenantResolverImpl(eventBus);
  }
}
