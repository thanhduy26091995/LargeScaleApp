import 'dart:async';
import 'package:contracts/contracts.dart';
import 'ui/login/login_screen.dart';
import 'ui/splash/splash_screen.dart';

/// Core infrastructure module — handles authentication UI and app entry points.
///
/// Provides [Routes.splash] and [Routes.login] (always available, no role
/// restriction). Listens for [UserLoggedOutEvent] to redirect back to login.
///
/// Priority 1000 — initialized first so navigation entry point is ready
/// before any feature module routes are registered.
///
/// Mirrors Android [CoreFeatureModule].
class CoreFeatureModule extends AppModule {
  @override
  final ModuleMetadata metadata = const ModuleMetadata(
    id: 'core',
    name: 'Core',
    version: '1.0.0',
    requiredRoles: {Role.ADMIN, Role.STAFF, Role.CUSTOMER, Role.GUEST},
    priority: 1000,
  );

  StreamSubscription<UserLoggedOutEvent>? _logoutSub;

  @override
  void initialize(ModuleContext context) {
    _logoutSub = context.eventBus
        .on<UserLoggedOutEvent>()
        .listen((_) => context.navigate(Routes.login));
  }

  @override
  List<ModuleRoute> provideRoutes() => [
        ModuleRoute(
          route: Routes.splash,
          requiredRole: Role.GUEST,
          builder: (context) => const SplashScreen(),
        ),
        ModuleRoute(
          route: Routes.login,
          requiredRole: Role.GUEST,
          builder: (context) => const LoginScreen(),
        ),
      ];

  @override
  List<UISlot> provideWidgets() => [];

  @override
  void onDestroy() {
    _logoutSub?.cancel();
    _logoutSub = null;
  }
}
