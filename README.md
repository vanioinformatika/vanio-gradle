# Vanio Gradle Plugins
Gradle convention plugin collection.

## List of Plugins
* common
* testReport

## Usage
Configure the plugin repository in ```settings.gradle.kts```
```kotlin
pluginManagement {
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
```
Add the necessary plugin (or plugins) to the ```plugins``` block in the ```build.gradle.kts```
```kotlin
plugins {
    id("hu.vanio.gradle.common") version "0.0.1"
    id("hu.vanio.gradle.testReport") version "0.0.1"
}

```