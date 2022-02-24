plugins {
    java
}

subprojects {
    apply(plugin = "java-library")

    tasks {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(16))
            }
        }
    }

    dependencies {
        compileOnly("me.clip:placeholderapi:2.11.1")
    }

    repositories {
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.unnamed.team/repository/unnamed-public/")
        mavenCentral()
    }
}
