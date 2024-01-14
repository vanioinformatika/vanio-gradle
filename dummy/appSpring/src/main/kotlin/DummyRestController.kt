package hu.vanio.gradle.dummy

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DummyRestController {

    @GetMapping("/dummy")
    fun dummy(): String {
        return "DUMMY"
    }
}