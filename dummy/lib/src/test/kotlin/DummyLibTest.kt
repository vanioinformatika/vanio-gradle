import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals


class DummyLibTest {

    @Test
    fun testDummyResultSuccess() {
        assertEquals("DUMMY", DummyLib().getDummyResult())
    }

    @Test
    @Ignore
    fun testDummyResultFailure() {
        assertEquals("DUMMYYY", DummyLib().getDummyResult())
    }

}