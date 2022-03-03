plugins {
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

tasks {
    shadowJar {
        archiveBaseName.set("crclans")
        archiveVersion.set("${project.version}")
        archiveClassifier.set("")
    }

    processResources {
        filesMatching("**/*.yml") {
            filter<org.apache.tools.ant.filters.ReplaceTokens>(
                "tokens" to mapOf("version" to project.version)
            )
        }
    }

    compileJava {
        options.compilerArgs.add("-parameters")
    }
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    api(project(":api"))

    implementation(project(":adapt"))
//    runtimeOnly(project(":adapt:adapt-v1_16_R3"))
    runtimeOnly(project(":adapt:adapt-v1_17_R1"))

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}