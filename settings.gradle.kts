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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NotiSiren"
include(":app")
include(":core")
include(":ui-components")
// --- temporarily disabled while verifying the build pattern ---
// include(":shared")
// include(":feature-main")
// include(":feature-filters")
// include(":feature-settings")
// include(":feature-notifications")
// include(":testing")
