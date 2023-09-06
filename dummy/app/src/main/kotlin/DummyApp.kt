class DummyApp {
    fun getDummyResult() = "DUMMY"
}

fun main() {
    println("RESULT: ${DummyApp().getDummyResult()}")
}