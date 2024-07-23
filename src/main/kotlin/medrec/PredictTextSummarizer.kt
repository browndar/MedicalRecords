package medrec
import com.google.cloud.aiplatform.v1.*
import com.google.protobuf.Value
import com.google.protobuf.util.JsonFormat
import medrec.model.PromptCache
import mu.KotlinLogging
import java.io.IOException


class PredictTextSummarizer(
    val parameters: String = Constants.MODEL_PARAMS,
    val project: String = Constants.MODEL_PROJECT,
    val location: String = Constants.MODEL_LOCATION,
    val publisher: String = Constants.MODEL_PUBLISHER,
    val model: String = Constants.MODEL_MODEL,
    val cache: PromptCache? = null,
) {
    private val logger = KotlinLogging.logger {}

    @Throws(IOException::class)
    fun predictTextSummarization(
        template: String,
        content: String,
        question: String,
        ) : String? {

        if (cache != null) {
            if (cache.containsKey(content = content, prompt = question)) {
                var responseContent = cache.get(content = content, prompt = question)
                // log cached responseContent
                logger.info { "Cached response: [question = $question], [response = $responseContent]"}

                return responseContent
            }
        }

        val fullPrompt = String.format(template,
            content,
            question)

        val predictTextSummarizer = PredictTextSummarizer()
        val summarization= predictTextSummarizer.predictTextSummarization(instance=fullPrompt)!!

        var prediction = summarization.getPredictions(0)
        val responseContent = prediction.getStructValue().getFieldsOrThrow("content")

        val responseString = responseContent.stringValue
        // log responseContent
        logger.info { "API response to googleai: [question = $question], [response = $responseString]"}

        // cache responseContent
        cache?.put(content, question, responseContent.stringValue)

        return responseContent.stringValue
    }

        // Get summarization from a supported text model
    @Throws(IOException::class)
    fun predictTextSummarization(
        instance: String?
    ): PredictResponse? {
        val endpoint = String.format(Constants.MODEL_ENDPOINT_TEMPLATE, this.location)
        val predictionServiceSettings =
            PredictionServiceSettings.newBuilder()
                .setEndpoint(endpoint)
                .build()

        PredictionServiceClient.create(predictionServiceSettings).use { predictionServiceClient ->
            val endpointName =
                EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model)
            // Use Value.Builder to convert instance to a dynamically typed value that can be
            // processed by the service.
            val instanceValue = Value.newBuilder()
            JsonFormat.parser().merge(instance, instanceValue)
            val instances: MutableList<Value> = ArrayList()
            instances.add(instanceValue.build())

            // Use Value.Builder to convert parameter to a dynamically typed value that can be
            // processed by the service.
            val parameterValueBuilder = Value.newBuilder()
            JsonFormat.parser().merge(parameters, parameterValueBuilder)
            val parameterValue = parameterValueBuilder.build()
            val predictResponse =
                predictionServiceClient.predict(endpointName, instances, parameterValue)

            return predictResponse
        }
    }
}

