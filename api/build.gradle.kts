plugins {
    id("com.github.johnrengelman.shadow") version("7.1.2")
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