package com.densitech.largescale.contracts

/**
 * Domain model representing an authenticated user.
 *
 * Lives in `:contracts` so any feature module can reference it
 * without depending on `:core`.
 */
data class User(
    /** Unique user identifier (UUID or backend-assigned ID). */
    val id: String,

    /** Login username / account name. */
    val username: String,

    /** Human-readable display name shown in the UI. */
    val displayName: String,

    /** User email address. */
    val email: String,

    /** Access role — drives module/screen visibility. */
    val role: Role,

    /** Tenant this user belongs to. */
    val tenantId: String,

    /** Optional avatar URL (null = use initials fallback). */
    val avatarUrl: String? = null
)
