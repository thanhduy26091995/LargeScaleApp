/// Standard navigation routes used across the application.
///
/// Mirrors Android [Routes] object. Feature modules should use these constants
/// when declaring [ModuleRoute] paths and calling [ModuleContext.navigate].
abstract final class Routes {
  // ── Core routes (always available) ────────────────────────────────────────
  static const splash = '/splash';
  static const login = '/login';
  static const register = '/register';
  static const settings = '/settings';

  // ── Dashboard ──────────────────────────────────────────────────────────────
  static const home = '/home';
  static const profile = '/profile';

  // ── Orders ─────────────────────────────────────────────────────────────────
  static const orders = '/orders';
  static const orderDetail = '/orders/:orderId';
  static const orderCreate = '/orders/create';

  // ── Inventory ──────────────────────────────────────────────────────────────
  static const inventory = '/inventory';
  static const inventoryDetail = '/inventory/:itemId';

  // ── Wallet ─────────────────────────────────────────────────────────────────
  static const wallet = '/wallet';
}
