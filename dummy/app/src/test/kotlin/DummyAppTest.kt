import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals


class DummyAppTest {

    @Test
    fun testDummyResultSuccess() {
        assertEquals("DUMMY", DummyApp().getDummyResult())
    }

}