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
        relocate("net.kyori.text", "$path.text")
        relocate("com.mojang", "$path.mojang")
        relocate("redis.clients.jedis", "$path.redis")
        relocate("com.google.gson", "$path.gson")
        relocate("com.mongodb", "$path.mongodb")
        relocate("io.papermc.lib", "$path.paperlib")
        relocate("net.kyori.adventure.text.minimessage", "$path.minimessage")
        relocate("org.bson", "$path.bson")
        relocate("org.apache.commons.pool2", "$path.pool2")
        relocate("org.json", "$path.json")
        relocate("net.cosmogrp.economy", "$path.economy")
        relocate("net.cosmogrp.storage", "$path.storage")
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