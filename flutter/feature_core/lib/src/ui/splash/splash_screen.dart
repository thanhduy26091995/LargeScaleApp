import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:contracts/contracts.dart';
import 'package:go_router/go_router.dart';

/// Splash screen — restores session on launch then navigates.
///
/// Navigates to [Routes.home] if a session is restored, [Routes.login] otherwise.
/// Mirrors Android [SplashScreen].
class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _checkSession();
  }

  Future<void> _checkSession() async {
    // Brief pause for splash visibility.
    await Future<void>.delayed(const Duration(milliseconds: 500));
    if (!mounted) return;

    final authService = GetIt.instance<AuthService>();
    final user = await authService.restoreSession();
    if (!mounted) return;

    context.go(user != null ? Routes.home : Routes.login);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'LargeScale',
              style: Theme.of(context).textTheme.headlineLarge?.copyWith(
                    color: Theme.of(context).colorScheme.primary,
                  ),
            ),
            const SizedBox(height: 8),
            Text(
              'Modular · Multi-tenant',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
            ),
            const SizedBox(height: 32),
            const CircularProgressIndicator(),
          ],
        ),
      ),
    );
  }
}
