package com.densitech.largescale.core.network.interceptor;

import com.densitech.largescale.core.storage.StorageManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<StorageManager> storageManagerProvider;

  public AuthInterceptor_Factory(Provider<StorageManager> storageManagerProvider) {
    this.storageManagerProvider = storageManagerProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(storageManagerProvider.get());
  }

  public static AuthInterceptor_Factory create(Provider<StorageManager> storageManagerProvider) {
    return new AuthInterceptor_Factory(storageManagerProvider);
  }

  public static AuthInterceptor newInstance(StorageManager storageManager) {
    return new AuthInterceptor(storageManager);
  }
}
