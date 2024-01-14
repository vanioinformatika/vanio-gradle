rootProject.name = "vanio-gradle"

includeBuild("plugin")
include(
    "dummy",
    "dummy:lib",
    "dummy:app",
    "dummy:appSpring"
)
