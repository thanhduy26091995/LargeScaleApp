import 'user.dart';
import 'role.dart';

/// Authentication service — single source of truth for user identity.
///
/// Lives in [contracts] so feature modules can inject it without depending on [core].
/// Mirrors Android [AuthService] interface.
///
/// Wire Core reacts to [UserAuthenticatedEvent]/[UserLoggedOutEvent] published
/// here to update [ModuleContext.currentRole] automatically.
abstract class AuthService {
  /// Currently authenticated user, or null when logged out.
  User? get currentUserValue;

  /// Current role — mirrors [currentUserValue]?.role, defaults to [Role.GUEST].
  Role get currentRole;

  /// True when a user is signed in.
  bool get isAuthenticated;

  /// Stream of role changes — used by [ModuleContext] to react to auth events.
  Stream<Role> get currentRoleStream;

  /// Attempt to authenticate with [username] and [password].
  ///
  /// Returns the authenticated [User] on success.
  /// Throws with a descriptive message on failure.
  Future<User> login(String username, String password);

  /// Sign out the current user.
  /// Resets role to [Role.GUEST] and publishes [UserLoggedOutEvent].
  Future<void> logout();

  /// Restore a previously persisted session (called on app start).
  /// Returns the restored [User] or null if no valid session exists.
  Future<User?> restoreSession();
}
