import 'package:contracts/contracts.dart';
import 'package:wire/wire.dart';
import '../data/orders_repository_impl.dart';
import '../domain/orders_repository.dart';
import '../domain/get_orders_use_case.dart';

/// Orders feature module — accessible to ADMIN and STAFF.
class OrdersModule extends AppModule {
  late WireContainer _container;

  @override
  ModuleMetadata get metadata => const ModuleMetadata(
        id: 'orders',
        name: 'Orders',
        requiredRoles: {Role.ADMIN, Role.STAFF},
        priority: 90,
      );

  @override
  void initialize(ModuleContext context) {
    _container = WireContainer();
    _container.register<OrdersRepository>(OrdersRepositoryImpl());
    _container.register<GetOrdersUseCase>(
      GetOrdersUseCase(_container.resolve<OrdersRepository>()),
    );

    // React to auth events
    context.eventBus.on<UserAuthenticatedEvent>().listen((_) {
      // Re-fetch orders for newly authenticated user
    });

    // Register widgets into slots
    for (final slot in provideWidgets()) {
      context.slotRegistry.register(slot);
    }
  }

  @override
  void onDestroy() {
    // SlotRegistry.clearModule is called by the registry orchestrator
  }

  @override
  List<ModuleRoute> provideRoutes() => [
        const ModuleRoute(route: '/orders', requiredRole: Role.STAFF),
      ];
}

