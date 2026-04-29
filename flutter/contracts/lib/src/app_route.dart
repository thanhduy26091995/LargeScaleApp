/// Sealed class representing all navigable routes in the app.
sealed class AppRoute {
  final String path;
  const AppRoute(this.path);
}

final class DashboardRoute extends AppRoute {
  const DashboardRoute() : super('/dashboard');
}

final class OrdersRoute extends AppRoute {
  const OrdersRoute() : super('/orders');
}

final class InventoryRoute extends AppRoute {
  const InventoryRoute() : super('/inventory');
}

final class WalletRoute extends AppRoute {
  const WalletRoute() : super('/wallet');
}
