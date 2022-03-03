plugins {
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

tasks {
    shadowJar {
        archiveBaseName.set("crclans-api")
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
}

dependencies {
    api("org.jetbrains:annotations:22.0.0")

    val storageVersion = "0.7.0"

    arrayOf("redis", "mongo-legacy", "gson").forEach {
        api("net.cosmogrp:storage-$it-dist:$storageVersion")
    }

    arrayOf("trew-dist", "nmessage-dist").forEach {
        api("net.cosmogrp:economy-$it:0.0.1")
    }

    api("me.yushust.inject:core:0.4.6-SNAPSHOT")
    api("me.yushust.message:core:6.0.17")
    api("me.yushust.message:sourcetype-bukkit-yml:6.0.17")
    api("me.fixeddev:commandflow-bukkit:0.5.0-SNAPSHOT")

    api("io.papermc:paperlib:1.0.7")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}