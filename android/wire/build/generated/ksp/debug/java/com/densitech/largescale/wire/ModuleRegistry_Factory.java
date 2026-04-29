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
public final class ModuleRegistry_Factory implements Factory<ModuleRegistry> {
  @Override
  public ModuleRegistry get() {
    return newInstance();
  }

  public static ModuleRegistry_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ModuleRegistry newInstance() {
    return new ModuleRegistry();
  }

  private static final class InstanceHolder {
    private static final ModuleRegistry_Factory INSTANCE = new ModuleRegistry_Factory();
  }
}
