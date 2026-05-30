// Top-level build file. Plugins are declared here with `apply false` so that
// sub-modules can apply them without re-declaring versions. AGP 9 provides
// built-in Kotlin support, so there is no Kotlin Android plugin here.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
