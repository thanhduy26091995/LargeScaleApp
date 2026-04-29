package com.densitech.largescale.feature.orders.ui.list;

import com.densitech.largescale.feature.orders.data.OrderRepository;
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
public final class OrdersViewModel_Factory implements Factory<OrdersViewModel> {
  private final Provider<OrderRepository> repositoryProvider;

  public OrdersViewModel_Factory(Provider<OrderRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public OrdersViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static OrdersViewModel_Factory create(Provider<OrderRepository> repositoryProvider) {
    return new OrdersViewModel_Factory(repositoryProvider);
  }

  public static OrdersViewModel newInstance(OrderRepository repository) {
    return new OrdersViewModel(repository);
  }
}
