package medrec.utils

import medrec.Constants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue as assertionsAssertTrue

class UtilsTest {
    // seems to be the way we're supposed to declare statics
    companion object {
       const val CHART_NOTES_SINGLE_PATH = "/ChartNotesSingle.txt"
   }

    @Test
    fun getResourceAsText() {
        val fileContent = Utils.INSTANCE.getResourceAsText(CHART_NOTES_SINGLE_PATH)
        assertEquals(3320, fileContent.length, "Should have found content in path")
    }

    @Test
    fun testGetResourcePath() {
        val path = Utils.INSTANCE.getResourceAsAbsPath(Constants.CREDENTIALS_PATH)
        assertionsAssertTrue(/* condition = */ path.endsWith("json"))
    }

    @Test
    fun countTokens() {
        // nothing
        val empty = ""
        assertEquals(1, Utils.INSTANCE.countTokens(empty))

        val one = "one"
        assertEquals(1, Utils.INSTANCE.countTokens(one))

        val five = "   one     two     three      four five"
        assertEquals(5, Utils.INSTANCE.countTokens(five))
    }

}