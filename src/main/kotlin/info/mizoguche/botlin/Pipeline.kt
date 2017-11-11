package info.mizoguche.botlin

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

typealias PipelineInterceptor<TContext> = suspend (TContext) -> Unit

class PipelineContext<out TContext>(private val interceptors: List<PipelineInterceptor<TContext>>, val message: TContext) {
    suspend fun proceed() {
        interceptors.forEach { it.invoke(message) }
    }
}

class Pipeline<TContext> {
    private val contextInterceptors = mutableListOf<PipelineInterceptor<TContext>>()
    val interceptors: List<PipelineInterceptor<TContext>>
        get() = contextInterceptors

    fun intercept(interceptor: PipelineInterceptor<TContext>) {
        contextInterceptors.add(interceptor)
    }

    fun execute(context: TContext): Job {
        val pipelineContext = PipelineContext(interceptors, context)
        return launch { pipelineContext.proceed() }
    }
}

typealias MessageInterceptor = PipelineInterceptor<BotMessage>
