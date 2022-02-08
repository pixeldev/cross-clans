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
        relocate("com.zaxxer.hikari", "$path.hikari")
        relocate("redis.clients.jedis", "$path.redis")
        relocate("org.jdbi.v3", "$path.jdbi")
        relocate("org.mariadb.jdbc", "$path.mariadb")
        relocate("com.github.benmanes.caffeine", "$path.caffeine")
        relocate("com.google.gson", "$path.gson")
        relocate("io.leangen.geantyref", "$path.geantyref")
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
}

dependencies {
    api(project(":api"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}