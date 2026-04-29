import 'package:flutter/foundation.dart';
import 'package:contracts/contracts.dart';

/// UI state for the login screen.
class LoginUiState {
  final String username;
  final String password;
  final bool isLoading;
  final String? errorMessage;

  const LoginUiState({
    this.username = '',
    this.password = '',
    this.isLoading = false,
    this.errorMessage,
  });

  LoginUiState copyWith({
    String? username,
    String? password,
    bool? isLoading,
    String? errorMessage,
    bool clearError = false,
  }) =>
      LoginUiState(
        username: username ?? this.username,
        password: password ?? this.password,
        isLoading: isLoading ?? this.isLoading,
        errorMessage: clearError ? null : (errorMessage ?? this.errorMessage),
      );
}

/// Controller for [LoginScreen].
///
/// Uses [ChangeNotifier] so [ListenableBuilder] can rebuild efficiently.
/// Mirrors Android [LoginViewModel] — [AuthService] is injected by the caller.
class LoginController extends ChangeNotifier {
  final AuthService _authService;

  LoginController(this._authService);

  LoginUiState _state = const LoginUiState();
  LoginUiState get state => _state;

  void onUsernameChange(String value) {
    _state = _state.copyWith(username: value, clearError: true);
    notifyListeners();
  }

  void onPasswordChange(String value) {
    _state = _state.copyWith(password: value, clearError: true);
    notifyListeners();
  }

  Future<void> login(VoidCallback onSuccess) async {
    if (_state.username.trim().isEmpty || _state.password.isEmpty) {
      _state =
          _state.copyWith(errorMessage: 'Please enter username and password');
      notifyListeners();
      return;
    }

    _state = _state.copyWith(isLoading: true, clearError: true);
    notifyListeners();

    try {
      await _authService.login(_state.username.trim(), _state.password);
      onSuccess();
    } catch (e) {
      _state = _state.copyWith(
        isLoading: false,
        errorMessage: e.toString().replaceFirst('Exception: ', ''),
      );
      notifyListeners();
    }
  }
}
