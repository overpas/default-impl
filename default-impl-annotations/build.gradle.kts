plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.detekt)
    id("publication")
}

android {
    namespace = "by.overpass.defaultimpl"
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
}

group = properties["lib.group"].toString()
version = properties["lib.version"].toString()
