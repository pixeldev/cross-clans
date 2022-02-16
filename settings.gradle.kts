rootProject.name = "cross-clans"

include("api", "core", "adapt")

arrayOf(
    "1_16_R3", "1_17_R1"
).forEach {
    include(":adapt:adapt-v$it")
}