import 'package:contracts/contracts.dart';

/// Dashboard feature module — accessible to ADMIN, STAFF, and CUSTOMER.
class DashboardModule extends AppModule {
  @override
  ModuleMetadata get metadata => const ModuleMetadata(
        id: 'dashboard',
        name: 'Dashboard',
        requiredRoles: {Role.ADMIN, Role.STAFF, Role.CUSTOMER},
        priority: 100, // loads first
      );

  @override
  void initialize(ModuleContext context) {
    // Register widgets into slots
    for (final slot in provideWidgets()) {
      context.slotRegistry.register(slot);
    }
  }

  @override
  List<ModuleRoute> provideRoutes() => [
        const ModuleRoute(route: '/dashboard', requiredRole: Role.GUEST),
      ];
}

