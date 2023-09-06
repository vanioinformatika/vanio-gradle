tasks.register("greeting") {
    group = "vanio-gradle"
    description = "Plugin alapszintű működését lehet ellenőrizni - üdvözlő szöveget jelenít meg"
    doLast {
        println("Hello from plugin 'vanio-gradle'")
    }
}

tasks.register("showPlugins") {
    group = "vanio-gradle"
    description = "Projectben szereplő plugin-ek listája"
    doLast {
        project.plugins.forEach {
            println("${it.javaClass.name} -> " +
                    "${it.javaClass.getResource(it.javaClass.simpleName+".class")?.toString()?.substringBefore(".jar!")?.substringAfterLast("/")}")
        }
    }

}