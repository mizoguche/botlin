package info.mizoguche.botlin.feature

import info.mizoguche.botlin.Pipelines

data class BotFeatureId(val value: String)

interface BotFeature {
    val id: BotFeatureId
    fun install(pipelines: Pipelines)
}

interface BotFeatureFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotFeature
}
