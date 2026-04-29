import 'package:contracts/contracts.dart';

/// Inventory feature module — accessible to ADMIN and STAFF.
class InventoryModule extends AppModule {
  @override
  ModuleMetadata get metadata => const ModuleMetadata(
        id: 'inventory',
        name: 'Inventory',
        requiredRoles: {Role.ADMIN, Role.STAFF},
        priority: 85,
      );

  @override
  void initialize(ModuleContext context) {
    for (final slot in provideWidgets()) {
      context.slotRegistry.register(slot);
    }
  }

  @override
  List<ModuleRoute> provideRoutes() => [
        const ModuleRoute(route: '/inventory', requiredRole: Role.STAFF),
      ];
}

