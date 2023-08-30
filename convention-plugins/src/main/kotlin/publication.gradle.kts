import java.util.Properties

plugins {
    `maven-publish`
    signing
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.key"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.key"] = System.getenv("SIGNING_KEY")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    // Configure maven central repository
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("default-impl")
            description.set("Kotlin Symbol Processing tool for generating default interface implementation constructors")
            url.set("https://github.com/overpas/default-impl")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/overpas/default-impl/blob/main/LICENSE.txt")
                }
            }
            developers {
                developer {
                    id.set("overpas")
                    name.set("Pavel Shurmilov")
                    email.set("pckeycalculator@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:github.com/overpas/default-impl.git")
                developerConnection.set("scm:git:ssh://github.com/overpas/default-impl.git")
                url.set("https://github.com/overpas/default-impl/tree/main")
            }
        }
    }
}

// Signing artifacts. Signing.* extra properties values will be used
signing {
    useInMemoryPgpKeys(
        project.ext["signing.keyId"]?.toString(),
        project.ext["signing.key"]?.toString(),
        project.ext["signing.password"]?.toString(),
    )
    sign(publishing.publications)
}
