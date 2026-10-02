import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

class CommonTest {
    @Test fun first() { assertTrue(true) }
    @Test fun second() { assertTrue(true) }
    @Ignore @Test fun skipped() {}
}
