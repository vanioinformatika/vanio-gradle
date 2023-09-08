import net.researchgate.release.GitAdapter

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.faendir.gradle.release") version "3.3.1"
}

group = "hu.vanio.gradle"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    mavenCentral()
}

val repoUrl = ""
lateinit var releaseVersion: String

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/vanioinformatika/maven-releases")
            credentials {
                username = System.getenv("GPR_USERNAME")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }
}

release {
    failOnPublishNeeded = false
    failOnCommitNeeded = false
    tagTemplate = "\$name-\$version"
    scmAdapters = listOf(GitAdapter::class.java)
    git {
        requireBranch = null
        pushToRemote = null
    }

}
val publishToMavenLocal by tasks.getting
val publish by tasks.getting
val afterReleaseBuild by tasks.getting
afterReleaseBuild.dependsOn(publish)

tasks.register("install") {
    group = "vanio-gradle"
    dependsOn(tasks.publishToMavenLocal)
    doFirst {
        println("install to local maven repo: ${project.name}:${project.version}")
    }
}

tasks.release {
    group = "vanio-gradle"
    doLast {
        println("library published to: ${repoUrl} : ${project.name}:${releaseVersion}")

    }
}

tasks.beforeReleaseBuild {
    dependsOn("clean")
}

tasks.afterReleaseBuild {
    dependsOn(tasks.publish)
}

tasks.createReleaseTag {
    doLast {
        releaseVersion = project.version.toString()
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kover:0.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}
