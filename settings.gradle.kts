pluginManagement {
    repositories {
        mavenCentral()
        google()
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
includeBuild("convention-plugins")
include(":default-impl-processor")
include(":default-impl-annotations")
include(":default-impl-sample")
