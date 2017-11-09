package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage

typealias BotMessageHandler = suspend (BotMessage) -> Unit

interface BotEngine {
    suspend fun start(handler: BotMessageHandler)
    fun stop()
}

interface BotEngineFactory<out C : Any, out E : BotEngine> {
    fun create(configure: C.() -> Unit = {}): E
}
