plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":default-impl-annotations"))
    ksp(project(":default-impl-processor"))
}
