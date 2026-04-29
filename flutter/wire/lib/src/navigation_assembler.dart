import 'package:contracts/contracts.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter/material.dart';

/// Assembles the [GoRouter] navigation graph from routes contributed by
/// all active [AppModule]s via [AppModule.provideRoutes].
///
/// Mirrors Android [NavigationAssembler] which combines [ModuleRoute] lists
/// from all initialized modules.
class NavigationAssembler {
  /// Builds a [GoRouter] from routes of all [modules].
  /// Initial location is [Routes.splash] so the splash screen runs first.
  GoRouter assemble(List<AppModule> modules) {
    final routes = <GoRoute>[];

    for (final module in modules) {
      for (final moduleRoute in module.provideRoutes()) {
        routes.add(_toGoRoute(moduleRoute));
      }
    }

    return GoRouter(
      initialLocation: Routes.splash,
      routes: routes,
    );
  }

  GoRoute _toGoRoute(ModuleRoute moduleRoute) {
    return GoRoute(
      path: moduleRoute.route,
      builder: (context, state) =>
          moduleRoute.builder != null
              ? moduleRoute.builder!(context)
              : _placeholderScreen(moduleRoute.route),
    );
  }

  Widget _placeholderScreen(String path) {
    return Scaffold(
      appBar: AppBar(title: Text(path)),
      body: Center(child: Text('Screen: $path')),
    );
  }
}
