package com.densitech.largescale.feature.orders.ui.widget;

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
public final class OrdersWidgetViewModel_Factory implements Factory<OrdersWidgetViewModel> {
  private final Provider<OrderRepository> repositoryProvider;

  public OrdersWidgetViewModel_Factory(Provider<OrderRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public OrdersWidgetViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static OrdersWidgetViewModel_Factory create(Provider<OrderRepository> repositoryProvider) {
    return new OrdersWidgetViewModel_Factory(repositoryProvider);
  }

  public static OrdersWidgetViewModel newInstance(OrderRepository repository) {
    return new OrdersWidgetViewModel(repository);
  }
}
