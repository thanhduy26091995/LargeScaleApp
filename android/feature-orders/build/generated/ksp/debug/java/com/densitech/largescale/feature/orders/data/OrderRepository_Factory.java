package com.densitech.largescale.feature.orders.data;

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
public final class OrderRepository_Factory implements Factory<OrderRepository> {
  @Override
  public OrderRepository get() {
    return newInstance();
  }

  public static OrderRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OrderRepository newInstance() {
    return new OrderRepository();
  }

  private static final class InstanceHolder {
    private static final OrderRepository_Factory INSTANCE = new OrderRepository_Factory();
  }
}
