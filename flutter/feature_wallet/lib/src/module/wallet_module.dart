import 'package:contracts/contracts.dart';

/// Wallet feature module — accessible to ADMIN, STAFF, and CUSTOMER.
class WalletModule extends AppModule {
  @override
  ModuleMetadata get metadata => const ModuleMetadata(
        id: 'wallet',
        name: 'Wallet',
        requiredRoles: {Role.ADMIN, Role.STAFF, Role.CUSTOMER},
        priority: 80,
      );

  @override
  void initialize(ModuleContext context) {
    for (final slot in provideWidgets()) {
      context.slotRegistry.register(slot);
    }
  }

  @override
  List<ModuleRoute> provideRoutes() => [
        const ModuleRoute(route: '/wallet', requiredRole: Role.CUSTOMER),
      ];
}

