package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage
import kotlinx.coroutines.experimental.Job

interface BotEngine {
    fun intercept(interceptor: MessageInterceptor)
    fun execute(message: BotMessage): Job
}

interface BotEngineFactory<out C : Any, out E : BotEngine> {
    fun create(configure: C.() -> Unit = {}): E
}
