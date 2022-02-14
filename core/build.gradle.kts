plugins {
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

tasks {
    shadowJar {
        archiveBaseName.set("crclans")
        archiveVersion.set("${project.version}")
        archiveClassifier.set("")

        val path = "net.cosmogrp.crclans.libs"
        relocate("me.fixeddev.commandflow", "$path.commandflow")
        relocate("me.yushust", "$path.yushust")
        relocate("me.lucko", "$path.lucko")
        relocate("net.kyori", "$path.kyori")
        relocate("com.mojang", "$path.mojang")
        relocate("redis.clients.jedis", "$path.redis")
        relocate("com.google.gson", "$path.gson")
        relocate("com.mongodb", "$path.mongodb")
    }

    processResources {
        filesMatching("**/*.yml") {
            filter<org.apache.tools.ant.filters.ReplaceTokens>(
                "tokens" to mapOf("version" to project.version)
            )
        }
    }
}

repositories {
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    api(project(":api"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
}