package medrec

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import medrec.utils.Utils
import kotlin.test.assertEquals


class PredictTextSummarizerTest {

    private val logger = KotlinLogging.logger {}

    @Test
    fun testRun() {
            val instance =
                """
            { "content": "
          
            %s
            
            Q: %s
            A:
            "}
            """.trimIndent()

        val prompt = "Can you extract the key entities such as name, date, and location as a json map?"

            val fullPrompt = String.format(instance,
                Utils.INSTANCE.getResourceAsText("/ChartNotesSingle.txt"),
                prompt)

            val predictTextSummarizer = PredictTextSummarizer()
            val summarization= predictTextSummarizer.predictTextSummarization(instance=fullPrompt)!!

        var prediction = summarization.getPredictions(0)
        val content = prediction.getStructValue().getFieldsOrThrow("content")
println("response: $content.stringValue")

        }

    @Test
    fun predictTextSummarization() {
        val predictTextSummarizer = PredictTextSummarizer(cache = Utils.INSTANCE.importPromptCache("prompt_cache.txt"))
        val template = Constants.TEMPLATE
        val content = Utils.INSTANCE.getResourceAsText("/ChartNotesSingle.txt")
        val question = "Can you extract the key entities such as name, date, and location as a json map?"



        val blah = predictTextSummarizer.predictTextSummarization(template = template, content = content, question = question)

        val blah2 = predictTextSummarizer.predictTextSummarization(template = template, content = content, question = question)

        assertEquals(blah2, blah)

        Utils.INSTANCE.export("prompt_cache.txt",
            predictTextSummarizer.cache!!
        )
    }


}