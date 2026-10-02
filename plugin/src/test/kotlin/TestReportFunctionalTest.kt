import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class TestReportFunctionalTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test fun `JVM JS and Wasm are summarized once after all tests`() {
        val project = temporaryFolder.newFolder()
        File(javaClass.getResource("/test-report")!!.toURI()).copyRecursively(project, overwrite = true)

        val taskRows = linkedMapOf(
            ":test" to "report-fixture:test",
            ":multiplatform:jvmTest" to "multiplatform:jvmTest",
            ":multiplatform:jsBrowserTest" to "multiplatform:jsBrowserTest",
            ":multiplatform:wasmJsBrowserTest" to "multiplatform:wasmJsBrowserTest"
        )
        val success = runner(project, "check").build()
        assertSummary(project, success, taskRows)
        val rows = rows(success)
        assertEquals(listOf(1L, 2L, 3L, 4L), taskRows.values.map { rows.getValue(it)[1].toLong() })

        // No replay of previous XML results when Gradle skips up-to-date tasks.
        val upToDate = runner(project, "check").build()
        taskRows.keys.forEach { assertEquals(TaskOutcome.UP_TO_DATE, upToDate.task(it)!!.outcome) }
        assertTrue(rows(upToDate).isEmpty())

        // Ordering constraints must not introduce dependencies on unrelated tests.
        val selected = runner(project, ":multiplatform:jsBrowserTest", "--rerun-tasks").build()
        assertSummary(project, selected, taskRows.filterKeys { it.endsWith(":jsBrowserTest") })
        taskRows.keys.filterNot { it.endsWith(":jsBrowserTest") }.forEach { assertNull(selected.task(it)) }

        // Failed leaf events and --continue still yield exactly one complete final summary.
        val commonTest = File(project, "multiplatform/src/commonTest/kotlin/CommonTest.kt")
        commonTest.writeText(commonTest.readText().replace("assertTrue(true)", "assertTrue(false)"))
        val failure = runner(project, "check", "--continue", "--rerun-tasks").buildAndFail()
        assertSummary(project, failure, taskRows)
        assertEquals(6L, rows(failure).getValue("SUM")[2].toLong())
        assertTrue(cleanOutput(failure).lineSequence().single { it.startsWith("SUM ") }.contains("FAILURE"))
    }

    private fun runner(project: File, vararg tasks: String) = GradleRunner.create()
        .withProjectDir(project)
        .withPluginClasspath()
        .withArguments(*tasks, "--parallel", "--max-workers=2", "--console=plain", "--stacktrace", "--no-configuration-cache")
        .forwardOutput()

    private fun cleanOutput(result: BuildResult) = result.output.replace(Regex("\u001B\\[[0-9;]*m"), "")

    private fun rows(result: BuildResult): Map<String, List<String>> {
        val lines = cleanOutput(result).lineSequence().filter { " -> " in it && "|" in it && !it.startsWith("Test task") }.toList()
        val names = lines.map { it.substringBefore(" -> ").trim() }
        assertEquals("Duplicate summary rows", names.size, names.toSet().size)
        return lines.associate { line ->
            line.substringBefore(" -> ").trim() to line.split('|').drop(1).take(7).map(String::trim)
        }
    }

    private fun assertSummary(project: File, result: BuildResult, taskRows: Map<String, String>) {
        val rows = rows(result)
        assertEquals(taskRows.values.toSet() + "SUM", rows.keys)
        assertEquals(1, Regex(" TEST SUMMARY ").findAll(cleanOutput(result)).count())
        assertEquals(TaskOutcome.SUCCESS, result.task(":testsum")!!.outcome)
        val summaryIndex = result.tasks.indexOfFirst { it.path == ":testsum" }
        val totals = MutableList(4) { 0L }
        taskRows.forEach { (path, name) ->
            assertTrue("$path must finish before testsum", result.tasks.indexOfFirst { it.path == path } in 0 until summaryIndex)
            val segments = path.trimStart(':').split(':')
            val module = segments.dropLast(1).fold(project) { dir, part -> File(dir, part) }
            val xmlFiles = File(module, "build/test-results/${segments.last()}").listFiles { file -> file.extension == "xml" }!!
            assertTrue("Missing XML for $path", xmlFiles.isNotEmpty())
            val counts = MutableList(4) { 0L }
            xmlFiles.forEach { xml ->
                val suite = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml).documentElement
                val tests = suite.getAttribute("tests").toLong()
                val failed = suite.getAttribute("failures").toLong() + suite.getAttribute("errors").toLong()
                val skipped = suite.getAttribute("skipped").toLong()
                listOf(tests, tests - failed - skipped, failed, skipped).forEachIndexed { i, n -> counts[i] += n }
            }
            assertEquals("Counts for $path", counts, rows.getValue(name).take(4).map(String::toLong))
            counts.forEachIndexed { i, n -> totals[i] += n }
            if (path.endsWith("BrowserTest")) {
                assertEquals("Browser context time", "00:00", rows.getValue(name)[5])
                assertEquals(rows.getValue(name)[4], rows.getValue(name)[6])
            }
        }
        assertEquals("SUM must count every leaf exactly once", totals, rows.getValue("SUM").take(4).map(String::toLong))
    }
}
