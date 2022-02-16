dependencies {
    compileOnly(project(":adapt"))

    implementation("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT") {
        exclude("net.kyori", "adventure-api")
    }

    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}