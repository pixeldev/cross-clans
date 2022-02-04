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
        mavenCentral()
    }
}
