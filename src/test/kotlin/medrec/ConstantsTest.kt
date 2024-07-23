package medrec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConstantsTest {

    @Test
    fun makeSureApiKeyWorksLikeIThinkItShould() {
        assertEquals(/* expected = */ 39, /* actual = */ Constants.API_KEY.length, /* message = */ "Sanity check on api key failed")
    }
}