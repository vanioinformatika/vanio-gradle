package hu.vanio.gradle.dummy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication

class DummyApp

fun main() {
    runApplication<DummyApp>()
}