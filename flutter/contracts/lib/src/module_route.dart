import 'package:flutter/widgets.dart';
import 'role.dart';

/// A navigation route contributed by a module.
///
/// Mirrors Android [ModuleRoute]. Modules return a list from [AppModule.provideRoutes].
/// The Wire Core [NavigationAssembler] aggregates all routes into the app router.
class ModuleRoute {
  /// The route path string (e.g., '/orders', '/orders/:id').
  final String route;

  /// Minimum role required to navigate to this route.
  final Role requiredRole;

  /// Optional screen builder. When provided, [NavigationAssembler] renders this
  /// widget instead of a placeholder. Null = placeholder screen during development.
  final Widget Function(BuildContext context)? builder;

  const ModuleRoute({
    required this.route,
    this.requiredRole = Role.GUEST,
    this.builder,
  });
}
