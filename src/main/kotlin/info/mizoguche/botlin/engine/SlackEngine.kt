package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage
import kotlinx.coroutines.experimental.launch

typealias PipelineInterceptor<TContext> = suspend (TContext) -> Unit
typealias MessageInterceptor = PipelineInterceptor<BotMessage>

class MessagePipelineContext(val interceptors: List<MessageInterceptor>, val message: BotMessage) {
    suspend fun proceed() {
        interceptors.forEach { it.invoke(message) }
    }
}

class SlackEngine {
    private val messageInterceptors = mutableListOf<MessageInterceptor>()
    val interceptors: List<MessageInterceptor>
        get() = messageInterceptors

    fun intercept(interceptor: MessageInterceptor) {
        messageInterceptors.add(interceptor)
    }

    inline fun execute(message: BotMessage) {
        val context = MessagePipelineContext(interceptors, message)
        launch { context.proceed() }
    }
}