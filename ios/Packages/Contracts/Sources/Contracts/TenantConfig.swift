import Foundation

/// Tenant-specific theme configuration.
public struct TenantTheme: Equatable {
    public let primaryColor: String
    public let secondaryColor: String
    public let backgroundColor: String
    public let logoUrl: String?

    public init(
        primaryColor: String = "#6200EE",
        secondaryColor: String = "#03DAC6",
        backgroundColor: String = "#FFFFFF",
        logoUrl: String? = nil
    ) {
        self.primaryColor = primaryColor
        self.secondaryColor = secondaryColor
        self.backgroundColor = backgroundColor
        self.logoUrl = logoUrl
    }
}

/// Tenant-specific API configuration.
public struct ApiConfig: Equatable {
    public let baseUrl: String
    public let apiKey: String?
    public let timeout: TimeInterval

    public init(
        baseUrl: String = "https://api.example.com",
        apiKey: String? = nil,
        timeout: TimeInterval = 30
    ) {
        self.baseUrl = baseUrl
        self.apiKey = apiKey
        self.timeout = timeout
    }
}

/// Per-tenant configuration for multi-tenant / white-label support.
///
/// Mirrors Android `TenantConfig`. Delivered via `ModuleContext.tenantConfig`.
public struct TenantConfig: Equatable {
    public let tenantId: String
    public let displayName: String

    /// Module IDs enabled for this tenant. Empty = all modules enabled.
    public let enabledModules: [String]
    public let theme: TenantTheme
    public let apiConfig: ApiConfig

    public init(
        tenantId: String,
        displayName: String,
        enabledModules: [String] = [],
        theme: TenantTheme = TenantTheme(),
        apiConfig: ApiConfig = ApiConfig()
    ) {
        self.tenantId = tenantId
        self.displayName = displayName
        self.enabledModules = enabledModules
        self.theme = theme
        self.apiConfig = apiConfig
    }
}
