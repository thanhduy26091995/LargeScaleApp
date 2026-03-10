pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Large Scale Module"

// App Shell
include(":app")

// Infrastructure Modules
include(":contracts")
include(":wire")
include(":core")
include(":shared-ui")

// Feature Modules
include(":feature-core")
include(":feature-dashboard")
include(":feature-orders")
include(":feature-inventory")
include(":feature-wallet")
 