package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage

typealias PipelineInterceptor<TContext> = suspend (TContext) -> Unit
typealias MessageInterceptor = PipelineInterceptor<BotMessage>

class MessagePipelineContext(private val interceptors: List<MessageInterceptor>, val message: BotMessage) {
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
}