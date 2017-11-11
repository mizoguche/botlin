package info.mizoguche.botlin.engine

import info.mizoguche.botlin.Pipelines

interface BotEngine {
    suspend fun start(pipelines: Pipelines)
    fun stop()
}

interface BotEngineFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotEngine
}
