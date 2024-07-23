package medrec

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val extractor = DocumentExtractor(
        filePath = Constants.TEST_DOC_PATH,
        useCache = true,
    )

    extractor.process()
}