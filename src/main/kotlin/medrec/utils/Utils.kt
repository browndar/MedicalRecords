package medrec.utils

import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import medrec.Constants.Companion.CREDENTIALS_PATH
import medrec.model.PromptCache
import mu.KotlinLogging
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets


class Utils private constructor() {
    companion object {
        val INSTANCE: Utils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Utils() }
    }

    private val logger = KotlinLogging.logger {}


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
        logger.info { "reading file from path [$path]" }

        if (File(path).exists()) {
            logger.info { "reading file from absolute path" }
            return File(path).inputStream()
        } else {
            logger.info { "reading file from classpath" }
            val stream = object {}.javaClass.getResourceAsStream(path)!!
            return stream
        }
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

    val Any?.asAnyMap: AnyMap
        get() {
            val resultMap = mutableMapOf<String, Any>()
            if (this is Map<*, *>) {
                for (item in this) {
                    val key = item.key
                    val value = item.value
                    if (key != null && value != null) {
                        if (key is String) {
                            resultMap[key] = value
                        } else {
                            throw RuntimeException("Expected Map<String,Any> but was Map<${key::class.simpleName},${value::class.simpleName}>")
                        }
                    }
                }
            } else {
                val typeName = if (this == null) "null" else this::class.simpleName
                throw RuntimeException("Expected Map<*,*> but was $typeName")
            }

            return resultMap
        }

    fun deepMerge(destination: AnyMap, source: AnyMap): AnyMap {
        val resultMap = destination.toMutableMap()
        for (key in source.keys) {
            //recursive merge for nested maps
            if (source[key] is Map<*, *> && resultMap[key] is Map<*, *>) {
                val originalChild = resultMap[key].asAnyMap
                val newChild = source[key].asAnyMap
                resultMap[key] = deepMerge(originalChild, newChild)
                //merge for collections
            } else if (source[key] is Collection<*> && resultMap[key] is Collection<*>) {
                if (!(resultMap[key] as Collection<*>).containsAll(source[key] as Collection<*>)) {
                    resultMap[key] = (resultMap[key] as Collection<*>) + (source[key] as Collection<*>)
                }
            } else {
                if (source[key] == null || (source[key] is String && (source[key] as String).isBlank())) continue
                resultMap[key] = source[key] as Any
            }
        }
        return resultMap
    }

    operator fun AnyMap.plus(source: AnyMap): AnyMap {
        return deepMerge(this, source)
    }

}



typealias AnyMap = Map<String, Any>