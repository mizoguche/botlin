package info.mizoguche.botlin

import info.mizoguche.botlin.pipeline.BotMessagePipeline

data class BotFeatureId(val value: String)

interface BotFeature {
    val id: BotFeatureId
    fun install(pipeline: BotMessagePipeline)
}

interface BotFeatureFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotFeature
}
