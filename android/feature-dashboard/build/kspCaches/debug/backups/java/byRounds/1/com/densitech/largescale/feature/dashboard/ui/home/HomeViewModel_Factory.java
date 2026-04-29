package com.densitech.largescale.feature.dashboard.ui.home;

import com.densitech.largescale.contracts.AuthService;
import com.densitech.largescale.contracts.SlotRegistry;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<AuthService> authServiceProvider;

  private final Provider<SlotRegistry> slotRegistryProvider;

  public HomeViewModel_Factory(Provider<AuthService> authServiceProvider,
      Provider<SlotRegistry> slotRegistryProvider) {
    this.authServiceProvider = authServiceProvider;
    this.slotRegistryProvider = slotRegistryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(authServiceProvider.get(), slotRegistryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<AuthService> authServiceProvider,
      Provider<SlotRegistry> slotRegistryProvider) {
    return new HomeViewModel_Factory(authServiceProvider, slotRegistryProvider);
  }

  public static HomeViewModel newInstance(AuthService authService, SlotRegistry slotRegistry) {
    return new HomeViewModel(authService, slotRegistry);
  }
}
