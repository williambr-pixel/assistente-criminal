plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.kotlin.coroutines.core)
    implementation(libs.javax.inject)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.truth)
}
