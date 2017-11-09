package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine

data class BotFeatureId(val value: String)

interface BotFeature {
    val id: BotFeatureId
    fun install(engine: BotEngine)
}

interface BotFeatureFactory<out C : Any, out F : BotFeature> {
    fun create(configure: C.() -> Unit = {}): F
}
