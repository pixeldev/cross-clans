dependencies {
    api("org.jetbrains:annotations:22.0.0")

    val storageVersion = "0.6.0"

    api("net.cosmogrp:storage-redis-dist:$storageVersion")
    api("net.cosmogrp:storage-mongo-legacy-dist:$storageVersion")

    arrayOf("trew-dist", "nmessage-dist").forEach {
        api("net.cosmogrp:economy-$it:0.0.1")
    }

    api("me.yushust.inject:core:0.4.5-SNAPSHOT")
    api("me.yushust.message:core:6.0.17")
    api("me.yushust.message:sourcetype-bukkit-yml:6.0.17")
    api("me.fixeddev:commandflow-brigadier:0.5.0-SNAPSHOT")

    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}