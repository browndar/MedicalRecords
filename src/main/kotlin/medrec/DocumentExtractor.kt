package medrec
import medrec.Constants.Companion.CACHE_LOC
import medrec.Constants.Companion.TEMPLATE
import medrec.Constants.Companion.TEMPLATE_RUNNING_ENTITIES
import medrec.Constants.Companion.TEMPLATE_RUNNING_SUMMARY
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import medrec.model.DocumentSummary
import medrec.model.PromptCache
import mu.KotlinLogging
import medrec.utils.Utils

/**
 *
 */
class DocumentExtractor(
    // file to process
    val filePath: String,

    // some documents can probably be broken down into smaller documents the separator is used to mark a new document
    var separator: String = null.toString(),
    // not going to impliment this but instead of just wating until we hit token limit can we give a hint to the
    // extractor about how the document should be broken down?
    var useSeparator: Boolean = false,

    // max tokens to feed into a model
    var maxTokens: Int = 3000,

    // if we are running lots of experiments in a row with repeated similar calls let's not use vertex if we don't have to
    var useCache: Boolean = true,

    var generateTitle: Boolean = true,

    // the various prompts that we are using to extract information from the document

    var summaryPrompt: String = Constants.SUMMARY_PROMPT,
    var summarizeSummariesPrompt: String = Constants.SUMMERIZE_SUMMARIES_PROMPT,
    var entityPrompt: String = Constants.EXTRACT_ENTITIES_PROMPT,
    var generateTitlePrompt: String = Constants.GENERATE_TITLE,
    var outlinePrompt: String = Constants.OUTLINE_PROMPT,


    var entities: HashMap<String, String> = HashMap(),

    var title: String = "Document Title",

    var summary: String = "There is no summary yet.",

    // this will ultimately be the output of the summarization
    var documentSummary: DocumentSummary = DocumentSummary()

) {

    private val logger = KotlinLogging.logger {}

    /**
     * process the document. I am using a two-tiered approach where I build up a document with a limit on the
     * number of tokens that I will send to the model and collect information as I go. When I finish
     * processing these chunks then I will take the summaries collected and build the requested information.
     */
    fun process() {
        var cache: PromptCache? = null
        if (useCache) {
            cache = Utils.INSTANCE.importPromptCache(CACHE_LOC)
        }
        val summarizer = PredictTextSummarizer(cache = cache)
        val contentBuilder = StringBuilder()
        var tokens = 0

        val stream = Utils.INSTANCE.getResourceAsStream(filePath)

        var firstLine: String? = null
        stream.bufferedReader().useLines { lines ->
            lines.forEach { it ->
                if (firstLine == null) firstLine = it
                tokens += Utils.INSTANCE.countTokens(it)
                if (tokens >= maxTokens) {
                    // process current prompt
                    processCurrentPrompt(summarizer, contentBuilder.toString())
                    // after current content processed
                    contentBuilder.clear()
                    tokens = 0
                }
                contentBuilder.append(it)
            }
            processCurrentPrompt(summarizer, contentBuilder.toString())
        }

        // done streaming collect, now build up the information we need from the summary
        summary = summarizer.predictTextSummarization(
            template = TEMPLATE,
            content = summary,
            question = summarizeSummariesPrompt
        )!!
        documentSummary.summary = summary

        if (generateTitle) {
            // title of first line is not super interesting here so let's just pull title from summarization?
            title = summarizer.predictTextSummarization(
                template = TEMPLATE,
                content = summary,
                question = generateTitlePrompt
            )!!
            documentSummary.title = title

        } else {
            documentSummary.title = firstLine.toString()
        }

        documentSummary.entities = entities

        val outlineText = summarizer.predictTextSummarization(
            template = TEMPLATE,
            content = summary,
            question = outlinePrompt
        )

        // these are places that we'll have to add robustness. The model of course will not always give us the format
        // we are looking for. We could accomodate by asking different questions based on the corpus of documents that
        // we are trying to analyze. It all doesn't have to be llm-based. We could pull some stuctured docs out if
        // structure is known to exists. Finally, we can catch json errors and just have empty elements as well.

        if (outlineText != null) {
            try {
                val json = Json.decodeFromString<HashMap<String, List<String>>>(outlineText)
                documentSummary.outline = json
            } catch (e: Exception) {
                logger.warn(e) {"Can't parse hashmap of arrays from [$outlineText], won't save outline" }
            }
        }

        // output and cleanup. If we are actually processing this we could just return the json rather than only logging

        val prettyJson = Json { // this returns the JsonBuilder
            prettyPrint = true
            // optional: specify indent
            prettyPrintIndent = " "
        }

        val jsonString = prettyJson.encodeToString(documentSummary)
        logger.info { "Printing key information about the document" }
        logger.info { "#################################################" }
        logger.info { jsonString }
        logger.info { "#################################################" }


        // write out prompt cache if we used it
        if (useCache) {
            if (cache != null) {
                Utils.INSTANCE.export(CACHE_LOC, cache)
            }
        }
    }

    private fun processCurrentPrompt(summarizer: PredictTextSummarizer, content: String) {
        //perform running summary
        val contentWithSummary = String.format("%s %s", content, TEMPLATE_RUNNING_SUMMARY.format(summary))
        summary = summarizer.predictTextSummarization(
            template = TEMPLATE,
            content = contentWithSummary,
            question = summaryPrompt
        ).orEmpty()

        // track entities
        val contentWithEntities = String.format("%s %s", content, TEMPLATE_RUNNING_ENTITIES.format(entities))
        val newEntities =
            summarizer.predictTextSummarization(template = TEMPLATE, contentWithEntities, entityPrompt)

        if (newEntities != null) {
            try {
                val json = Json.decodeFromString<Map<String, String>>(newEntities)
                entities.putAll(json)
            } catch (e: Exception) {
                logger.warn(e) {"Can't parse hashmap of strings to strings from [$newEntities], won't ad entities" }
            }
        }

        logger.debug { newEntities }
    }


}