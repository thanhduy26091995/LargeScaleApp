import 'dart:async';
import 'package:contracts/contracts.dart';

/// Mock implementation of [AuthService] with in-memory credential store.
///
/// Mirrors Android [AuthServiceImpl] — uses the same test accounts.
///
/// Test accounts:
/// | username | password | role     |
/// |----------|----------|----------|
/// | admin    | admin    | ADMIN    |
/// | staff    | staff    | STAFF    |
/// | customer | customer | CUSTOMER |
/// | guest    | guest    | GUEST    |
///
/// On login: publishes [UserAuthenticatedEvent] so [ModuleContext.currentRole]
/// updates automatically — no direct coupling to Wire Core needed.
class AuthServiceImpl implements AuthService {
  final EventBus _eventBus;

  AuthServiceImpl(this._eventBus);

  User? _currentUser;
  Role _currentRole = Role.GUEST;
  bool _isAuthenticated = false;

  final _roleController = StreamController<Role>.broadcast();

  static const _mockAccounts = <String, _MockAccount>{
    'admin': _MockAccount(
      password: 'admin',
      userId: 'usr-001',
      displayName: 'Admin User',
      email: 'admin@example.com',
      role: Role.ADMIN,
    ),
    'staff': _MockAccount(
      password: 'staff',
      userId: 'usr-002',
      displayName: 'Staff Member',
      email: 'staff@example.com',
      role: Role.STAFF,
    ),
    'customer': _MockAccount(
      password: 'customer',
      userId: 'usr-003',
      displayName: 'Customer',
      email: 'customer@example.com',
      role: Role.CUSTOMER,
    ),
    'guest': _MockAccount(
      password: 'guest',
      userId: 'usr-004',
      displayName: 'Guest',
      email: 'guest@example.com',
      role: Role.GUEST,
    ),
  };

  // ── AuthService ────────────────────────────────────────────────────────────

  @override
  User? get currentUserValue => _currentUser;

  @override
  Role get currentRole => _currentRole;

  @override
  bool get isAuthenticated => _isAuthenticated;

  @override
  Stream<Role> get currentRoleStream => _roleController.stream;

  @override
  Future<User> login(String username, String password) async {
    final key = username.trim().toLowerCase();
    final account = _mockAccounts[key];
    if (account == null) throw Exception("User '$username' not found");
    if (account.password != password) throw Exception('Invalid password');

    final user = User(
      id: account.userId,
      username: key,
      displayName: account.displayName,
      email: account.email,
      role: account.role,
      tenantId: 'default',
    );
    _applySession(user);
    _eventBus.publish(
      UserAuthenticatedEvent(userId: user.id, role: user.role.name),
    );
    return user;
  }

  @override
  Future<void> logout() async {
    _clearSession();
    _eventBus.publish(UserLoggedOutEvent());
  }

  @override
  Future<User?> restoreSession() async {
    // No persistence in mock — always requires fresh login.
    return null;
  }

  // ── Private ────────────────────────────────────────────────────────────────

  void _applySession(User user) {
    _currentUser = user;
    _currentRole = user.role;
    _isAuthenticated = true;
    _roleController.add(_currentRole);
  }

  void _clearSession() {
    _currentUser = null;
    _currentRole = Role.GUEST;
    _isAuthenticated = false;
    _roleController.add(_currentRole);
  }
}

class _MockAccount {
  final String password;
  final String userId;
  final String displayName;
  final String email;
  final Role role;

  const _MockAccount({
    required this.password,
    required this.userId,
    required this.displayName,
    required this.email,
    required this.role,
  });
}

/// Stub for tests — always authenticated as ADMIN.
class StubAuthService implements AuthService {
  @override
  User? get currentUserValue => null;

  @override
  Role get currentRole => Role.ADMIN;

  @override
  bool get isAuthenticated => true;

  @override
  Stream<Role> get currentRoleStream => Stream.value(Role.ADMIN);

  @override
  Future<User> login(String username, String password) async => const User(
        id: 'stub',
        username: 'stub',
        displayName: 'Stub User',
        email: 'stub@test.com',
        role: Role.ADMIN,
        tenantId: 'test',
      );

  @override
  Future<void> logout() async {}

  @override
  Future<User?> restoreSession() async => null;
}

