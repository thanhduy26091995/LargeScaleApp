/// Tenant-specific theme configuration.
class TenantTheme {
  final String primaryColor;
  final String secondaryColor;
  final String backgroundColor;
  final String? logoUrl;

  const TenantTheme({
    this.primaryColor = '#6200EE',
    this.secondaryColor = '#03DAC6',
    this.backgroundColor = '#FFFFFF',
    this.logoUrl,
  });
}

/// Tenant-specific API configuration.
class ApiConfig {
  final String baseUrl;
  final String? apiKey;
  final int timeout;

  const ApiConfig({
    this.baseUrl = 'https://api.example.com',
    this.apiKey,
    this.timeout = 30000,
  });
}

/// Per-tenant configuration for multi-tenant / white-label support.
///
/// Mirrors Android [TenantConfig]. Provided via [ModuleContext.tenantConfig].
class TenantConfig {
  final String tenantId;
  final String displayName;

  /// Module IDs enabled for this tenant. Empty = all modules enabled.
  final List<String> enabledModules;
  final TenantTheme theme;
  final ApiConfig apiConfig;

  const TenantConfig({
    required this.tenantId,
    required this.displayName,
    this.enabledModules = const [],
    this.theme = const TenantTheme(),
    this.apiConfig = const ApiConfig(),
  });
}
