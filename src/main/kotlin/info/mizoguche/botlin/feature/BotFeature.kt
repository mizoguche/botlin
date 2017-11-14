package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotPipeline
import info.mizoguche.botlin.Botlin

data class BotFeatureId(val value: String)

class BotFeatureContext(private val featureId: BotFeatureId, private val botlin: Botlin) {
    val pipelines = botlin.pipelines

    inline fun <reified T : Any> pipelineOf(): BotPipeline<T> {
        return pipelines.pipelineOf()
    }

    fun set(content: String) {
        botlin.storage.set(featureId, content)
    }

    fun get(): String? {
        return botlin.storage.get(featureId)
    }
}

interface BotFeature {
    val id: BotFeatureId
    val requiredFeatures: Set<BotFeatureId>
    fun install(context: BotFeatureContext)
}

interface BotFeatureFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotFeature
}
