import java.util.Properties
import java.io.FileInputStream
import java.util.Base64

plugins {
    kotlin("multiplatform") version "1.5.21"
    id("org.jetbrains.dokka") version "1.5.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    `maven-publish`
    signing
}

group = "dev.kdrag0n"
version = "1.0.1"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js {
        nodejs()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    // Read local.properties file first if it exists
    val p = Properties()
    FileInputStream(secretPropsFile).use { ins -> p.load(ins) }
    p.forEach { name, value -> ext[name as String] = value }
} else {
    // Use system environment variables
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    ext["sonatypeStagingProfileId"] = System.getenv("SONATYPE_STAGING_PROFILE_ID")
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.key"] = System.getenv("SIGNING_KEY")
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(rootProject.extra["sonatypeStagingProfileId"] as String?)
            username.set(rootProject.extra["ossrhUsername"] as String?)
            password.set(rootProject.extra["ossrhPassword"] as String?)

            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        artifact(javadocJar)

        pom {
            val githubUrl = "https://github.com/kdrag0n/colorkt"

            name.set("Color.kt")
            description.set("Modern color science library for Kotlin Multiplatform and Java")
            url.set(githubUrl)

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/mit-license.php")
                }
            }
            developers {
                developer {
                    id.set("kdrag0n")
                    name.set("Danny Lin")
                    // Avoid automated scraping
                    email.set(Base64.getDecoder().decode("bWF2ZW5Aa2RyYWcwbi5kZXY=").toString(Charsets.UTF_8))
                }
            }
            scm {
                connection.set("scm:git:$githubUrl.git")
                developerConnection.set("scm:git:ssh://github.com/kdrag0n/colorkt.git")
                url.set(githubUrl)
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        rootProject.extra["signing.keyId"] as String?,
        rootProject.extra["signing.key"] as String?,
        rootProject.extra["signing.password"] as String?
    )

    sign(publishing.publications)
}
