package com.densitech.largescale.core.storage;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
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
public final class StorageManager_Factory implements Factory<StorageManager> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public StorageManager_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public StorageManager get() {
    return newInstance(dataStoreProvider.get());
  }

  public static StorageManager_Factory create(Provider<DataStore<Preferences>> dataStoreProvider) {
    return new StorageManager_Factory(dataStoreProvider);
  }

  public static StorageManager newInstance(DataStore<Preferences> dataStore) {
    return new StorageManager(dataStore);
  }
}
