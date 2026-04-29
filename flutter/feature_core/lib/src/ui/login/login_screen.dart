import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:contracts/contracts.dart';
import 'package:go_router/go_router.dart';
import 'login_controller.dart';

/// Login screen — username + password form backed by [LoginController].
///
/// Mirrors Android [LoginScreen]. Resolves [AuthService] from [GetIt] so it
/// can be placed anywhere in the navigation graph without a BuildContext ancestor.
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  late final LoginController _controller;

  @override
  void initState() {
    super.initState();
    _controller = LoginController(GetIt.instance<AuthService>());
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListenableBuilder(
        listenable: _controller,
        builder: (context, _) {
          final state = _controller.state;
          return SafeArea(
            child: Center(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Text(
                      'Welcome back',
                      style: Theme.of(context).textTheme.headlineMedium,
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Sign in to continue',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: Theme.of(context).colorScheme.onSurfaceVariant,
                          ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 40),
                    TextField(
                      decoration: const InputDecoration(
                        labelText: 'Username',
                        border: OutlineInputBorder(),
                      ),
                      textInputAction: TextInputAction.next,
                      onChanged: _controller.onUsernameChange,
                    ),
                    const SizedBox(height: 16),
                    _PasswordField(
                      errorMessage: state.errorMessage,
                      onChanged: _controller.onPasswordChange,
                      onSubmitted: (_) =>
                          _controller.login(() => context.go(Routes.home)),
                    ),
                    const SizedBox(height: 24),
                    FilledButton(
                      onPressed: state.isLoading
                          ? null
                          : () => _controller.login(
                                () => context.go(Routes.home),
                              ),
                      child: state.isLoading
                          ? const SizedBox(
                              height: 20,
                              width: 20,
                              child: CircularProgressIndicator(strokeWidth: 2),
                            )
                          : const Text('Sign In'),
                    ),
                    const SizedBox(height: 32),
                    Text(
                      'Test accounts: admin/admin · staff/staff · customer/customer',
                      style: Theme.of(context).textTheme.labelSmall?.copyWith(
                            color: Theme.of(context).colorScheme.onSurfaceVariant,
                          ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}

class _PasswordField extends StatefulWidget {
  final String? errorMessage;
  final ValueChanged<String> onChanged;
  final ValueChanged<String> onSubmitted;

  const _PasswordField({
    required this.errorMessage,
    required this.onChanged,
    required this.onSubmitted,
  });

  @override
  State<_PasswordField> createState() => _PasswordFieldState();
}

class _PasswordFieldState extends State<_PasswordField> {
  bool _obscure = true;

  @override
  Widget build(BuildContext context) {
    return TextField(
      obscureText: _obscure,
      decoration: InputDecoration(
        labelText: 'Password',
        border: const OutlineInputBorder(),
        errorText: widget.errorMessage,
        suffixIcon: IconButton(
          icon: Icon(_obscure ? Icons.visibility_off : Icons.visibility),
          onPressed: () => setState(() => _obscure = !_obscure),
        ),
      ),
      textInputAction: TextInputAction.done,
      onChanged: widget.onChanged,
      onSubmitted: widget.onSubmitted,
    );
  }
}
