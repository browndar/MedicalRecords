package medrec.controller

import medrec.model.PromptCache

class PromptCaching(location: String) {



    var location: String

    var cache: PromptCache = PromptCache()
        get() {
            return field
        }
        set(value) {
            field = value
        }

    init {
        this.location = location


    }

    fun valid(prompts: Set<String>): Boolean {



        return false
    }
}