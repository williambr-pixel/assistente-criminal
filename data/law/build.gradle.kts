plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sottti.android.app.template.data.law"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.hilt)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.domain.law)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.test.core)
    testImplementation(libs.truth)
}
