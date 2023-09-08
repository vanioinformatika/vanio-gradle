tasks.register("greeting") {
    group = "vanio-gradle"
    description = "Show a welcome message"
    doLast {
        println("Hello from plugin 'vanio-gradle'")
    }
}

tasks.register("showPlugins") {
    group = "vanio-gradle"
    description = "Show the list of plugins included in the project"
    doLast {
        project.plugins.forEach {
            println("${it.javaClass.name} -> " +
                    "${it.javaClass.getResource(it.javaClass.simpleName+".class")?.toString()?.substringBefore(".jar!")?.substringAfterLast("/")}")
        }
    }

}