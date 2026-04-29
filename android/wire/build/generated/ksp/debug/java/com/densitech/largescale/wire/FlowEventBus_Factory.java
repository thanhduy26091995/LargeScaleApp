package com.densitech.largescale.wire;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FlowEventBus_Factory implements Factory<FlowEventBus> {
  @Override
  public FlowEventBus get() {
    return newInstance();
  }

  public static FlowEventBus_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FlowEventBus newInstance() {
    return new FlowEventBus();
  }

  private static final class InstanceHolder {
    private static final FlowEventBus_Factory INSTANCE = new FlowEventBus_Factory();
  }
}
