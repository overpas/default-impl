plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "by.overpass.defaultimpl.sample"
    compileSdk = properties["android.compileSdk"].toString().toInt()
    defaultConfig {
        minSdk = properties["android.minSdk"].toString().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs(
                "src/androidMain/res",
                "src/commonMain/resources",
            )
        }
    }
}

kotlin {
    jvmToolchain(17)
    jvm()
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    mingwX64()
    linuxX64()
    linuxArm64()
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(project(":default-impl-annotations"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":default-impl-processor"))
//    add("kspJvm", project(":default-impl-processor"))
//    add("kspJvmTest", project(":default-impl-processor"))
//    add("kspAndroid", project(":default-impl-processor"))
//    add("kspAndroidTest", project(":default-impl-processor"))
//    add("kspIosX64", project(":default-impl-processor"))
//    add("kspIosX64Test", project(":default-impl-processor"))
//    add("kspIosArm64", project(":default-impl-processor"))
//    add("kspIosArm64Test", project(":default-impl-processor"))
//    add("kspIosSimulatorArm64", project(":default-impl-processor"))
//    add("kspIosSimulatorArm64Test", project(":default-impl-processor"))
//    add("kspLinuxX64", project(":default-impl-processor"))
//    add("kspLinuxX64Test", project(":default-impl-processor"))
//    add("kspLinuxArm64", project(":default-impl-processor"))
//    add("kspLinuxArm64Test", project(":default-impl-processor"))
//    add("kspMingwX64", project(":default-impl-processor"))
//    add("kspMingwX64Test", project(":default-impl-processor"))
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
