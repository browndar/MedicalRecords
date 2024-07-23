package medrec

import medrec.utils.Utils

/**
 * have a bunch of constants here so that we can easily move this logic to configurations
 */
class Constants {
    companion object {
        val API_PATH = "/apiKey.txt"
        val API_KEY = Utils.INSTANCE.getResourceAsText(API_PATH)

        val CREDENTIALS_PATH = "/medrec-429721-b51b01a2770d.json"
        val GOOGLE_APPLICATION_CREDENTIALS_ENV = "GOOGLE_APPLICATION_CREDENTIALS"

        // llm arguments
        val MODEL_PARAMS = """{
  "temperature": 0,
  "maxOutputTokens": 1024,
  "topP": 0,
  "topK": 1
}"""

        val MODEL_PROJECT = "medrec-429721"
        val MODEL_LOCATION = "us-central1"
        val MODEL_PUBLISHER = "google"
        val MODEL_MODEL = "text-bison@001"

        val MODEL_ENDPOINT_TEMPLATE = "%s-aiplatform.googleapis.com:443"

        val TEST_DOC_PATH = "/ChartNotes.txt"
        val CACHE_LOC = "prompt_cache.txt"

        val TEMPLATE =
        """
            { "content": "
          
            %s
            
            Q: %s
            A:
            "}
            """.trimIndent()

        val TEMPLATE_RUNNING_SUMMARY =
            """
            
            Q: We have broken this document up into several prompts. What is the summary of this document thus far?
            A: %s
            
            """.trimIndent()

        val SUMMARY_PROMPT = "Given the summary of the document thusfar and this new content can you update the summary?"

        val SUMMERIZE_SUMMARIES_PROMPT = "Can you summarize the the above document succinctly?"

        val GENERATE_TITLE = "Title the above document?"

        val TEMPLATE_RUNNING_ENTITIES =
            """
            
            Q: We have broken this document up into several prompts. What entities have been extracted thus far?
            A: %s
            
            """.trimIndent()

        val EXTRACT_ENTITIES_PROMPT = "Extract names and values of important entities such as name, date of birth, location in the above document as a flattened json hashmap?"

        val OUTLINE_PROMPT = "Extract a two-tiered outline from this document as json objects where the name is the top tier and the value is a list of strings in the bottom tier"


    }

}