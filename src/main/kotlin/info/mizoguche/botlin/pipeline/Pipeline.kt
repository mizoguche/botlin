package info.mizoguche.botlin.pipeline

import info.mizoguche.botlin.BotMessage
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

typealias PipelineInterceptor<TContext> = suspend (TContext) -> Unit
typealias MessageInterceptor = PipelineInterceptor<BotMessage>

class MessagePipelineContext(private val interceptors: List<MessageInterceptor>, val message: BotMessage) {
    suspend fun proceed() {
        interceptors.forEach { it.invoke(message) }
    }
}

class BotMessagePipeline {
    private val messageInterceptors = mutableListOf<MessageInterceptor>()
    val interceptors: List<MessageInterceptor>
        get() = messageInterceptors

    fun intercept(interceptor: MessageInterceptor) {
        messageInterceptors.add(interceptor)
    }

    inline fun execute(message: BotMessage): Job {
        val context = MessagePipelineContext(interceptors, message)
        return launch { context.proceed() }
    }
}