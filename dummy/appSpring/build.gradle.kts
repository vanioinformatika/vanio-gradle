plugins {
    kotlin("jvm")
    kotlin("plugin.spring") version "2.2.20"
    id("org.springframework.boot") version "3.5.6"
    id("org.springdoc.openapi-gradle-plugin")  version "1.9.0"
    id("hu.vanio.gradle.apiDocs")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.2.1")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform {}
}

val docList = mapOf(
    "http://localhost:8080/api-docs" to "api-docs-grouped.json",
//    "http://localhost:8080/api-docs" to "api-docs-grouped2.json",
)
openApi {
    outputDir.set(file("$buildDir/docs"))
    apiDocsUrl = "http://localhost:8080/api-docs"
    outputFileName = "api-docs.json"
    waitTimeInSeconds.set(60)
//    groupedApiMappings.set(docList)
}
