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
    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    macosX64()
    macosArm64()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    mingwX64()
    linuxX64()
    linuxArm64()
}

group = properties["lib.group"].toString()
version = properties["lib.version"].toString()

tasks.matching { it.name.endsWith("PublicationToSonatypeRepository") }
    .configureEach {
        dependsOn(tasks.matching { it.name == "signKotlinMultiplatformPublication" })
        dependsOn(tasks.matching { it.name == "signAndroidDebugPublication" })
        dependsOn(tasks.matching { it.name == "signAndroidReleasePublication" })
        dependsOn(tasks.matching { it.name == "signJvmPublication" })
        dependsOn(tasks.matching { it.name == "signIosX64Publication" })
        dependsOn(tasks.matching { it.name == "signIosArm64Publication" })
        dependsOn(tasks.matching { it.name == "signIosSimulatorArm64Publication" })
        dependsOn(tasks.matching { it.name == "signWatchosX64Publication" })
        dependsOn(tasks.matching { it.name == "signWatchosArm32Publication" })
        dependsOn(tasks.matching { it.name == "signWatchosArm64Publication" })
        dependsOn(tasks.matching { it.name == "signWatchosSimulatorArm64Publication" })
        dependsOn(tasks.matching { it.name == "signMacosX64Publication" })
        dependsOn(tasks.matching { it.name == "signMacosArm64Publication" })
        dependsOn(tasks.matching { it.name == "signTvosX64Publication" })
        dependsOn(tasks.matching { it.name == "signTvosArm64Publication" })
        dependsOn(tasks.matching { it.name == "signTvosSimulatorArm64Publication" })
        dependsOn(tasks.matching { it.name == "signMingwX64Publication" })
        dependsOn(tasks.matching { it.name == "signLinuxX64Publication" })
        dependsOn(tasks.matching { it.name == "signLinuxArm64Publication" })
    }
