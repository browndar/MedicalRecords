package medrec

import org.junit.jupiter.api.Test

class DocumentExtractorTest {

    @Test
    fun process() {
        val extractor = DocumentExtractor(filePath = Constants.TEST_DOC_PATH,
            useCache = true,
        )

        extractor.process()


    }
}