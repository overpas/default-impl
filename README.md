# default-impl
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.overpas/default-impl-processor/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.overpas/default-impl-processor)
[![CI](https://github.com/overpas/default-impl/actions/workflows/ci.yml/badge.svg)](https://github.com/overpas/default-impl/actions/workflows/ci.yml)
![Badge-Android](https://img.shields.io/badge/platform-android-blue)
![Badge-JVM](https://img.shields.io/badge/platform-jvm-blue)
![Badge-iOS](https://img.shields.io/badge/platform-ios-blue)
![Badge-watchOS](https://img.shields.io/badge/platform-watchos-blue)
![Badge-macOS](https://img.shields.io/badge/platform-macos-blue)
![Badge-tvOS](https://img.shields.io/badge/platform-tvos-blue)
![Badge-mingw](https://img.shields.io/badge/platform-mingw-blue)
![Badge-linux](https://img.shields.io/badge/platform-linux-blue)

A Kotlin Symbol Processing tool generating default interface implementation constructor functions

```kotlin
@DefaultImpl(MyInterfaceImpl::class)
interface MyInterface

internal object MyInterfaceImpl

val obj = MyInterface() // this will be generated with all constructor parameters
```

## Usage
#### In Kotlin Multiplatform projects
In your `build.gradle.kts`:

```gradle
plugins {
    // ...
    id("com.google.devtools.ksp")
}

kotlin {
    // ...
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") // optional: if you want to generate the code in commonMain
            dependencies {
                implementation("io.github.overpas:default-impl-annotations:0.0.2")
            }
        }
        // ...
    }
}

dependencies {
    add("kspCommonMainMetadata", "io.github.overpas:default-impl-processor:0.0.2")
    // other configurations (don't include any if you want to generate the code in commonMain):
    // add("kspJvm", "io.github.overpas:default-impl-processor:0.0.2")
    // add("kspAndroid", "io.github.overpas:default-impl-processor:0.0.2")
    // add("kspIosArm64", "io.github.overpas:default-impl-processor:0.0.2")
    // etc.
}

// optional: if you want to generate the code in commonMain
tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
```
#### In plain Kotlin projects
In your `build.gradle.kts`

```gradle
plugins {
    // ...
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("io.github.overpas:default-impl-annotations:0.0.2")
    ksp("io.github.overpas:default-impl-processor:0.0.2")
}
```

## Troubleshooting
If you are using Detekt and `detektMetadataMain` fails with `Reason: Task ':detektMetadataMain' uses this output of task ':kspCommonMainKotlinMetadata' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.
` you might consider adding:
```gradle
tasks.matching { it.name == "detektMetadataMain" }
    .configureEach {
        dependsOn(tasks.matching { it.name == "kspCommonMainKotlinMetadata" })
    }
```
