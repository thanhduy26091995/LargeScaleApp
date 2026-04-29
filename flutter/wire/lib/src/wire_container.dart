import 'package:get_it/get_it.dart';

/// Dependency injection container backed by [GetIt].
///
/// Feature modules call [register] during their [AppModule.register] phase.
/// Services are later resolved via [resolve].
class WireContainer {
  final GetIt _locator;

  WireContainer({GetIt? locator}) : _locator = locator ?? GetIt.instance;

  /// Register a singleton [instance] of type [T].
  void register<T extends Object>(T instance) {
    if (!_locator.isRegistered<T>()) {
      _locator.registerSingleton<T>(instance);
    }
  }

  /// Register a lazy singleton factory of type [T].
  void registerFactory<T extends Object>(T Function() factory) {
    if (!_locator.isRegistered<T>()) {
      _locator.registerLazySingleton<T>(factory);
    }
  }

  /// Resolve a registered service of type [T].
  /// Throws [StateError] if [T] is not registered.
  T resolve<T extends Object>() {
    assert(_locator.isRegistered<T>(),
        'WireContainer: $T is not registered. Did you call register<$T>() in your AppModule?');
    return _locator<T>();
  }
}
