plugins {
    kotlin("jvm")
    alias(libs.plugins.detekt)
    id("publication")
}

group = properties["lib.group"].toString()
version = properties["lib.version"].toString()

dependencies {
    implementation(libs.ksp.api)
    implementation(project(":default-impl-annotations"))
    testImplementation(kotlin("test"))
    testImplementation(libs.compile.testing.ksp)
}
