pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "default-impl"
include(":default-impl-processor")
include(":default-impl-annotations")
include(":default-impl-sample")
