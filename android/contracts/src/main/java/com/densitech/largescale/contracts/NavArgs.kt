package com.densitech.largescale.contracts

/**
 * Helper class for extracting navigation arguments.
 * Provides type-safe access to route parameters and query arguments.
 *
 * Usage in a Composable:
 * ```
 * @Composable
 * fun OrderDetailScreen(navArgs: NavArgs) {
 *     val orderId = navArgs.getString("orderId") ?: return
 *     // Load order details
 * }
 * ```
 */
class NavArgs(
    private val pathParams: Map<String, String> = emptyMap(),
    private val queryParams: Map<String, String> = emptyMap()
) {
    /**
     * Get a string argument by key.
     */
    fun getString(key: String): String? {
        return pathParams[key] ?: queryParams[key]
    }

    /**
     * Get a required string argument.
     * Throws IllegalArgumentException if not found.
     */
    fun getRequiredString(key: String): String {
        return getString(key) ?: throw IllegalArgumentException("Required argument '$key' not found")
    }

    /**
     * Get an integer argument by key.
     */
    fun getInt(key: String): Int? {
        return getString(key)?.toIntOrNull()
    }

    /**
     * Get a required integer argument.
     */
    fun getRequiredInt(key: String): Int {
        return getInt(key) ?: throw IllegalArgumentException("Required int argument '$key' not found")
    }

    /**
     * Get a boolean argument by key.
     */
    fun getBoolean(key: String): Boolean? {
        return getString(key)?.toBooleanStrictOrNull()
    }

    /**
     * Get a boolean argument with default value.
     */
    fun getBoolean(key: String, default: Boolean): Boolean {
        return getBoolean(key) ?: default
    }

    companion object {
        /**
         * Create NavArgs from a map of parameters.
         */
        fun from(params: Map<String, String>): NavArgs {
            return NavArgs(pathParams = params)
        }

        /**
         * Empty NavArgs instance.
         */
        val Empty = NavArgs()
    }
}

/**
 * Argument type definitions for navigation.
 * Used when registering routes with the navigation system.
 */
sealed class NavArgType {
    object StringType : NavArgType()
    object IntType : NavArgType()
    object BoolType : NavArgType()
    object LongType : NavArgType()
}

/**
 * Navigation argument definition.
 *
 * @property name Argument name
 * @property type Argument type
 * @property optional Whether the argument is optional
 * @property defaultValue Default value if not provided
 */
data class NavArgDefinition(
    val name: String,
    val type: NavArgType,
    val optional: Boolean = false,
    val defaultValue: String? = null
)
