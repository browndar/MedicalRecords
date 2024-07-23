package medrec.model

import kotlinx.serialization.Serializable

@Serializable
data class DocumentSummary(
    var title: String = "",
    var entities: HashMap<String, String> = HashMap(),
    var summary: String = "",
    var outline: HashMap<String, List<String>> = HashMap(),
)

