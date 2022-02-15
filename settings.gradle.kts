rootProject.name = "cross-clans"

include("api", "core", "adapt")

arrayOf(
    "1_16_R3"
).forEach {
    include(":adapt:adapt-v$it")
}