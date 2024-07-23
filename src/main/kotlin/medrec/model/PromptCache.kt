package medrec.model

import kotlinx.serialization.Serializable


@Serializable
data class PromptCache(
    var contents: HashSet<String> = HashSet(),
    var prompts: HashSet<String> = HashSet(),
    var cache: HashMap<String, HashMap<String, String>> = HashMap()) {

    fun put(content: String, prompt: String, result: String) {
        val promptCache = cache.getOrDefault(content, HashMap())
        promptCache[prompt] = result
        cache[content] = promptCache

        contents.add(content)
        prompts.add(prompt)

    }

    fun get(content: String, prompt: String) : String? {
        val promptCache = cache.getOrDefault(content, HashMap())
        return promptCache.get(prompt)
    }

    fun containsKey(content: String, prompt: String) : Boolean {
        if (!(cache.containsKey(content))) {
            return false
        }

        val promptCache = cache.get(content)

        return promptCache!!.containsKey(prompt)
    }

}
