package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage

typealias PipelineInterceptor<TContext> = suspend (TContext) -> Unit
typealias MessageInterceptor = PipelineInterceptor<BotMessage>

class SlackEngine {
    private val messageInterceptors = mutableListOf<MessageInterceptor>()
    val interceptors: List<MessageInterceptor>
        get() = messageInterceptors

    fun intercept(interceptor: MessageInterceptor) {
        messageInterceptors.add(interceptor)
    }
}