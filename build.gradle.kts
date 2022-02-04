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

    repositories {
        mavenLocal()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.unnamed.team/repository/unnamed-public/")
        mavenCentral()
    }
}
