plugins {
    kotlin("jvm")
    alias(libs.plugins.detekt)
}

dependencies {
    implementation(libs.ksp.api)
    implementation(project(":default-impl-annotations"))
}
