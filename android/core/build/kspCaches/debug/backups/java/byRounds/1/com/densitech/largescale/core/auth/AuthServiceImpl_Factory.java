package com.densitech.largescale.core.auth;

import com.densitech.largescale.contracts.EventBus;
import com.densitech.largescale.core.storage.StorageManager;
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
public final class AuthServiceImpl_Factory implements Factory<AuthServiceImpl> {
  private final Provider<EventBus> eventBusProvider;

  private final Provider<StorageManager> storageManagerProvider;

  public AuthServiceImpl_Factory(Provider<EventBus> eventBusProvider,
      Provider<StorageManager> storageManagerProvider) {
    this.eventBusProvider = eventBusProvider;
    this.storageManagerProvider = storageManagerProvider;
  }

  @Override
  public AuthServiceImpl get() {
    return newInstance(eventBusProvider.get(), storageManagerProvider.get());
  }

  public static AuthServiceImpl_Factory create(Provider<EventBus> eventBusProvider,
      Provider<StorageManager> storageManagerProvider) {
    return new AuthServiceImpl_Factory(eventBusProvider, storageManagerProvider);
  }

  public static AuthServiceImpl newInstance(EventBus eventBus, StorageManager storageManager) {
    return new AuthServiceImpl(eventBus, storageManager);
  }
}
