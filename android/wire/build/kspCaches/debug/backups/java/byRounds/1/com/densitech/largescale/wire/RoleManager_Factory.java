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
public final class RoleManager_Factory implements Factory<RoleManager> {
  private final Provider<FlowEventBus> eventBusProvider;

  public RoleManager_Factory(Provider<FlowEventBus> eventBusProvider) {
    this.eventBusProvider = eventBusProvider;
  }

  @Override
  public RoleManager get() {
    return newInstance(eventBusProvider.get());
  }

  public static RoleManager_Factory create(Provider<FlowEventBus> eventBusProvider) {
    return new RoleManager_Factory(eventBusProvider);
  }

  public static RoleManager newInstance(FlowEventBus eventBus) {
    return new RoleManager(eventBus);
  }
}
