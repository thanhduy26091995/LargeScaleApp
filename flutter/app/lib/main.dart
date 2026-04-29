import 'dart:async';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:get_it/get_it.dart';
import 'package:contracts/contracts.dart';
import 'package:wire/wire.dart';
import 'package:core/core.dart';
import 'package:shared_ui/shared_ui.dart';
import 'package:feature_core/feature_core.dart';
import 'package:feature_dashboard/feature_dashboard.dart';
import 'package:feature_orders/feature_orders.dart';
import 'package:feature_inventory/feature_inventory.dart';
import 'package:feature_wallet/feature_wallet.dart';

void main() {
  // 1. Bootstrap Wire Core infrastructure
  final container = WireContainer();
  final eventBus = AppEventBus();
  final slotRegistry = WireSlotRegistry();

  // 2. Register core services
  final authService = AuthServiceImpl(eventBus);
  container.register<AuthService>(authService);
  container.register<NetworkService>(StubNetworkService());
  container.register<StorageService>(StubStorageService());

  // 3. Register AuthService in GetIt so screens can resolve it
  GetIt.instance.registerSingleton<AuthService>(authService);

  // 4. Reactive role/tenant streams — seeded from AuthService
  final roleController = StreamController<Role>.broadcast();
  final tenantController = StreamController<TenantConfig?>.broadcast();
  roleController.add(authService.currentRole);

  // Connect AuthService role changes to ModuleContext role stream
  authService.currentRoleStream.listen(roleController.add);

  // 5. Build ModuleContext — gateway for every module
  GoRouter? routerRef;
  final moduleContext = ModuleContextImpl(
    eventBus: eventBus,
    slotRegistry: slotRegistry,
    roleController: roleController,
    tenantController: tenantController,
    navigateFn: (route) => routerRef?.go(route),
  );

  // 6. Register all modules (CoreFeatureModule always first at priority 1000)
  final registry = ModuleRegistry();
  registry.register(CoreFeatureModule());
  registry.register(DashboardModule());
  registry.register(OrdersModule());
  registry.register(InventoryModule());
  registry.register(WalletModule());

  // 7. Resolve active modules for current role (GUEST before login)
  final activeModules = registry.resolve(authService.currentRole, null);

  // 8. Initialize each active module with ModuleContext
  for (final module in activeModules) {
    module.initialize(moduleContext);
    eventBus.publish(ModuleInitializedEvent(moduleId: module.metadata.id));
  }

  // 9. Assemble navigation graph from all module routes
  final router = NavigationAssembler().assemble(activeModules);
  routerRef = router;

  runApp(LargeScaleApp(router: router));
}

class LargeScaleApp extends StatelessWidget {
  final GoRouter router;

  const LargeScaleApp({super.key, required this.router});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'LargeScaleApp',
      theme: AppTheme.light,
      darkTheme: AppTheme.dark,
      routerConfig: router,
    );
  }
}

