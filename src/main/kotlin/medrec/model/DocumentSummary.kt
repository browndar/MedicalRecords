package medrec.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class DocumentSummary(
    var title: String = "",
    var entities: HashMap<String, String> = HashMap(),
    var summary: String = "",
    var outline: HashMap<String, JsonElement> = HashMap(),
)

