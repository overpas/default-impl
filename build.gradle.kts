import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.detekt) apply false
}

tasks.register("cleanAll", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("detektAll") {
    allprojects {
        this@register.dependsOn(tasks.withType<Detekt>())
    }
}
