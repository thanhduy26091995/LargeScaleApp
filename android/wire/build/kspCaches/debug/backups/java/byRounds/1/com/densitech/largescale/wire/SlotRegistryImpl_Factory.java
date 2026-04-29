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
public final class SlotRegistryImpl_Factory implements Factory<SlotRegistryImpl> {
  @Override
  public SlotRegistryImpl get() {
    return newInstance();
  }

  public static SlotRegistryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SlotRegistryImpl newInstance() {
    return new SlotRegistryImpl();
  }

  private static final class InstanceHolder {
    private static final SlotRegistryImpl_Factory INSTANCE = new SlotRegistryImpl_Factory();
  }
}
