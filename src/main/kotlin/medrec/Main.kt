package medrec

import mu.KotlinLogging
import java.io.File
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {

    var filePath = Constants.TEST_DOC_PATH

    logger.info { "arguments size ${args.asList().size}" }

    // we only take file as an argument
    if (args.size > 1) {
        logger.error { "arguments count is [${args.size}], it should only be 1" }
        exitProcess(-1)
    }

    // if there's an arg it's the file location
    if (args.size == 1) {
        val fileString = args.get(0)
        val file = File(fileString)
        if (!file.exists()) {
            logger.error { "file [${fileString}] does not exist" }
            exitProcess(-1)
        }
        filePath = file.absolutePath
    }

    val extractor = DocumentExtractor(
        filePath = filePath,
        useCache = false,
    )

    extractor.process()
}