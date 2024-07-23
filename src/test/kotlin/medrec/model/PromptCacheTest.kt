package medrec.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import medrec.utils.Utils
import kotlin.test.assertEquals

class PromptCacheTest {

    @Test
    fun testSerializeSmoke() {
        val cache = PromptCache()
        cache.put(content = "c1", prompt = "p1", result = "c1p1")
        cache.put(content = "c1", prompt = "p2", result = "c1p2")
        cache.put(content = "c1", prompt = "p3", result = "c1p3")
        cache.put(content = "c2", prompt = "p1", result = "c2p1")
        cache.put(content = "c2", prompt = "p2", result = "c2p2")
        cache.put(content = "c2", prompt = "p3", result = "c2p3")
        cache.put(content = "c3", prompt = "p1", result = "c3p1")
        cache.put(content = "c3", prompt = "p2", result = "c3p2")
        cache.put(content = "c3", prompt = "p3", result = "c3p3")

        val jsonString = Json.encodeToString(cache)
        println(jsonString)
        val decoded = Json.decodeFromString<PromptCache>(jsonString)

        assertEquals(cache, decoded)
        assertEquals(cache.prompts.size, 3)
        assertEquals(cache.prompts.size, 3)
        assertEquals(cache.cache.size, 3)

        Utils.INSTANCE.export("prompt_cache_smoke.txt", cache)

        val cacheFromDisk = Utils.INSTANCE.importPromptCache("prompt_cache_smoke.txt")

        assertEquals(cache, cacheFromDisk)
    }


}