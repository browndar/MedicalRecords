package medrec.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LocalCredentialsProviderTest {

    @Test
    fun getCredentials() {
        val credsProv = LocalCredentialsProvider()
        val creds = credsProv.credentials

        assertEquals(creds.universeDomain, "googleapis.com")
       // var blah = crets
       // println(blah.toString())
    }
}