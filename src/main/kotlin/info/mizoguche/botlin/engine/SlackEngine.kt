package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

typealias PipelineInterceptor<TContext> = suspend (TContext) -> Unit
typealias MessageInterceptor = PipelineInterceptor<BotMessage>

class MessagePipelineContext(val interceptors: List<MessageInterceptor>, val message: BotMessage) {
    suspend fun proceed() {
        interceptors.forEach { it.invoke(message) }
    }
}

class SlackEngine : BotEngine {
    suspend override fun start(handler: BotMessageHandler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val messageInterceptors = mutableListOf<MessageInterceptor>()
    val interceptors: List<MessageInterceptor>
        get() = messageInterceptors

    override fun intercept(interceptor: MessageInterceptor) {
        messageInterceptors.add(interceptor)
    }

    override inline fun execute(message: BotMessage): Job {
        val context = MessagePipelineContext(interceptors, message)
        return launch { context.proceed() }
    }
}