plugins {
    kotlin("jvm")
    alias(libs.plugins.detekt)
}

dependencies {
    implementation(libs.ksp.api)
    implementation(project(":default-impl-annotations"))
    testImplementation(kotlin("test"))
    testImplementation(libs.compile.testing.ksp)
}
