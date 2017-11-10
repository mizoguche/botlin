package info.mizoguche.botlin.feature

import info.mizoguche.botlin.Pipeline

data class BotFeatureId(val value: String)

interface BotFeature<T> {
    val id: BotFeatureId
    fun install(pipeline: Pipeline<T>)
}

interface BotFeatureFactory<T, out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotFeature<T>
}
