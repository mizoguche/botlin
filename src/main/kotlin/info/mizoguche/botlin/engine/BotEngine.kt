package info.mizoguche.botlin.engine

import info.mizoguche.botlin.Pipelines

data class BotEngineId(val value: String)

interface BotEngine {
    val id: BotEngineId
    suspend fun start(pipelines: Pipelines)
    fun stop()
}

interface BotEngineFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotEngine
}
