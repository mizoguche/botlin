package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage
import kotlinx.coroutines.experimental.Job

typealias BotMessageHandler = suspend (BotMessage) -> Unit

interface BotEngine {
    fun intercept(interceptor: MessageInterceptor)
    fun execute(message: BotMessage): Job
    suspend fun start(handler: BotMessageHandler)
    fun stop()
}

interface BotEngineFactory<out C : Any, out E : BotEngine> {
    fun create(configure: C.() -> Unit = {}): E
}
