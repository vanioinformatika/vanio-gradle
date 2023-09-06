rootProject.name = "vanio-gradle"

includeBuild("plugin") {
    name = "pluginBuild"
}
include(
    "plugin",
    "dummy",
    "dummy:lib",
    "dummy:app"
)
