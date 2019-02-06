package info.mizoguche.botlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

typealias BotPipelineInterceptor<TContext> = suspend (TContext) -> Unit

class BotPipelineContext<out TContext>(private val interceptors: List<BotPipelineInterceptor<TContext>>, val message: TContext) {
    suspend fun proceed() {
        interceptors.forEach { it.invoke(message) }
    }
}

class BotPipeline<TContext>(private val parentScope: CoroutineScope) {
    private val contextInterceptors = mutableListOf<BotPipelineInterceptor<TContext>>()
    private val interceptors: List<BotPipelineInterceptor<TContext>>
        get() = contextInterceptors

    fun intercept(interceptor: BotPipelineInterceptor<TContext>) {
        contextInterceptors.add(interceptor)
    }

    fun execute(context: TContext): Job {
        val pipelineContext = BotPipelineContext(interceptors, context)
        return parentScope.launch { pipelineContext.proceed() }
    }
}
