# Vanio Gradle Plugins
Gradle convention plugin collection.

## List of Plugins
* common
* apiDocs
* testReport

## Test report regression check

Run `./gradlew :plugin:test --tests TestReportFunctionalTest` with JDK 21 and
Chrome/Chromium installed (`CHROME_BIN` can specify the executable). The TestKit
fixture uses the repository's Gradle wrapper version (9.1.0) and Kotlin 2.2.20,
matching the dummy projects. Its first run downloads the Kotlin JS/Wasm Node,
Yarn and Karma dependencies.

The check runs real JVM, JS browser and Wasm browser tests across projects with
parallel execution enabled. It compares every summary row and SUM against the
XML reports, checks finalizer ordering, skipped and failed tests, up-to-date
tasks, and a browser-only invocation. Browser test duration comes from Gradle's
test events; the JVM-specific context measurement remains zero for browsers.
`testsum` orders itself after participating test tasks without scheduling extra
tests or changing `check` dependencies.

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
    id("hu.vanio.gradle.common") version "1.1.0"
    id("hu.vanio.gradle.testReport") version "1.1.0"
    id("hu.vanio.gradle.apiDocs") version "1.1.0"
}

```