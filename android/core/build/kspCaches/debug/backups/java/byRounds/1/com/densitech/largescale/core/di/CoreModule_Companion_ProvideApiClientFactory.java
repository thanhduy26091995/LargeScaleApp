package com.densitech.largescale.core.di;

import com.densitech.largescale.core.network.ApiClient;
import com.densitech.largescale.core.network.interceptor.AuthInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class CoreModule_Companion_ProvideApiClientFactory implements Factory<ApiClient> {
  private final Provider<AuthInterceptor> authInterceptorProvider;

  public CoreModule_Companion_ProvideApiClientFactory(
      Provider<AuthInterceptor> authInterceptorProvider) {
    this.authInterceptorProvider = authInterceptorProvider;
  }

  @Override
  public ApiClient get() {
    return provideApiClient(authInterceptorProvider.get());
  }

  public static CoreModule_Companion_ProvideApiClientFactory create(
      Provider<AuthInterceptor> authInterceptorProvider) {
    return new CoreModule_Companion_ProvideApiClientFactory(authInterceptorProvider);
  }

  public static ApiClient provideApiClient(AuthInterceptor authInterceptor) {
    return Preconditions.checkNotNullFromProvides(CoreModule.Companion.provideApiClient(authInterceptor));
  }
}
