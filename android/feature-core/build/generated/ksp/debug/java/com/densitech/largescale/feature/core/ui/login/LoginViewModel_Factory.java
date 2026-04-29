package com.densitech.largescale.feature.core.ui.login;

import com.densitech.largescale.contracts.AuthService;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<AuthService> authServiceProvider;

  public LoginViewModel_Factory(Provider<AuthService> authServiceProvider) {
    this.authServiceProvider = authServiceProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authServiceProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<AuthService> authServiceProvider) {
    return new LoginViewModel_Factory(authServiceProvider);
  }

  public static LoginViewModel newInstance(AuthService authService) {
    return new LoginViewModel(authService);
  }
}
