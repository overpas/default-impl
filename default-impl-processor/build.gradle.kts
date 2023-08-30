plugins {
    kotlin("multiplatform")
    alias(libs.plugins.detekt)
    id("publication")
}

group = properties["lib.group"].toString()
version = properties["lib.version"].toString()

kotlin {
    jvmToolchain(17)
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.ksp.api)
                implementation(project(":default-impl-annotations"))
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.compile.testing.ksp)
            }
            kotlin.srcDir("src/test/kotlin")
            resources.srcDir("src/test/resources")
        }
    }
}

tasks.matching { it.name.endsWith("PublicationToSonatypeRepository") }
    .configureEach {
        dependsOn(tasks.matching { it.name == "signKotlinMultiplatformPublication" })
        dependsOn(tasks.matching { it.name == "signJvmPublication" })
    }
