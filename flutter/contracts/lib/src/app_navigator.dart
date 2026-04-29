import 'app_route.dart';

/// Navigation service contract — modules use this to navigate
/// without depending on a concrete router implementation.
abstract class AppNavigator {
  void navigate(AppRoute route);
  void goBack();
}
