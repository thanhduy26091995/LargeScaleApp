import 'dart:async';
import 'package:contracts/contracts.dart';
import 'event_bus.dart';
import 'slot_registry.dart';

/// Concrete implementation of [ModuleContext].
///
/// Provided to each module during [AppModule.initialize]. Exposes
/// Wire Core services without leaking DI internals.
class ModuleContextImpl implements ModuleContext {
  final AppEventBus _eventBus;
  final WireSlotRegistry _slotRegistry;
  final StreamController<Role> _roleController;
  final StreamController<TenantConfig?> _tenantController;
  final void Function(String route) _navigateFn;

  ModuleContextImpl({
    required AppEventBus eventBus,
    required WireSlotRegistry slotRegistry,
    required StreamController<Role> roleController,
    required StreamController<TenantConfig?> tenantController,
    required void Function(String route) navigateFn,
  })  : _eventBus = eventBus,
        _slotRegistry = slotRegistry,
        _roleController = roleController,
        _tenantController = tenantController,
        _navigateFn = navigateFn;

  @override
  Stream<TenantConfig?> get tenantConfig => _tenantController.stream;

  @override
  Stream<Role> get currentRole => _roleController.stream;

  @override
  AppEventBus get eventBus => _eventBus;

  @override
  WireSlotRegistry get slotRegistry => _slotRegistry;

  @override
  void navigate(String route) => _navigateFn(route);
}

