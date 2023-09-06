import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.toDuration

/**
 * Test results summarizing
 */
allprojects {
    afterEvaluate {
        tasks.withType(Test::class.java) {
            finalizedBy(testsum)
            addTestListener(object : TestListener {
                override fun beforeSuite(suite: TestDescriptor) {
                    testSuites[suite] = Clock.System.now()
                }
                override fun beforeTest(testDescriptor: TestDescriptor) {
                    val start = testSuites.remove(testDescriptor.parent)
                    if (start != null) {
                        testTetel.add(TestResultData("${this@allprojects.name}:${this@withType.name}", 0, 0, 0, 0, 0, (Clock.System.now() - start).inWholeMilliseconds))
                    }
                }
                override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
                    testTetel.add(TestResultData("${this@allprojects.name}:${this@withType.name}", result.testCount, result.successfulTestCount, result.failedTestCount, result.skippedTestCount, result.endTime - result.startTime, 0, result.resultType))
                }
                override fun afterSuite(suite: TestDescriptor, result: TestResult) {}
            })
        }
    }
}

data class TestResultData(
    val name: String,
    val testCount: Long = 0,
    val successfulTestCount: Long = 0,
    val failedTestCount: Long = 0,
    val skippedTestCount: Long = 0,
    val testTime: Long = 0,
    val contextTime: Long = 0,
    val result: TestResult.ResultType = TestResult.ResultType.SUCCESS
) {
    fun toString(nameLength: Int, numLength: Int) =
        "${name.padEnd(nameLength)} -> " +
                when (result) {
                    TestResult.ResultType.SUCCESS -> ANSI_GREEN
                    TestResult.ResultType.FAILURE -> ANSI_RED
                    TestResult.ResultType.SKIPPED -> ANSI_BLACK
                } +
                "${result.toString().padEnd(10)}${ANSI_RESET_COLOR} " +
                "| ${testCount.toString().padStart(numLength)}" +
                "| ${successfulTestCount.toString().padStart(numLength)}" +
                "| ${failedTestCount.toString().padStart(numLength)}"+
                "| ${skippedTestCount.toString().padStart(numLength)}"+
                "|     ${testTime.durationToString()}" +
                "|     ${contextTime.durationToString()}"+
                "|     ${(testTime + contextTime).durationToString()}|"
}

fun Long.durationToString() =
    this.toDuration(kotlin.time.DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _ -> "${minutes.toString().padStart(2,'0')}:${seconds.toString().padStart(2,'0') }"}

operator fun TestResultData.plus(plus: TestResultData?): TestResultData {
    if (plus == null) return this
    return TestResultData(
        this.name,
        this.testCount + plus.testCount,
        this.successfulTestCount + plus.successfulTestCount,
        this.failedTestCount + plus.failedTestCount,
        this.skippedTestCount + plus.skippedTestCount,
        this.testTime + plus.testTime,
        this.contextTime + plus.contextTime,
        when {
            this.result == TestResult.ResultType.FAILURE || plus.result == TestResult.ResultType.FAILURE -> TestResult.ResultType.FAILURE
            this.result == TestResult.ResultType.SKIPPED || plus.result == TestResult.ResultType.SKIPPED -> TestResult.ResultType.SKIPPED
            else -> TestResult.ResultType.SUCCESS
        }
    )
}

val ANSI_RESET = "\u001B[0m"
val ANSI_RESET_COLOR = "\u001B[39m\u001B[39m"
val ANSI_BOLD_ON = "\u001B[1m"
val ANSI_BOLD_OFF = "\u001B[22m"
val ANSI_BLACK = "\u001B[30m"
val ANSI_RED = "\u001B[31m"
val ANSI_GREEN = "\u001B[32m"

val testTetel = mutableListOf<TestResultData>()
val testSuites = mutableMapOf<TestDescriptor, Instant>()
val testsum = tasks.create("testsum") {
    doLast {
        if (testTetel.isNotEmpty()) {
            val nameLength = testTetel.maxByOrNull { it.name.length }?.name?.length ?: 0
            val numLength = 7
            val padLength = 57 + nameLength + (4 * numLength)
            println(
                ANSI_BOLD_ON + " TEST SUMMARY ".padEnd(padLength / 2 + 7, '-').padStart(padLength, '-') + ANSI_BOLD_OFF
            )
            println(ANSI_BOLD_ON + "Test task".padEnd(nameLength)+" -> Result     |   Tests|  Passed|  Failed| Skipped| Test time|   Context|     Total|" + ANSI_BOLD_OFF)
            println("".padStart(padLength, '-'))
            testTetel.groupingBy { it.name }
                .aggregate { _, accumulator: TestResultData?, element, _ ->
                    element + accumulator
                } // project + test task summarize
                .toSortedMap()
                .values
                .fold(TestResultData("")) { acc, testResult ->
                    println(testResult.toString(nameLength, numLength))
                    testResult + acc
                } // details
                .also { testEredmeny ->
                    println("".padEnd(padLength, '-'))
                    println(ANSI_BOLD_ON + testEredmeny.copy(name = "SUM").toString(nameLength, numLength) + ANSI_RESET)
                } // sum
        }
    }
}
