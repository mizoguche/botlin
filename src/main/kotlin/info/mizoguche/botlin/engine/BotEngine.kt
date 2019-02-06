package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotPipelines
import kotlinx.coroutines.CoroutineScope

data class BotEngineId(val value: String)

interface BotEngine {
    val id: BotEngineId
    suspend fun start(botPipelines: BotPipelines)
    fun stop()
}

interface BotEngineFactory<out C : Any> {
    fun create(parentScope: CoroutineScope, configure: C.() -> Unit = {}): BotEngine
}
