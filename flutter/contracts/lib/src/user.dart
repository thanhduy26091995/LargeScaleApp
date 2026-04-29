import 'role.dart';

/// Domain model representing an authenticated user.
///
/// Mirrors Android [User] data class in contracts.
/// Lives in [contracts] so feature modules can reference it
/// without depending on [core].
class User {
  /// Unique user identifier.
  final String id;

  /// Login username / account name.
  final String username;

  /// Human-readable display name shown in the UI.
  final String displayName;

  /// User email address.
  final String email;

  /// Access role — drives module/screen visibility.
  final Role role;

  /// Tenant this user belongs to.
  final String tenantId;

  /// Optional avatar URL (null = use initials fallback).
  final String? avatarUrl;

  const User({
    required this.id,
    required this.username,
    required this.displayName,
    required this.email,
    required this.role,
    required this.tenantId,
    this.avatarUrl,
  });

  User copyWith({
    String? id,
    String? username,
    String? displayName,
    String? email,
    Role? role,
    String? tenantId,
    String? avatarUrl,
  }) =>
      User(
        id: id ?? this.id,
        username: username ?? this.username,
        displayName: displayName ?? this.displayName,
        email: email ?? this.email,
        role: role ?? this.role,
        tenantId: tenantId ?? this.tenantId,
        avatarUrl: avatarUrl ?? this.avatarUrl,
      );

  @override
  String toString() =>
      'User(id: $id, username: $username, role: $role, tenantId: $tenantId)';
}
