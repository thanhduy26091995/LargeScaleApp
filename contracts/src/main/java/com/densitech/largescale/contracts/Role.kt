package com.densitech.largescale.contracts

/**
 * Role-based access control for modules.
 * Determines which modules and features a user can access.
 */
enum class Role {
    /**
     * Full access to all modules and administrative features.
     */
    ADMIN,

    /**
     * Business operations access (orders, inventory, etc.).
     */
    STAFF,

    /**
     * Customer-facing features only.
     */
    CUSTOMER,

    /**
     * Public features only (login, registration, public content).
     */
    GUEST
}
