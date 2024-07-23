package medrec.utils

import medrec.Constants.Companion.CREDENTIALS_PATH
import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import medrec.model.PromptCache
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets


class Utils private constructor() {
    companion object {
        val INSTANCE: Utils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Utils() }
    }

    /**
     * take a path starting at resources (in either src or test) and read it as a string
      */
    fun getResourceAsText(path: String):String {
        // attempting to just write my first kotlin function but using some java libs
        val stream = object {}.javaClass.getResourceAsStream(path)
        val convertedInputStream = String(stream!!.readAllBytes(), StandardCharsets.UTF_8)
        return convertedInputStream
    }

    fun getResourceAsAbsPath(path: String):String {
        // attempting to just write my first kotlin function but using some java libs
        val uri = object {}.javaClass.getResource(path)!!
        return uri.path
    }

    fun getResourceAsStream(path: String):InputStream {
        // attempting to just write my first kotlin function but using some java libs
        val stream = object {}.javaClass.getResourceAsStream(path)!!
        return stream
    }

    /**
     * exploring different approaches to auth with google apis, went with service token but
     * could use locally defined creds depending on use case.
     */
    fun getCredentials(): Credentials {
        val credsStream = getResourceAsStream(CREDENTIALS_PATH)
        val credentials = GoogleCredentials.fromStream(credsStream)
        return credentials
    }


    fun export(path: String, value: PromptCache) {
        val jsonString = Json.encodeToString(value)
        File(path).writeText(jsonString)
    }

    fun importPromptCache(path: String) : PromptCache {
        val jsonString = File(path).readText()
        return Json.decodeFromString<PromptCache>(jsonString)
    }

    /**
     * given string return number of tokens in that string
     */
    fun countTokens(line: String) : Int {
        return line.trim().split("""\s+""".toRegex()).size
    }

}