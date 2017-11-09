package info.mizoguche.botlin.pipeline

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.engine.MessageInterceptor
import info.mizoguche.botlin.engine.MessagePipelineContext
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

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